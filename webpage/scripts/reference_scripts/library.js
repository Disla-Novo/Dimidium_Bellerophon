//  documentation for Bellerophon commands
// This script adds the interactive demo functionality to library.html

//  MACRO STRUCTURE ------------------------------------------
const commandData = {
  "m-title": {
    label: "M.title",
    description: "Defines the start of a macro with a unique name.",
    example: `M.title "MyMacro"
    Home
M.end`,
    canCompile: false,
  },
  "m-call": {
    label: "M.call",
    description:
      "Calls another macro by name. The called macro must be defined elsewhere.",
    example: `M.title "MainMacro"
    M.call "SubMacro"
M.end`,
    canCompile: false,
  },
  "m-end": {
    label: "M.end",
    description: "Ends the current macro. Every macro must end with M.end.",
    example: `M.title "MyMacro"
    Home
M.end`,
    canCompile: false,
  },

  // ===== MOTION =====
  home: {
    label: "Home",
    description:
      "Homes all axes. Optionally specify axes to home with coordinates.",
    example: `M.title "Home Example"
    Absolute
    Home
M.end`,
  },
  "home-coord": {
    label: "Home with Coordinates",
    description: "Homes specific axes to the given coordinates.",
    example: `M.title "Home with Coordinates"
    Absolute
    Home -> x=0 y=0 z=0
M.end`,
  },
  move: {
    label: "Move",
    description:
      "Moves the print head in a direction by one unit (left | right | center | up | down).",
    example: `M.title "Move Example"
    Absolute
    Home
    Move left
    Move right
    Move center
M.end`,
  },
  moveto: {
    label: "MoveTo",
    description:
      "Moves the print head to absolute coordinates. Supports X, Y, Z, and E axes.",
    example: `M.title "MoveTo Example"
    Absolute
    Home
    MoveTo x=100 y=100 z=0.2
    MoveTo x=150 y=100
    MoveTo x=150 y=150
    MoveTo x=100 y=150
    MoveTo x=100 y=100
M.end`,
  },
  absolute: {
    label: "Absolute",
    description:
      "Sets absolute positioning mode (G90). All coordinates are relative to origin.",
    example: `M.title "Absolute Example"
    Absolute
    Home
    MoveTo x=100 y=100 z=0.2
M.end`,
  },
  relative: {
    label: "Relative",
    description:
      "Sets relative positioning mode (G91). Coordinates are offsets from current position.",
    example: `M.title "Relative Example"
    Relative
    Home
    MoveTo x=10 y=10 z=0.2
M.end`,
  },
  relativeextrusion: {
    label: "RelativeExtrusion",
    description:
      "Sets extrusion to relative mode (M83). E values are increments from current position.",
    example: `M.title "RelativeExtrusion Example"
    RelativeExtrusion
M.end`,
  },
  resetextruder: {
    label: "ResetExtruder",
    description: "Resets the extruder position to zero (G92 E0).",
    example: `M.title "ResetExtruder Example"
    ResetExtruder
M.end`,
  },

  // ===== TEMPERATURE =====
  heat: {
    label: "Heat",
    description:
      "Heats the target (bed | extruder | chamber) to temperature and WAITS until reached.",
    example: `M.title "Heat Example"
    Heat bed=60
    Heat extruder=210
    Heat chamber=50
M.end`,
  },
  "set-heater": {
    label: "Set_Heater_Temperature",
    description:
      "Sets target temperature without waiting (M104/M140). Execution continues immediately.",
    example: `M.title "Set Heater Example"
    Set_Heater_Temperature bed=60
    Set_Heater_Temperature extruder=210
    Set_Heater_Temperature chamber=50
M.end`,
  },
  waitfortemp: {
    label: "WaitForTemp",
    description:
      "Waits until the specified heater reaches its currently set temperature.",
    example: `M.title "WaitForTemp Example"
    Set_Heater_Temperature extruder=210
    WaitForTemp extruder
M.end`,
  },
  cooldown: {
    label: "Cooldown",
    description:
      "Turns off heaters. With target turns off specific heater (extruder | bed | chamber). Without target turns off all heaters.",
    example: `M.title "Cooldown Example"
    Heat bed=60
    Heat extruder=210
    Dwell 2000 ms
    Cooldown bed
    Cooldown extruder
    Cooldown chamber
    Cooldown
    Cooldown all
M.end`,
  },

  // ===== PRINTER CONTROL =====
  setspeed: {
    label: "SetSpeed",
    description: "Sets the movement speed in mm/min for all subsequent moves.",
    example: `M.title "SetSpeed Example"
    Absolute
    Home
    SetSpeed = 1500
    MoveTo x=100 y=100 z=0.2
M.end`,
  },
  setfan: {
    label: "SetFan",
    description:
      "Sets the cooling fan speed. Range depends on firmware (0-255 or 0-100%).",
    example: `M.title "SetFan Example"
    SetFan = 255
M.end`,
  },
  timeout: {
    label: "TIMEOUT_SET",
    description:
      "Sets the idle timeout in seconds. After this period, motors and heaters disable.",
    example: `M.title "Timeout Example"
    TIMEOUT_SET 300
M.end`,
  },
  dwell: {
    label: "Dwell",
    description:
      "Pauses execution for a specified time (s | ms). Defaults to seconds if unit omitted.",
    example: `M.title "Dwell Example"
    Heat bed=60
    Dwell 5000 ms
    Heat extruder=210
    Dwell 2 s
M.end`,
  },
  pause: {
    label: "Pause",
    description: "Pauses macro execution. Can be resumed with Resume.",
    example: `M.title "Pause Example"
    Home
    Pause
    MoveTo x=100 y=100 z=0.2
    Resume
M.end`,
  },
  resume: {
    label: "Resume",
    description: "Resumes a paused macro execution.",
    example: `M.title "Resume Example"
    Home
    Pause
    MoveTo x=100 y=100 z=0.2
    Resume
M.end`,
  },

  // ===== CALIBRATION =====
  level: {
    label: "Level",
    description: "Alias for BedMeshCalibrate. Performs bed mesh calibration.",
    example: `M.title "Level Example"
    Home
    Level
    LoadBedMesh "default"
M.end`,
  },
  bedmesh: {
    label: "BedMeshCalibrate",
    description:
      "Performs bed mesh calibration to map the bed surface for Z compensation.",
    example: `M.title "BedMeshCalibrate Example"
    Home
    BedMeshCalibrate
    LoadBedMesh "default"
M.end`,
  },
  loadbedmesh: {
    label: "LoadBedMesh",
    description: "Loads a saved bed mesh profile by name.",
    example: `M.title "LoadBedMesh Example"
    Home
    Level
    LoadBedMesh "default"
M.end`,
  },
  probe: {
    label: "ProbeCalibrate",
    description:
      "Calibrates the Z-probe offset for accurate first layer height.",
    example: `M.title "ProbeCalibrate Example"
    Home
    ProbeCalibrate
M.end`,
  },
  pressureadvance: {
    label: "SetPressureAdvance",
    description:
      "Sets the pressure advance / linear advance value for extrusion consistency.",
    example: `M.title "Pressure Advance Example"
    SetPressureAdvance 0.04
M.end`,
  },

  // ===== FLOW CONTROL =====
  if: {
    label: "If / Endif",
    description:
      "Conditional block based on temperature checks (extruder | bed | chamber).",
    example: `M.title "If Example"
    Heat extruder=210
    if extruder > 200
        Respond MSG "Extruder is ready!"
    endif
    Cooldown
M.end`,
  },
  endif: {
    label: "endif",
    description: "Ends an if conditional block.",
    example: `M.title "If Example"
    if extruder > 200
        Respond MSG "Ready!"
    endif
M.end`,
    canCompile: false,
  },
  repeat: {
    label: "Repeat",
    description:
      "Repeats a block of code a specified number of times (static).",
    example: `M.title "Repeat Example"
    Absolute
    Home
    repeat 5
        MoveTo x=50 y=50 z=0.2
        MoveTo x=150 y=50
        MoveTo x=150 y=150
        MoveTo x=50 y=150
        MoveTo x=50 y=50
    end
M.end`,
  },
  brepeat: {
    label: "Brepeat",
    description:
      "Dynamic loop with iterator variable i. Supports math expressions.",
    example: `M.title "Brepeat Example"
    Absolute
    SetSpeed = 1500
    Home
    radius = 50
    center_x = 100
    center_y = 100
    Brepeat 360
        pos_x = center_x + radius * cos(i)
        pos_y = center_y + radius * sin(i)
        MoveTo x=pos_x y=pos_y z=0.2
    end
    Home
M.end`,
  },
  end: {
    label: "end",
    description: "Ends a loop or layer block.",
    example: `M.title "End Example"
    repeat 3
        MoveTo x=50 y=50
    end
M.end`,
    canCompile: false,
  },
  respond: {
    label: "Respond MSG",
    description: "Sends a message to the printer console or display.",
    example: `M.title "Respond Example"
    Home
    Respond MSG "Starting print job..."
    Heat bed=60
    Heat extruder=210
    Respond MSG "Heaters at target"
M.end`,
  },

  // ===== EXPRESSIONS (info only) =====
  functions: {
    label: "Math Functions",
    description:
      "Available anywhere an expression is expected: sin(), cos(), tan(), sqrt(), abs(), sign(). Evaluated at compile time.",
    example: `M.title "Functions Example"
    Absolute
    Home
    Brepeat 36
        pos_x = 100 + 50 * cos(i * 10)
        pos_y = 100 + 50 * sin(i * 10)
        MoveTo x=pos_x y=pos_y z=0.2
    end
M.end`,
  },
  constants: {
    label: "Constants",
    description:
      "pi is available globally in any expression. i is the loop iterator, only valid inside Brepeat loops.",
    example: `M.title "Constants Example"
Absolute
    Home
     radius = pi * 10
    offset = pi * 5
    SetSpeed = 1500
    MoveTo x=radius y=offset z=0.2
    Brepeat 5
        MoveTo x=i*5+10 y=i*5+10 z=0.2
    end
M.end`,
  },
  operators: {
    label: "Operators",
    description:
      "Arithmetic: + - * / ^ (power). Assignment operators: = += -= *= /=. All work on variables and coordinates.",
    example: `M.title "Operators Example"
    Absolute
    Home
    base = 50
    offset = 25
    speed = (base + offset) * 2
    SetSpeed = speed
    MoveTo x=base y=base z=0.2
    MoveTo x=base+offset y=base
    MoveTo x=base+offset y=base+offset
    MoveTo x=base y=base+offset
    MoveTo x=base y=base
M.end`,
  },
  //  MACRO STRUCTURE END ------------------------------------------
  //COMPILE-TIME SETTINGS HERE ---------------------------------------------------------------------------
  setnozzle: {
    label: "SetNozzle",
    description:
      "Sets the nozzle diameter (mm) for extrusion calculations. Compile-time only.",
    example: `M.title "SetNozzle Example"
    SetNozzle = 0.6
M.end`,
    canCompile: false,
  },
  setfilament: {
    label: "SetFilament",
    description:
      "Sets the filament diameter (mm) for extrusion calculations. Compile-time only.",
    example: `M.title "SetFilament Example"
    SetFilament = 2.85
M.end`,
    canCompile: false,
  },
  setlayerheight: {
    label: "SetLayerHeight",
    description:
      "Sets the layer height (mm) for extrusion and Layer command. Compile-time only.",
    example: `M.title "SetLayerHeight Example"
    SetLayerHeight = 0.3
M.end`,
    canCompile: false,
  },
  setextrusionmultiplier: {
    label: "SetExtrusionMultiplier",
    description:
      "Adjusts the extrusion multiplier (flow rate). Compile-time only.",
    example: `M.title "SetExtrusionMultiplier Example"
    SetExtrusionMultiplier = 0.95
M.end`,
    canCompile: false,
  },
  enableautoextrude: {
    label: "EnableAutoExtrude",
    description:
      "Enables (1) or disables (0) automatic extrusion calculation. Compile-time only.",
    example: `M.title "EnableAutoExtrude Example"
    EnableAutoExtrude = 1
M.end`,
    canCompile: false,
  },
  layer: {
    label: "Layer",
    description:
      "Repeats a block for the specified number of layers, automatically raising Z. Compile-time only.",
    example: `M.title "Layer Example"
    Absolute
    SetSpeed = 2000
    Layer 10
        MoveTo x=50 y=50
        MoveTo x=150 y=50
        MoveTo x=150 y=150
        MoveTo x=50 y=150
        MoveTo x=50 y=50
    end
    Cooldown
M.end`,
    canCompile: false,
  },

  // ===== FILE EXECUTION =====
  printfile: {
    label: "PRINTFILE",
    description: "Prints a G-code file from the printer's SD card or storage.",
    example: `M.title "PRINTFILE Example"
    Home
    Heat bed=60
    Heat extruder=210
    PRINTFILE "prints/part.gcode"
    Cooldown
M.end`,
  },
  "insertgcode-embed": {
    label: "InsertGCode (Embed)",
    description:
      "Inserts a G-code file directly into the output at compile time. Requires G-code folder set in Settings.",
    example: `M.title "InsertGCode Embed Example"
    Home
    Heat bed=60
    Heat extruder=210
    InsertGCode "part.gcode"
    Cooldown
M.end`,
    canCompile: false,
    needsFolder: true,
  },
  "insertgcode-reference": {
    label: "InsertGCode (Reference)",
    description:
      "Emits a firmware command (M98 for Klipper, M23 for Marlin) to reference a file on the printer's SD card. Does not read the file — no G-code folder needed.",
    example: `M.title "InsertGCode Reference Example"
    Home
    Heat bed=60
    Heat extruder=210
    InsertGCode "part.gcode" reference
    Cooldown
M.end`,
  },
    // added VARIABLES 7/12/26
  "var": {
    label: "var (Global)",
    description: "Declares a global variable that persists across macros. Use 'var' for values that need to be shared between M.title blocks.",
    example: `M.title "Global Variable Example"
    var global_offset = 20
    var feedrate = 1500
    Absolute
    Home
    SetSpeed = feedrate
    MoveTo x=global_offset y=global_offset z=0.2
    M.call "SubMacro"
    M.end

    M.title "SubMacro"
    global_offset = global_offset + 10
    MoveTo x=global_offset y=global_offset z=0.2
    M.end`,
  },
  "assignment": {
    label: "Assignment (Local)",
    description: "Assigns a value to a local variable. Only valid within the current macro. Does not persist across M.call.",
    example: `M.title "Local Assignment Example"
    Absolute
    Home
    pos_x = 50
    pos_y = 50
    MoveTo x=pos_x y=pos_y z=0.2
    pos_x = 150
    MoveTo x=pos_x y=pos_y
    M.end`,
  },
  "axis-assignment": {
    label: "Axis Assignment (Reserved)",
    description: "🔺 x, y, z, and e are reserved axis names. Assigning to them directly will throw a clear error.",
    example: `M.title "Axis Assignment Error"
    Absolute
    // This will throw: "ERROR: 'x' is a reserved axis name"
    x = 10
    MoveTo x=20 y=30
    M.end`,
     canCompile: false,
  },
};

let currentCommandId = null;
let isCompiling = false;

document.addEventListener("DOMContentLoaded", () => {
  const demoInput = document.getElementById("demo-input");
  const demoCompile = document.getElementById("demo-compile");
  const demoStatus = document.getElementById("demo-status");
  const demoOutput = document.getElementById("demo-output-content");

  // cache DOM elements for grammar items
  const grammarItems = document.querySelectorAll(".grammar-item[data-command]");

  grammarItems.forEach((item) => {
    item.addEventListener("click", function (e) {
      if (e.target.closest(".add-btn")) return;
      selectCommand(this.dataset.command);
    });
  });

  function selectCommand(id) {
    const cmd = commandData[id];
    if (!cmd) return;

    currentCommandId = id;

    grammarItems.forEach((el) => {
      el.classList.toggle("active", el.dataset.command === id);
    });

    document.getElementById("demo-title").textContent = cmd.label;
    document.getElementById("demo-description").textContent = cmd.description;
    demoInput.value = cmd.example;

    demoOutput.innerHTML =
      '<span class="placeholder">Compile to see output</span>';
    demoStatus.textContent = "Ready";
    demoStatus.className = "demo-status";

    if (cmd.canCompile === false) {
      demoCompile.style.display = "none";
      demoStatus.textContent = "Info Only";
      demoOutput.innerHTML =
        '<span class="placeholder">This command is informational only and does not produce G-code output on its own.</span>';
    } else {
      demoCompile.style.display = "inline-block";
      demoStatus.textContent = "Ready";
    }
  }

  demoCompile.addEventListener("click", async () => {
    if (isCompiling) return;

    const cmd = commandData[currentCommandId];
    if (cmd && cmd.canCompile === false) {
      demoStatus.textContent = "Info Only";
      return;
    }

    const code = demoInput.value.trim();
    if (!code) {
      demoStatus.textContent = "No code to compile";
      demoStatus.className = "demo-status error";
      return;
    }

    isCompiling = true;
    demoCompile.disabled = true;
    demoStatus.textContent = "Compiling...";
    demoStatus.className = "demo-status";

    try {
      let profile = {
        name: "Default",
        maxX: 220,
        maxY: 220,
        maxZ: 250,
        nozzleDiameter: 0.4,
        filamentDiameter: 1.75,
        layerHeight: 0.2,
        extrusionMultiplier: 1.0,
      };

      const savedProfile = localStorage.getItem("dimidium_profile");
      if (savedProfile) {
        try {
          profile = JSON.parse(savedProfile);
        } catch (e) {}
      }

      const res = await fetch("/compile", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          code,
          mode: "klipper",
          profile,
          gcodeFolder: localStorage.getItem("bellerophon-gcode-folder") || "",
        }),
      });

      const data = await res.json();

      if (data.success) {
        let output = data.output;
        if (output.startsWith("SUCCESS_PAGED:")) {
          output =
            "[Large output — paged to disk. Preview not available here.]";
        }
        demoOutput.textContent = output;
        demoStatus.textContent = "✓ Success";
        demoStatus.className = "demo-status success";
      } else {
        demoOutput.textContent =
          "Error: " + (data.error || "Unknown compiler error");
        demoStatus.textContent = "✗ Compilation failed";
        demoStatus.className = "demo-status error";
      }
    } catch (err) {
      demoOutput.textContent = "Network error: " + err.message;
      demoStatus.textContent = "✗ Connection error";
      demoStatus.className = "demo-status error";
    }

    isCompiling = false;
    demoCompile.disabled = false;
  });

  // Ctrl+Enter to compile [just wanted an extra key thing]
  demoInput.addEventListener("keydown", (e) => {
    if (e.ctrlKey && e.key === "Enter") {
      e.preventDefault();
      demoCompile.click();
    }
  });

  // Auto-select first command
  const firstCmd = document.querySelector(".grammar-item[data-command]");
  if (firstCmd) selectCommand(firstCmd.dataset.command);
});
