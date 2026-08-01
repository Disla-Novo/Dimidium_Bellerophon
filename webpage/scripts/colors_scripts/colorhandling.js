let tokenColors = {};
let currentTheme = "starter";

function escapeHTML(str) {
  return str
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

function getSemanticColor(type) {
  const theme = window.THEME_MAP?.[currentTheme];
  return theme?.semantic?.[type];
}

function resolveTokenColor(tokenName, fallbackColor) {
  if (!tokenName) return fallbackColor;

  if (tokenColors[tokenName]) {
    return tokenColors[tokenName];
  }

  const normalized = tokenName.toUpperCase();
  if (tokenColors[normalized]) {
    return tokenColors[normalized];
  }

  const semanticFallbacks = {
    TITLE: "keyword",
    MEND: "keyword",
    HOME: "keyword",
    MOVE: "keyword",
    HEAT: "keyword",
    LEVEL: "keyword",
    CALL: "keyword",
    IF: "keyword",
    ENDIF: "keyword",
    REPEAT: "keyword",
    END: "keyword",
    BREPEAT: "keyword",
    PAUSE: "keyword",
    RESPOND: "keyword",
    RESUME: "keyword",
    WAITFORTEMP: "keyword",
    COOLDOWN: "keyword",
    SET_SPEED: "keyword",
    SET_FAN: "keyword",
    PRINTFILE: "keyword",
    LOAD_BED_MESH: "keyword",
    BED_MESH_CALIBRATE: "keyword",
    PROBE_CALIBRATE: "keyword",
    SET_PRESSURE_ADVANCE: "keyword",
    RESET_EXTRUDER: "keyword",
    RELATIVEEXTRUSION: "keyword",
    ABSOLUTE: "keyword",
    RELATIVE: "keyword",
    MOVEEX: "keyword",
    STRING: "string",
    NUMBER: "number",
    ID: "variable",
  };

  const semanticType = semanticFallbacks[normalized] || semanticFallbacks[tokenName];
  return getSemanticColor(semanticType) || fallbackColor;
}

async function highlightCode() {
  const input = document.getElementById("code-input");
  const overlay = document.getElementById("code-overlay");
  if (!input || !overlay) return;

  const codeInput = input.value;

  const response = await fetch("/highlight", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ code: codeInput }),
  });

  const tokens = await response.json();

  let html = "";
  let lastIndex = 0;

  tokens.forEach((t) => {
    if (t.start > lastIndex) {
      html += escapeHTML(codeInput.slice(lastIndex, t.start));
    }

    let color = resolveTokenColor(t.name, "#ffffff");

    if (t.name === "ID") {
      let nextIndex = t.end + 1;

      while (nextIndex < codeInput.length && /\s/.test(codeInput[nextIndex])) {
        nextIndex++;
      }

      const nextChar = codeInput[nextIndex] || "";

      let prevIndex = t.start - 1;

      while (prevIndex >= 0 && /\s/.test(codeInput[prevIndex])) {
        prevIndex--;
      }

      const prevChar = codeInput[prevIndex] || "";

      const isAssignmentTarget =
        nextChar === "=" ||
        ["=", "+", "-", "*", "/", "(", ","].includes(prevChar);

      if (isAssignmentTarget) {
        color = getSemanticColor("variable") || "#9cdcfe";
      } else {
        color = resolveTokenColor("ID", "#abb2bf");
      }
    }

    html += `<span style="color:${color}">${escapeHTML(t.text)}</span>`;
    lastIndex = t.end + 1;
  });

  if (lastIndex < codeInput.length) {
    html += escapeHTML(codeInput.slice(lastIndex));
  }

  overlay.innerHTML = html + (codeInput.endsWith("\n") ? " " : "");
  overlay.scrollTop = input.scrollTop;
  overlay.scrollLeft = input.scrollLeft;
}

window.applyTheme = function (themeKey) {
  const theme = window.THEME_MAP?.[themeKey];
  if (!theme) return;

  currentTheme = themeKey;

  tokenColors = { ...theme.tokens };

  document.body.className = document.body.className
    .split(' ')
    .filter(cls => !cls.startsWith('theme-'))
    .join(' ');

  document.body.classList.add(`theme-${themeKey}`);

  let link = document.getElementById("theme-css");

  if (!link) {
    link = document.createElement("link");
    link.id = "theme-css";
    link.rel = "stylesheet";
    document.head.appendChild(link);
  }

  link.href = theme.css;

  localStorage.setItem("bellerophon-theme", themeKey);

  highlightCode();
};

window.addEventListener("DOMContentLoaded", () => {
  const input = document.getElementById("code-input");
  if (!input) return;

  input.addEventListener("input", highlightCode);

  input.addEventListener("scroll", () => {
    const overlay = document.getElementById("code-overlay");
    if (!overlay) return;

    overlay.scrollTop = input.scrollTop;
    overlay.scrollLeft = input.scrollLeft;
  });

  const saved = localStorage.getItem("bellerophon-theme") || "starter";

  const wait = setInterval(() => {
    if (window.THEME_MAP) {
      window.applyTheme(saved);
      clearInterval(wait);
    }
  }, 10);
});