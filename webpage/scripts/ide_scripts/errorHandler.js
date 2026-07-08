// config so far. might leave it at here for now and add more rules later as needed. This is mostly to catch common mistakes and provide better error messages than the compiler can do on its own, especially for things like missing M.end or typos in command names.
// vscode for some reason can predict text like above. interesting feature...ig?
// keeping error handling a bit more simplistic

// some known things that the error handler doesnt deal with
// half statements. like extruder = 60 should be Heat extruder = 60; but the
// error handler just skips it and ignores it. perhaps i could add a check that warns that this line or statement was not converted?
// i kind of give up on error handling for now sinces its mostly a front end thing. the backend already ignores unrecognized tokens.

// Added this helper function
// errorHandler.js - Fixed Version
// using currentMode from mscript hopefully
// Helper function for logging

// security change: added this to prevent potential XSS if the log messages ever include user input. All messages are now treated as plain text.
function addLogMessage(message) {
  const consoleLog = document.getElementById("console-log");
  if (consoleLog) {
    const timestamp = new Date().toLocaleTimeString();
    const entry = document.createElement("div");
    entry.className = "log-entry";
    entry.textContent = `[${timestamp}] ${message}`;
    consoleLog.appendChild(entry);
    consoleLog.scrollTop = consoleLog.scrollHeight;
  }
}

//let currentMode = "klipper";

const keywords = new Set([

  "M.TITLE",
  "M.END",
  "M.CALL",
  "HOME",
  "MOVE",
  "MOVETO",
  "ABSOLUTE",
  "RELATIVE",
  "RELATIVEEXTRUSION",
  "RESETEXTRUDER",
  "HEAT",
  "SET_HEATER_TEMPERATURE",
  "WAITFORTEMP",
  "COOLDOWN",
  "SETSPEED",
  "SETFAN",
  "TIMEOUT_SET",
  "DWELL",
  "PAUSE",
  "RESUME",
  "LEVEL",
  "BEDMESHCALIBRATE",
  "LOADBEDMESH",
  "PROBECALIBRATE",
  "SETPRESSUREADVANCE",
  "IF",
  "ENDIF",
  "REPEAT",
  "BREPEAT",
  "END",
  "RESPOND",
  "SETNOZZLE",
  "SETFILAMENT",
  "SETLAYERHEIGHT",
  "SETEXTRUSIONMULTIPLIER",
  "ENABLEAUTOEXTRUDE",
  "LAYER",
  "PRINTFILE",
  "INSERTGCODE",
  "LEFT",
  "RIGHT",
  "CENTER",
  "UP",
  "DOWN",
  "BED",
  "EXTRUDER",
  "CHAMBER",
  "MSG",
]);

function findClosestKeyword(word) {
  for (const kw of keywords) {
    if (kw.toLowerCase().startsWith(word.toLowerCase())) return kw;
  }
  return null;
}

// Main compile handler
window.addEventListener("DOMContentLoaded", () => {
  const compileBtn = document.getElementById("compile-btn");
  const log = document.getElementById("console-log");
  const input = document.getElementById("code-input");
  const gcodeOutput = document.getElementById("gcode-output");

  if (!compileBtn || !log || !input || !gcodeOutput) {
    console.error("Compile system not initialized correctly");
    return;
  }

  compileBtn.addEventListener("click", async () => {
    // don't trim: the gutter numbers lines off the raw textarea value, so
    // stripping leading blank lines here would shift every reported line
    // number away from what's actually shown next to the code
    const code = input.value;

    if (!code.trim()) {
      logMessage(log, "Build failed: Editor is empty.", "error");
      return;
    }

    gcodeOutput.textContent = "";
    logMessage(
      log,
      `Starting Bellerophon build... [${new Date().toLocaleTimeString()}]`,
      "system",
    );

    try {
      const semanticErrors = await getSemanticErrors(code);
      clearErrorLines();

      if (semanticErrors.length > 0) {
        semanticErrors.forEach((err) => {
          logMessage(
            log,
            `Line ${err.line}: ${err.message}`,
            err.type === "error"
              ? "error"
              : err.type === "warning"
                ? "warning"
                : "hint",
          );
          markErrorLine(err.line, err.type);
        });
        return;
      }
      console.log("Compiling with mode:", window.currentMode);

      let profile;
      const savedProfile = localStorage.getItem("dimidium_profile");
      if (savedProfile) {
        profile = JSON.parse(savedProfile);
      } else {
        const limitXInput = document.getElementById("limit-x");
        const limitYInput = document.getElementById("limit-y");
        const limitZInput = document.getElementById("limit-z");
        profile = {
          name: "Default",
          maxX: limitXInput ? parseFloat(limitXInput.value) || 220 : 220,
          maxY: limitYInput ? parseFloat(limitYInput.value) || 220 : 220,
          maxZ: limitZInput ? parseFloat(limitZInput.value) || 250 : 250,
          nozzleDiameter: 0.4,
          filamentDiameter: 1.75,
          layerHeight: 0.2,
          extrusionMultiplier: 1.0,
        };
      }

      console.log("=== FRONTEND DEBUG ===");
      console.log("Mode:", window.currentMode);
      console.log("Profile object:", profile);
      console.log("Profile maxX:", profile.maxX);
      console.log("Profile maxY:", profile.maxY);
      console.log("Profile maxZ:", profile.maxZ);

      // ADDEDDDDDDD 4/8/2026
      const gcodeFolder = localStorage.getItem("bellerophon-gcode-folder") || "";

      const payload = {
        code: code,
        mode: window.currentMode,
        profile: profile,
        gcodeFolder: gcodeFolder,
      };

      const res = await fetch("/compile", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      });

      if (!res.ok) throw new Error(`Compiler returned HTTP ${res.status}`);

      const data = await res.json();

      if (data.success) {
        if (data.output.startsWith("SUCCESS_PAGED:")) {
          const filePath = data.output.replace("SUCCESS_PAGED:", "");
          gcodeOutput.innerHTML = `<div class="paged-output-msg">
                        <strong>Industrial Build Complete</strong><br>
                        G-Code is too large for preview (~${(code.length / 1024).toFixed(0)} KB source).<br>
                        Cached at: <code>${filePath}</code>
                    </div>`;
          logMessage(
            log,
            "Build finished. Memory Paging active: Streamed to local disk.",
            "system",
          );
        } else {
          gcodeOutput.innerHTML = window.highlightGCode(data.output);
          logMessage(
            log,
            "Build finished. Successfully compiled to " +
              window.currentMode.toUpperCase() +
              " format",
            "system",
          );
        }
      }
      // added this here
      else if (Array.isArray(data.errors)) {
        data.errors.forEach((err) => {
          logMessage(log, `Line ${err.line}: ${err.message}`, "error");
          markErrorLine(err.line);
        });
      } else {
        logMessage(
          log,
          `Build failed: ${data.error || "Unknown compiler error"}`,
          "error",
        );
      }
    } catch (err) {
      logMessage(log, `Network error: ${err.message}`, "error");
    }
    log.scrollTop = log.scrollHeight;
  });
});

function logMessage(logElement, text, type = "system") {
  const entry = document.createElement("div");
  entry.className = `log-entry ${type}`;
  entry.textContent = `> ${text}`;
  logElement.appendChild(entry);
}

function markErrorLine(lineNumber, type = "error") {
  const gutter = document.getElementById("line-numbers");
  if (!gutter) return;

  const lines = gutter.children;
  const index = lineNumber - 1;
  if (index < 0 || index >= lines.length) return;

  lines[index].classList.remove("error", "warning", "hint");
  lines[index].classList.add("semantic-error-line", type);
}

function clearErrorLines() {
  document
    .querySelectorAll(".semantic-error-line")
    .forEach((el) => el.classList.remove("semantic-error-line"));
}

async function getSemanticErrors(code) {
  // 4/10/2026 added this to really give a check
  if (code.length > 100000) {
    logMessage(
      document.getElementById("console-log"),
      "Large Script Detected! Skipping real-time syntax highlighting for performance.",
      "system",
    );
    return [];
  }

  try {
    const res = await fetch("/highlight", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ code }),
    });

    if (!res.ok) {
      console.warn("Highlight endpoint failed, skipping semantic errors");
      return [];
    }

    const tokens = await res.json();
    const errors = [];
    const lines = code.split("\n");

    function checkMacroBoundaries(lines) {
      const macroErrors = [];
      let insideMacro = false;
      let macroStartLine = 0;

      lines.forEach((line, idx) => {
        const trimmed = line.trim();
        const lineNum = idx + 1;

        if (!trimmed || trimmed.startsWith("#")) return;

        if (/^M\.title\b/i.test(trimmed)) {
          if (insideMacro) {
            macroErrors.push({
              line: macroStartLine,
              message:
                "Previous macro must end with M.end before starting a new macro",
              type: "error",
            });
          }
          insideMacro = true;
          macroStartLine = lineNum;
          return;
        }

        if (/^M\.end\b/i.test(trimmed)) {
          if (!insideMacro) {
            macroErrors.push({
              line: lineNum,
              message: "M.end found without a matching M.title",
              type: "error",
            });
          }
          insideMacro = false;
          return;
        }

        if (!insideMacro) {
          macroErrors.push({
            line: lineNum,
            message:
              "Command found outside of a macro. Each macro must start with M.title and end with M.end",
            type: "error",
          });
        }
      });

      if (insideMacro) {
        macroErrors.push({
          line: macroStartLine,
          message: "Macro must end with M.END",
          type: "error",
        });
      }

      return macroErrors;
    }

    errors.push(...checkMacroBoundaries(lines));

    lines.forEach((line, idx) => {
      const trimmed = line.trim();
      const lineNum = idx + 1;

      if (!trimmed || trimmed.startsWith("#")) return;

      if (/^M\.title\b/i.test(trimmed) && !/"[^"]+"/.test(trimmed)) {
        errors.push({
          line: lineNum,
          message: 'M.TITLE requires a string name (e.g. M.title "MyMacro")',
          type: "error",
        });
      }
    });

    function levenshtein(a, b) {
      const dp = Array.from({ length: a.length + 1 }, () => []);
      for (let i = 0; i <= a.length; i++) dp[i][0] = i;
      for (let j = 0; j <= b.length; j++) dp[0][j] = j;

      for (let i = 1; i <= a.length; i++) {
        for (let j = 1; j <= b.length; j++) {
          if (a[i - 1].toLowerCase() === b[j - 1].toLowerCase()) {
            dp[i][j] = dp[i - 1][j - 1];
          } else {
            dp[i][j] = Math.min(dp[i - 1][j - 1], dp[i][j - 1], dp[i - 1][j]) + 1;
          }
        }
      }
      return dp[a.length][b.length];
    }

    function findClosestKeyword(word) {
      let closest = null;
      let minDist = Infinity;
      for (const kw of keywords) {
        const dist = levenshtein(word, kw);
        if (dist < minDist && dist <= Math.max(2, Math.floor(kw.length / 3))) {
          minDist = dist;
          closest = kw;
        }
      }
      return closest;
    }

    lines.forEach((line, idx) => {
      const lineNum = idx + 1;
      const seenAxes = new Set();
      const trimmed = line.trim();
      if (!trimmed || trimmed.startsWith("#")) return;

      if (/^\w+\s*=\s*\d+/.test(trimmed)) {
        const firstWord = trimmed.split(/\s|=/)[0].toUpperCase();
        const originalWord = trimmed.split(/\s|=/)[0];

        if (["EXTRUDER", "BED", "FAN"].includes(firstWord)) {
          errors.push({
            line: lineNum,
            message: `Hint: You defined a variable named '${originalWord}'. If you meant to control hardware, make sure to use a command like 'Heat extruder = ...' or 'SetFan = ...'`,
            type: "hint",
          });
        }
      }

      tokens.forEach((t) => {
        const tokenLine = code.slice(0, t.start).split("\n").length;
        if (tokenLine !== lineNum) return;

        const text = t.text.trim();

        if (t.name === "UNRECOGNIZED") {
          const suggestion = findClosestKeyword(text);
          let message = `Unrecognized token: ${text}`;
          if (suggestion) {
            message += ` → Did you mean "${suggestion}"?`;
          }
          errors.push({
            line: lineNum,
            message: message,
            type: "error",
          });
        }

        // added half statement for this 4/10/2026

        // changed the location

if (t.name === "ID") {
  const upperText = text.toUpperCase();
  const trimmedLine = lines[lineNum - 1].trim();
  const isAssignment = /^\w+\s*=\s*/.test(trimmedLine);
  const lineHasEquals = trimmedLine.includes("=");
  const tokenIsFirstWord = trimmedLine.startsWith(text);

  if (["X", "Y", "Z", "E"].includes(upperText)) {
    if (seenAxes.has(upperText)) {
      errors.push({
        line: lineNum,
        message: `Duplicate axis in line: ${text}`,
        type: "warning",
      });
    } else {
      seenAxes.add(upperText);
    }
  } else if (!isAssignment && !(lineHasEquals && !tokenIsFirstWord) && !keywords.has(upperText)) {
    const suggestion = findClosestKeyword(text);
    let message = `Unknown command: ${text}`;
    if (suggestion) {
      message += ` → Did you mean "${suggestion}"?`;
    }
    errors.push({
      line: lineNum,
      message: message,
      type: "error",
    });
  }
}
      });
    });

    return errors;
  } catch (err) {
    console.warn("Error getting semantic errors:", err);
    return [];
  }
}