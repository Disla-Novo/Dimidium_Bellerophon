// persistence.js
// Single source of truth for app state, backed by /state on the local server.

(function () {
  "use strict";

  const STATE_ENDPOINT = "/state";
  const RESET_ENDPOINT = "/state/reset";
  const SCHEMA_VERSION = 1;
  const SAVE_DEBOUNCE_MS = 500;
  const LEGACY_MIGRATION_FLAG = "bellerophon_migrated_to_state";

  
  const FORBIDDEN_KEYS = new Set(["__proto__", "constructor", "prototype"]);
  function isSafePath(parts) {
    for (const p of parts) {
      if (FORBIDDEN_KEYS.has(p)) return false;
    }
    return true;
  }

  function emptyState() {
    return {
      version: SCHEMA_VERSION,
      updatedAt: new Date().toISOString(),
      data: {
        editor: {},
        profile: {},
        references: {},
        gcode: {},
        gravity: {},
        ui: {},
        theme: {},
        cfgGenerator: {},
      },
    };
  }

  let state = null;
  try {
    const xhr = new XMLHttpRequest();
    xhr.open("GET", STATE_ENDPOINT, false);
    xhr.send();
    if (xhr.status === 200 && xhr.responseText) {
      const parsed = JSON.parse(xhr.responseText);
      if (parsed && typeof parsed === "object") state = parsed;
    }
  } catch (e) {
    console.warn("[Persistence] Could not load state:", e);
  }
  if (!state) state = emptyState();
  if (!state.data || typeof state.data !== "object") state.data = {};
  if (!state.version) state.version = SCHEMA_VERSION;

  function migrateLegacyKeys() {
    if (localStorage.getItem(LEGACY_MIGRATION_FLAG) === "1") return;

    const setIfMissing = (section, key, value) => {
      if (value === null || value === undefined || value === "") return;
      if (FORBIDDEN_KEYS.has(section) || FORBIDDEN_KEYS.has(key)) return;
      if (!state.data[section]) state.data[section] = {};
      if (state.data[section][key] === undefined) {
        state.data[section][key] = value;
      }
    };

    try {
      setIfMissing(
        "editor",
        "content",
        localStorage.getItem("bellerophon_editor_content"),
      );

      const profile = localStorage.getItem("dimidium_profile");
      if (profile)
        try {
          setIfMissing("profile", "values", JSON.parse(profile));
        } catch (e) {}

      const refs = localStorage.getItem("jupitoreRefs");
      if (refs)
        try {
          setIfMissing("references", "saved", JSON.parse(refs));
        } catch (e) {}

      setIfMissing(
        "gcode",
        "folder",
        localStorage.getItem("bellerophon-gcode-folder"),
      );

      const files = localStorage.getItem("bellerophon-gcode-files");
      if (files)
        try {
          setIfMissing("gcode", "files", JSON.parse(files));
        } catch (e) {}

      const groups = localStorage.getItem("gravity_groups");
      if (groups)
        try {
          setIfMissing("gravity", "groups", JSON.parse(groups));
        } catch (e) {}

      const height = localStorage.getItem("jupitore-console-height");
      if (height) {
        const n = parseInt(height, 10);
        if (!isNaN(n)) setIfMissing("ui", "consoleHeight", n);
      }

      setIfMissing(
        "ui",
        "firmware",
        sessionStorage.getItem("bellerophon_target"),
      );

      setIfMissing(
        "theme",
        "current",
        localStorage.getItem("bellerophon-theme"),
      );

      const cfgSession = localStorage.getItem("dimidium_session");
      if (cfgSession)
        try {
          setIfMissing("cfgGenerator", "session", JSON.parse(cfgSession));
        } catch (e) {}

      console.log("[Persistence] Legacy state folded into state.json.");
      scheduleSave();
    } catch (e) {
      console.warn("[Persistence] Migration error:", e);
    }
  }

  function clearLegacyKeys() {
    [
      "bellerophon_editor_content",
      "dimidium_profile",
      "jupitoreRefs",
      "bellerophon-gcode-folder",
      "bellerophon-gcode-files",
      "gravity_groups",
      "jupitore-console-height",
      "bellerophon-theme",
      "dimidium_session",
      "bellerophon_demo_code",
    ].forEach((k) => localStorage.removeItem(k));
    sessionStorage.removeItem("bellerophon_target");
  }

  const Persistence = {
    get(path, fallback) {
      if (!path) return state.data;
      const parts = path.split(".");
      if (!isSafePath(parts)) return fallback;
      let node = state.data;
      for (const part of parts) {
        if (node == null || typeof node !== "object") return fallback;
        if (!Object.prototype.hasOwnProperty.call(node, part)) return fallback;
        node = node[part];
      }
      return node === undefined ? fallback : node;
    },

    set(path, value) {
      const parts = path.split(".");
      if (!isSafePath(parts)) {
        console.warn("[Persistence] Refused unsafe path:", path);
        return;
      }
      let node = state.data;
      for (let i = 0; i < parts.length - 1; i++) {
        const part = parts[i];
        if (
          !Object.prototype.hasOwnProperty.call(node, part) ||
          !node[part] ||
          typeof node[part] !== "object"
        ) {
          node[part] = {};
        }
        node = node[part];
      }
      node[parts[parts.length - 1]] = value;
      scheduleSave();
    },

    remove(path) {
      const parts = path.split(".");
      if (!isSafePath(parts)) {
        console.warn("[Persistence] Refused unsafe path:", path);
        return;
      }
      let node = state.data;
      for (let i = 0; i < parts.length - 1; i++) {
        if (!Object.prototype.hasOwnProperty.call(node, parts[i])) return;
        node = node[parts[i]];
      }
      delete node[parts[parts.length - 1]];
      scheduleSave();
    },

    snapshot() {
      return JSON.parse(JSON.stringify(state));
    },

    async reset() {
      try {
        const res = await fetch(RESET_ENDPOINT, { method: "POST" });
        const data = await res.json();
        if (data && data.success) {
          state = emptyState();
          return true;
        }
        return false;
      } catch (e) {
        console.warn("[Persistence] Reset failed:", e);
        return false;
      }
    },

    flush() {
      return saveNow();
    },
  };

  let saveTimer = null;
  let saving = false;
  let pendingSave = false;
  let hasSavedOnce = false;

  function scheduleSave() {
    if (saveTimer) clearTimeout(saveTimer);
    saveTimer = setTimeout(saveNow, SAVE_DEBOUNCE_MS);
  }

  async function saveNow() {
    if (saveTimer) {
      clearTimeout(saveTimer);
      saveTimer = null;
    }
    if (saving) {
      pendingSave = true;
      return;
    }
    saving = true;
    try {
      const res = await fetch(STATE_ENDPOINT, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(state),
      });
      if (!res.ok) {
        console.warn("[Persistence] Save failed: HTTP " + res.status);
      } else if (!hasSavedOnce) {
        hasSavedOnce = true;
        clearLegacyKeys();
        localStorage.setItem(LEGACY_MIGRATION_FLAG, "1");
      }
    } catch (e) {
      console.warn("[Persistence] Save failed:", e);
    } finally {
      saving = false;
      if (pendingSave) {
        pendingSave = false;
        scheduleSave();
      }
    }
  }

  window.addEventListener("beforeunload", () => {
    try {
      const blob = new Blob([JSON.stringify(state)], {
        type: "application/json",
      });
      navigator.sendBeacon(STATE_ENDPOINT, blob);
    } catch (e) {}
  });

  migrateLegacyKeys();
  window.Persistence = Persistence;
})();
