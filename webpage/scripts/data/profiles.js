

(function () {
  "use strict";

  const PROFILES_KEY = "profile.profiles";
  const ACTIVE_KEY = "profile.activeId";

  const DEFAULT_PROFILE = {
    name: "Default",
    maxX: 220,
    maxY: 220,
    maxZ: 250,
    nozzleDiameter: 0.4,
    filamentDiameter: 1.75,
    layerHeight: 0.2,
    extrusionMultiplier: 1.0,
  };

  function allProfiles() {
    return Persistence.get(PROFILES_KEY, {});
  }

  function saveProfiles(map) {
    Persistence.set(PROFILES_KEY, map);
  }

  function newId() {
    return crypto.randomUUID
      ? crypto.randomUUID()
      : "p_" + Date.now() + "_" + Math.random().toString(36).slice(2);
  }

  function normalizeName(name) {
    return (name == null ? "" : String(name)).trim();
  }

  function namesEqual(a, b) {
    return normalizeName(a).toLowerCase() === normalizeName(b).toLowerCase();
  }

  function existsByName(name, excludeId) {
    const target = normalizeName(name);
    if (!target) return false;
    const map = allProfiles();
    for (const id of Object.keys(map)) {
      if (id === excludeId) continue;
      if (namesEqual(map[id].name, target)) return true;
    }
    return false;
  }

  function nextDefaultName() {
    const map = allProfiles();
    let max = 0;
    for (const id of Object.keys(map)) {
      const m = /^default_(\d+)$/i.exec(map[id].name || "");
      if (m) max = Math.max(max, parseInt(m[1], 10));
    }
    return "default_" + (max + 1);
  }

  function list() {
    const map = allProfiles();
    return Object.keys(map)
      .map((id) => ({ id, name: map[id].name }))
      .sort((a, b) =>
        a.name.localeCompare(b.name, undefined, { sensitivity: "base" }),
      );
  }

  function getActiveId() {
    const id = Persistence.get(ACTIVE_KEY, null);
    const map = allProfiles();
    if (id && map[id]) return id;
    const ids = Object.keys(map);
    if (ids.length > 0) {
      Persistence.set(ACTIVE_KEY, ids[0]);
      return ids[0];
    }
    return null;
  }

  function getActive() {
    const id = getActiveId();
    if (!id) return null;
    const p = allProfiles()[id];
    return p ? Object.assign({ id: id }, p) : null;
  }

  function setActive(id) {
    const map = allProfiles();
    if (!map[id]) return false;
    Persistence.set(ACTIVE_KEY, id);
    return true;
  }

  function create(name) {
    let finalName = normalizeName(name);
    if (!finalName) finalName = nextDefaultName();
    if (existsByName(finalName, null)) return null;

    const id = newId();
    const map = allProfiles();
    map[id] = Object.assign({}, DEFAULT_PROFILE, { name: finalName });
    saveProfiles(map);

    if (!Persistence.get(ACTIVE_KEY, null)) {
      Persistence.set(ACTIVE_KEY, id);
    }
    return id;
  }

  function rename(id, newName) {
    const map = allProfiles();
    if (!map[id]) return false;
    const finalName = normalizeName(newName);
    if (!finalName) return false;
    if (namesEqual(map[id].name, finalName)) return true;
    if (existsByName(finalName, id)) return false;
    map[id].name = finalName;
    saveProfiles(map);
    return true;
  }

  function duplicate(id) {
    const map = allProfiles();
    if (!map[id]) return null;
    const source = map[id];
    let candidate = source.name + " (copy)";
    let n = 1;
    while (existsByName(candidate, null)) {
      n += 1;
      candidate = source.name + " (copy " + n + ")";
    }
    const newId_ = newId();
    map[newId_] = Object.assign({}, source, { name: candidate });
    saveProfiles(map);
    return newId_;
  }

  function update(id, values) {
    const map = allProfiles();
    if (!map[id]) return false;
    map[id] = Object.assign({}, map[id], values);
    saveProfiles(map);
    return true;
  }

  function remove(id) {
    const map = allProfiles();
    if (!map[id]) return false;
    if (Object.keys(map).length <= 1) return false;
    delete map[id];
    saveProfiles(map);

    if (Persistence.get(ACTIVE_KEY, null) === id) {
      const remaining = Object.keys(map);
      Persistence.set(ACTIVE_KEY, remaining[0]);
    }
    return true;
  }

  function ensureDefault() {
    if (list().length === 0) {
      create("Default");
    }
  }

  window.Profiles = {
    DEFAULT_PROFILE: DEFAULT_PROFILE,
    list: list,
    getActive: getActive,
    getActiveId: getActiveId,
    setActive: setActive,
    create: create,
    rename: rename,
    duplicate: duplicate,
    update: update,
    remove: remove,
    existsByName: existsByName,
    ensureDefault: ensureDefault,
  };
})();