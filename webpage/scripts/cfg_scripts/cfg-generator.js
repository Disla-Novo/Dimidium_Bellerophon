const boards = {
  "skr-mini-e3-v3": {
    name: "SKR Mini E3 V3.0",
    x_step: "PB13",
    x_dir: "!PB12",
    x_enable: "!PB14",
    x_endstop: "^PC0",
    y_step: "PB10",
    y_dir: "!PB2",
    y_enable: "!PB11",
    y_endstop: "^PC1",
    z_step: "PB0",
    z_dir: "PC5",
    z_enable: "!PB1",
    z_endstop: "^PC2",
    uart_pin: "PC11",
    tx_pin: "PC10",
    has_uart_global: true,
    needs_standby_pins: false,
  },
  "skr-e3-turbo": {
    name: "SKR E3 Turbo",
    x_step: "P1.4",
    x_dir: "P1.8",
    x_enable: "P1.0",
    x_endstop: "^P1.29",
    y_step: "P1.14",
    y_dir: "P1.15",
    y_enable: "P1.9",
    y_endstop: "^P1.28",
    z_step: "P4.29",
    z_dir: "P4.28",
    z_enable: "P1.16",
    z_endstop: "^P1.27",
    e_step: "P2.11",
    e_dir: "P2.12",
    e_enable: "P0.21",
    uart_pin_x: "P1.1",
    uart_addr_x: 0,
    uart_pin_y: "P1.10",
    uart_addr_y: 1,
    uart_pin_z: "P1.17",
    uart_addr_z: 2,
    uart_pin_e: "P1.25",
    uart_addr_e: 4,
    needs_standby_pins: true,
    standby_pins: "!P3.26, !P3.25, !P1.18, !P1.19, !P2.13",
  },
  "creality-4.2.2": {
    name: "Creality 4.2.2",
    x_step: "PC2",
    x_dir: "PB9",
    x_enable: "!PC3",
    x_endstop: "^PA5",
    y_step: "PB8",
    y_dir: "PB7",
    y_enable: "!PB6",
    y_endstop: "^PA6",
    z_step: "PB6",
    z_dir: "!PB5",
    z_enable: "!PB4",
    z_endstop: "^PA7",
    uart_pin_global: "PC11",
    has_uart_global: true,
    needs_standby_pins: false,
  },
  "creality-4.2.7": {
    name: "Creality 4.2.7",
    x_step: "PB9",
    x_dir: "PC2",
    x_enable: "!PC3",
    x_endstop: "^PA5",
    y_step: "PB7",
    y_dir: "PB8",
    y_enable: "!PB6",
    y_endstop: "^PA6",
    z_step: "PB5",
    z_dir: "!PB6",
    z_enable: "!PB4",
    z_endstop: "^PA7",
    uart_pin_global: "PC11",
    has_uart_global: true,
    needs_standby_pins: false,
  },
  "btt-skr-3": {
    name: "BIGTREETECH SKR 3",
    x_step: "PE2",
    x_dir: "PE1",
    x_enable: "!PE0",
    x_endstop: "^PC0",
    y_step: "PE5",
    y_dir: "PE4",
    y_enable: "!PE3",
    y_endstop: "^PC1",
    z_step: "PE8",
    z_dir: "PE7",
    z_enable: "!PE6",
    z_endstop: "^PC2",
    uart_pin_global: "PC11",
    has_uart_global: true,
  },
  "btt-manta-m8p": {
    name: "BIGTREETECH Manta M8P",
    x_step: "PB9",
    x_dir: "PB8",
    x_enable: "!PC3",
    x_endstop: "^PC14",
    y_step: "PC2",
    y_dir: "PC1",
    y_enable: "!PC0",
    y_endstop: "^PA5",
    z_step: "PB6",
    z_dir: "PB5",
    z_enable: "!PB4",
    z_endstop: "^PA6",
    uart_pin_global: "PC11",
    has_uart_global: true,
  },
  "fysetc-spider": {
    name: "FYSETC Spider",
    x_step: "PE11",
    x_dir: "PE10",
    x_enable: "!PE9",
    x_endstop: "^PA1",
    y_step: "PD8",
    y_dir: "PB12",
    y_enable: "!PD9",
    y_endstop: "^PA2",
    z_step: "PD12",
    z_dir: "!PC4",
    z_enable: "!PE8",
    z_endstop: "^PA0",
    uart_pin_x: "PE7",
    uart_pin_y: "PE15",
    uart_pin_z: "PA15",
    has_uart_global: false,
  },
  "octopus-pro": {
    name: "Octopus Pro",
    x_step: "PB9",
    x_dir: "PB8",
    x_enable: "!PC3",
    x_endstop: "^PA5",
    y_step: "PC2",
    y_dir: "PC1",
    y_enable: "!PC0",
    y_endstop: "^PA6",
    z_step: "PB6",
    z_dir: "PB5",
    z_enable: "!PB4",
    z_endstop: "^PA7",
    uart_pin_global: "PC11",
    has_uart_global: true,
  },
  "btt-manta-m5p": {
    name: "BIGTREETECH Manta M5P",
    x_step: "PC8",
    x_dir: "PC9",
    x_enable: "!PA15",
    x_endstop: "^!PD3",
    y_step: "PA10",
    y_dir: "!PA14",
    y_enable: "!PA13",
    y_endstop: "^!PD2",
    z_step: "PC6",
    z_dir: "PC7",
    z_enable: "!PA9",
    z_endstop: "^!PC3",
    e_step: "PB12",
    e_dir: "!PB11",
    e_enable: "!PA8",
    uart_pin_x: "PD9",
    uart_pin_y: "PD8",
    uart_pin_z: "PB10",
    uart_pin_e: "PB2",
    has_uart_global: false,
    needs_standby_pins: false,
  },
};

// Builds a [tmc2209 <section>] block. tx_pin and uart_address are only
// included when actually provided - a board with one dedicated UART pin
// per driver doesn't need an address (there's nothing to multiplex), so
// omitting it here rather than inventing one keeps the output honest about
// what the board dictionary actually knows.
function tmc2209Block(sectionName, uartPin, txPin, uartAddress, runCurrent) {
  let block = `[tmc2209 ${sectionName}]\nuart_pin: ${uartPin}\n`;
  if (txPin !== undefined) {
    block += `tx_pin: ${txPin}\n`;
  }
  if (uartAddress !== undefined) {
    block += `uart_address: ${uartAddress}\n`;
  }
  block += `run_current: ${runCurrent}\nstealthchop_threshold: 999999\n\n`;
  return block;
}

// Generate based on form inputs
function generateConfig() {
  const boardKey = document.getElementById("board").value;
  const driverType = document.getElementById("driver").value;
  const probeType = document.getElementById("probe").value;
  const bedX = document.getElementById("bedX").value;
  const bedY = document.getElementById("bedY").value;
  const bedZ = document.getElementById("bedZ").value;
  const mcuSerial = document.getElementById("mcuSerial").value.trim();

  const board = boards[boardKey];
  if (!board) return "# ERROR: Board not found";

  let cfg = `# printer.cfg - Generated by Dimidium CFG Generator\n`;
  cfg += `# Board: ${board.name}\n`;
  cfg += `# Driver: ${driverType.toUpperCase()}\n`;
  cfg += `# Probe: ${probeType}\n\n`;

  cfg += `[mcu]\n`;
  if (mcuSerial) {
    cfg += `serial: /dev/serial/by-id/${mcuSerial}\n\n`;
  } else {
    cfg += `serial: /dev/serial/by-id/usb-Klipper_xxxxxxxx-if00\n\n`;
  }

  cfg += `[printer]\n`;
  cfg += `kinematics: cartesian\n`;
  cfg += `max_velocity: 300\n`;
  cfg += `max_accel: 3000\n`;
  cfg += `max_z_velocity: 5\n`;
  cfg += `max_z_accel: 100\n\n`;

  cfg += `[stepper_x]\n`;
  cfg += `step_pin: ${board.x_step}\n`;
  cfg += `dir_pin: ${board.x_dir}\n`;
  cfg += `enable_pin: ${board.x_enable}\n`;
  cfg += `microsteps: 16\n`;
  cfg += `rotation_distance: 40\n`;
  cfg += `endstop_pin: ${board.x_endstop}\n`;
  cfg += `position_endstop: 0\n`;
  cfg += `position_max: ${bedX}\n`;
  cfg += `homing_speed: 50\n\n`;

  cfg += `[stepper_y]\n`;
  cfg += `step_pin: ${board.y_step}\n`;
  cfg += `dir_pin: ${board.y_dir}\n`;
  cfg += `enable_pin: ${board.y_enable}\n`;
  cfg += `microsteps: 16\n`;
  cfg += `rotation_distance: 40\n`;
  cfg += `endstop_pin: ${board.y_endstop}\n`;
  cfg += `position_endstop: 0\n`;
  cfg += `position_max: ${bedY}\n`;
  cfg += `homing_speed: 50\n\n`;

  // Stepper Z section — depends on whether a probe is used
  if (probeType !== "none") {
    cfg += `[stepper_z]\n`;
    cfg += `step_pin: ${board.z_step}\n`;
    cfg += `dir_pin: ${board.z_dir}\n`;
    cfg += `enable_pin: ${board.z_enable}\n`;
    cfg += `microsteps: 16\n`;
    cfg += `rotation_distance: 8\n`;
    cfg += `endstop_pin: probe:z_virtual_endstop\n`;
    cfg += `position_max: ${bedZ}\n`;
    cfg += `position_min: -5\n\n`;
  } else {
    cfg += `[stepper_z]\n`;
    cfg += `step_pin: ${board.z_step}\n`;
    cfg += `dir_pin: ${board.z_dir}\n`;
    cfg += `enable_pin: ${board.z_enable}\n`;
    cfg += `microsteps: 16\n`;
    cfg += `rotation_distance: 8\n`;
    cfg += `endstop_pin: ${board.z_endstop}\n`;
    cfg += `position_endstop: 0\n`;
    cfg += `position_max: ${bedZ}\n\n`;
  }

  // TMC2209 driver configuration. Boards wire UART one of two ways:
  // per-axis dedicated pins (uart_pin_x/y/z/e - address only needed if the
  // board actually shares one physical bus between drivers, e.g. SKR E3
  // Turbo) or one shared global pin/bus for all axes (uart_pin_global,
  // has_uart_global: true). Driven off what fields the board actually
  // defines rather than the board name, so this works for any board added
  // to the dictionary, not just the ones it was originally written for.
  if (driverType === "tmc2209") {
    if (board.uart_pin_x !== undefined) {
      cfg += tmc2209Block("stepper_x", board.uart_pin_x, undefined, board.uart_addr_x, "0.580");
      cfg += tmc2209Block("stepper_y", board.uart_pin_y, undefined, board.uart_addr_y, "0.580");
      cfg += tmc2209Block("stepper_z", board.uart_pin_z, undefined, board.uart_addr_z, "0.580");
    } else if (board.has_uart_global) {
      const uartPin = board.uart_pin_global || "PC11";
      const txPin = board.tx_pin || "PC10";
      cfg += tmc2209Block("stepper_x", uartPin, txPin, 0, "0.580");
      cfg += tmc2209Block("stepper_y", uartPin, txPin, 1, "0.580");
      cfg += tmc2209Block("stepper_z", uartPin, txPin, 2, "0.580");
    }
  }

  // Extruder configuration. Falls back to the SKR Mini E3's extruder pinout
  // when a board doesn't define its own e_step/e_dir/e_enable - a
  // pre-existing limitation for boards without that data, not something
  // this change attempts to source pin-accurate values for.
  cfg += `[extruder]\n`;
  if (board.e_step !== undefined) {
    cfg += `step_pin: ${board.e_step}\ndir_pin: ${board.e_dir}\nenable_pin: ${board.e_enable}\n`;
  } else {
    cfg += `step_pin: PB3\ndir_pin: !PB4\nenable_pin: !PD1\n`;
  }
  cfg += `microsteps: 16\nrotation_distance: 33.500\nnozzle_diameter: 0.400\nfilament_diameter: 1.750\nheater_pin: PC8\nsensor_type: EPCOS 100K B57560G104F\nsensor_pin: PA0\ncontrol: pid\npid_Kp: 21.527\npid_Ki: 1.063\npid_Kd: 108.982\nmin_temp: 0\nmax_temp: 250\n\n`;

  // TMC2209 extruder - same per-axis vs global-bus split as the steppers
  // above. If a board has neither (no uart_pin_e and no global UART bus),
  // there's no data to build this section from, so it's omitted rather
  // than guessing.
  if (driverType === "tmc2209") {
    if (board.uart_pin_e !== undefined) {
      cfg += tmc2209Block("extruder", board.uart_pin_e, undefined, board.uart_addr_e, "0.650");
    } else if (board.has_uart_global) {
      cfg += tmc2209Block(
        "extruder",
        board.uart_pin_global || "PC11",
        board.tx_pin || "PC10",
        3,
        "0.650",
      );
    }
  }

  // Heater bed
  cfg += `[heater_bed]\nheater_pin: PC9\nsensor_type: ATC Semitec 104GT-2\nsensor_pin: PC4\ncontrol: pid\npid_Kp: 54.027\npid_Ki: 0.770\npid_Kd: 948.182\nmin_temp: 0\nmax_temp: 130\n\n`;
  cfg += `[heater_fan controller_fan]\npin: PB15\nheater: heater_bed\nheater_temp: 45.0\n\n`;
  cfg += `[heater_fan nozzle_cooling_fan]\npin: PC7\n\n`;
  cfg += `[fan]\npin: PC6\n\n`;

  // BLTouch / CR-Touch support
  if (probeType === "bltouch") {
    cfg += `[bltouch]\nsensor_pin: ^PC14\ncontrol_pin: PA1\nx_offset: -40\ny_offset: -10\nz_offset: 2.0\n\n`;
  } else if (probeType === "cr-touch") {
    cfg += `[cr_touch]\nsensor_pin: ^PC14\ncontrol_pin: PA1\nx_offset: -40\ny_offset: -10\nz_offset: 2.0\n\n`;
  }

  // Safe Z home and bed mesh (probe required)
  if (probeType !== "none") {
    const centerX = Math.floor(parseInt(bedX) / 2);
    const centerY = Math.floor(parseInt(bedY) / 2);
    cfg += `[safe_z_home]\nhome_xy_position: ${centerX},${centerY}\nspeed: 75\nz_hop: 10\nz_hop_speed: 5\n\n`;
    cfg += `[bed_mesh]\nspeed: 150\nhorizontal_move_z: 5\nmesh_min: 30,30\nmesh_max: ${parseInt(bedX) - 30},${parseInt(bedY) - 30}\nprobe_count: 5,5\n\n`;
  }

  // Board pins, display, and other hardware
  cfg += `[board_pins]\naliases:\n    EXP1_1=PB5, EXP1_3=PA9, EXP1_5=PA10, EXP1_7=PB8, EXP1_9=<GND>,\n    EXP1_2=PA15, EXP1_4=<RST>, EXP1_6=PB9, EXP1_8=PD6, EXP1_10=<5V>\n\n`;
  cfg += `[display]\nlcd_type: emulated_st7920\nspi_software_miso_pin: PD8\nspi_software_mosi_pin: PD6\nspi_software_sclk_pin: PB9\nen_pin: PB8\nencoder_pins: ^PA10, ^PA9\nclick_pin: ^!PA15\n\n`;
  cfg += `[output_pin beeper]\npin: PB5\n\n`;
  cfg += `[virtual_sdcard]\npath: ~/printer_data/gcodes\n\n`;
  cfg += `[pause_resume]\n\n[respond]\n\n[display_status]\n`;

  // SKR E3 Turbo standby pins
  if (board.needs_standby_pins) {
    cfg += `\n[static_digital_output tmc_standby_pins]\npins: ${board.standby_pins}\n`;
  }

  return cfg;
}

// Session saving and loading
function saveSession() {
  const formData = {
    board: document.getElementById("board").value,
    driver: document.getElementById("driver").value,
    probe: document.getElementById("probe").value,
    bedX: document.getElementById("bedX").value,
    bedY: document.getElementById("bedY").value,
    bedZ: document.getElementById("bedZ").value,
    mcuSerial: document.getElementById("mcuSerial").value,
    outputCfg: document.getElementById("outputCfg").value,
  };
  localStorage.setItem("dimidium_session", JSON.stringify(formData));
}

function loadSession() {
  const saved = localStorage.getItem("dimidium_session");
  if (!saved) return false;
  try {
    const data = JSON.parse(saved);
    if (data.board) document.getElementById("board").value = data.board;
    if (data.driver) document.getElementById("driver").value = data.driver;
    if (data.probe) document.getElementById("probe").value = data.probe;
    if (data.bedX) document.getElementById("bedX").value = data.bedX;
    if (data.bedY) document.getElementById("bedY").value = data.bedY;
    if (data.bedZ) document.getElementById("bedZ").value = data.bedZ;
    if (data.mcuSerial)
      document.getElementById("mcuSerial").value = data.mcuSerial;
    if (data.outputCfg)
      document.getElementById("outputCfg").value = data.outputCfg;
    return true;
  } catch (e) {
    return false;
  }
}

// Auto-save on any form input change
function attachAutoSave() {
  ["board", "driver", "probe", "bedX", "bedY", "bedZ", "mcuSerial"].forEach(
    (id) => {
      const el = document.getElementById(id);
      if (el) el.addEventListener("input", saveSession);
    },
  );
  const textarea = document.getElementById("outputCfg");
  if (textarea) textarea.addEventListener("input", saveSession);
}

// Buttons
function handleGenerate() {
  const newConfig = generateConfig();
  document.getElementById("outputCfg").value = newConfig;
  saveSession();
}

function handleDownload() {
  const config = document.getElementById("outputCfg").value;
  if (!config.trim()) {
    alert("No configuration to download. Click 'Generate' first.");
    return;
  }
  const blob = new Blob([config], { type: "text/plain" });
  const link = document.createElement("a");
  const url = URL.createObjectURL(blob);
  link.href = url;
  link.download = "printer.cfg";
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}

function handleClearConfig() {
  if (confirm("Clear the current config? This cannot be undone.")) {
    document.getElementById("outputCfg").value =
      "\n# Generate a new config using the form above.";
    saveSession();
  }
}

function handleClearUpload() {
  if (
    confirm(
      "Clear uploaded file? This will reset checker dimensions to generator defaults.",
    )
  ) {
    const fileInput = document.getElementById("cfgUpload");
    fileInput.value = "";

    const bedX = document.getElementById("bedX").value;
    const bedY = document.getElementById("bedY").value;
    const bedZ = document.getElementById("bedZ").value;

    document.getElementById("checkerBedX").value = bedX;
    document.getElementById("checkerBedY").value = bedY;
    document.getElementById("checkerBedZ").value = bedZ;

    const resultsDiv = document.getElementById("checkerResults");
    if (resultsDiv) {
      resultsDiv.innerHTML =
        '<div class="checker-summary pass">Upload cleared. Using generator dimensions.</div>';
      setTimeout(() => {
        if (
          resultsDiv.innerHTML ===
          '<div class="checker-summary pass">Upload cleared. Using generator dimensions.</div>'
        ) {
          resultsDiv.innerHTML = "";
        }
      }, 2000);
    }
  }
}

// ---------------------------------------------------------------------------------
// ESCAPE HTML
function escapeHtml(text) {
  if (text == null) return "";
  const div = document.createElement("div");
  div.textContent = String(text);
  return div.innerHTML;
}

// ---------------------------------------------------------------------------------
// BOUNDARY CHECKER FUNCTIONS BELOW.
// ---------------------------------------------------------------------------------
let syncEnabled = false;

function toggleBoundaryChecker() {
  const content = document.getElementById("checkerContent");
  const icon = document.getElementById("checkerToggleIcon");
  if (content.style.display === "none") {
    content.style.display = "block";
    icon.textContent = "▼";
  } else {
    content.style.display = "none";
    icon.textContent = "➤";
  }
}

function toggleSync(button) {
  syncEnabled = !syncEnabled;
  if (syncEnabled) {
    button.classList.add("active");
    syncFromGenerator();
  } else {
    button.classList.remove("active");
  }
}

function syncFromGenerator() {
  if (!syncEnabled) return;

  const bedX = document.getElementById("bedX").value;
  const bedY = document.getElementById("bedY").value;
  const bedZ = document.getElementById("bedZ").value;

  document.getElementById("checkerBedX").value = bedX;
  document.getElementById("checkerBedY").value = bedY;
  document.getElementById("checkerBedZ").value = bedZ;

  const resultsDiv = document.getElementById("checkerResults");
  if (resultsDiv) {
    resultsDiv.innerHTML =
      '<div class="checker-summary pass">Synced dimensions from generator</div>';
    setTimeout(() => {
      if (
        resultsDiv.innerHTML ===
        '<div class="checker-summary pass">Synced dimensions from generator</div>'
      ) {
        resultsDiv.innerHTML = "";
      }
    }, 2000);
  }

  const macroInput = document.getElementById("macroInput");
  if (macroInput && macroInput.value.trim()) {
    runBoundaryCheck();
  }
}

function parsePrinterCfgForDimensions(cfgText) {
  const dimensions = { bedX: null, bedY: null, bedZ: null };

  const x = cfgText.match(/\[stepper_x\][\s\S]*?position_max:\s*(\d+)/i);
  const y = cfgText.match(/\[stepper_y\][\s\S]*?position_max:\s*(\d+)/i);
  const z = cfgText.match(/\[stepper_z\][\s\S]*?position_max:\s*(\d+)/i);

  if (x) dimensions.bedX = parseFloat(x[1]);
  if (y) dimensions.bedY = parseFloat(y[1]);
  if (z) dimensions.bedZ = parseFloat(z[1]);

  return dimensions;
}

function handleCfgUpload(file) {
  const reader = new FileReader();
  reader.onload = function (e) {
    const cfg = e.target.result;
    const dim = parsePrinterCfgForDimensions(cfg);

    if (dim.bedX) {
      document.getElementById("checkerBedX").value = dim.bedX;
    }
    if (dim.bedY) {
      document.getElementById("checkerBedY").value = dim.bedY;
    }
    if (dim.bedZ) {
      document.getElementById("checkerBedZ").value = dim.bedZ;
    }

    const resultsDiv = document.getElementById("checkerResults");
    resultsDiv.innerHTML = `<div class="checker-summary pass">Loaded printer.cfg (${escapeHtml(dim.bedX || "?")} x ${escapeHtml(dim.bedY || "?")} x ${escapeHtml(dim.bedZ || "?")})</div>`;

    setTimeout(() => {
      if (document.getElementById("macroInput").value.trim())
        runBoundaryCheck();
    }, 100);
  };
  reader.readAsText(file);
}

function parseGcodeMoves(gcode) {
  const lines = gcode.split("\n");
  const moves = [];
  let currentX = 0,
    currentY = 0,
    currentZ = 0;
  let isRelative = false;
  const moveRegex = /G0?1\s*([^;]*)/i;
  lines.forEach((line, idx) => {
    const clean = line.trim();
    if (
      clean.startsWith("[") ||
      clean.toLowerCase().startsWith("gcode:") ||
      clean === ""
    )
      return;
    if (/G90/i.test(clean)) {
      isRelative = false;
      return;
    }
    if (/G91/i.test(clean)) {
      isRelative = true;
      return;
    }
    const match = clean.match(moveRegex);
    if (!match) return;
    const params = match[1];
    const xMatch = params.match(/X(-?\d+\.?\d*)/i);
    const yMatch = params.match(/Y(-?\d+\.?\d*)/i);
    const zMatch = params.match(/Z(-?\d+\.?\d*)/i);
    let newX = currentX,
      newY = currentY,
      newZ = currentZ;
    if (isRelative) {
      if (xMatch) newX += parseFloat(xMatch[1]);
      if (yMatch) newY += parseFloat(yMatch[1]);
      if (zMatch) newZ += parseFloat(zMatch[1]);
    } else {
      if (xMatch) newX = parseFloat(xMatch[1]);
      if (yMatch) newY = parseFloat(yMatch[1]);
      if (zMatch) newZ = parseFloat(zMatch[1]);
    }
    moves.push({ line: idx + 1, text: clean, x: newX, y: newY, z: newZ });
    currentX = newX;
    currentY = newY;
    currentZ = newZ;
  });
  return moves;
}

function checkMovesAgainstBed(moves, bedX, bedY, bedZ) {
  const errors = [],
    warnings = [];
  const maxX = parseFloat(bedX),
    maxY = parseFloat(bedY),
    maxZ = parseFloat(bedZ);
  moves.forEach((move) => {
    if (move.x < 0 || move.x > maxX)
      errors.push({
        line: move.line,
        text: move.text,
        reason: `X = ${move.x.toFixed(2)} out of bounds (0-${maxX})`,
      });
    if (move.y < 0 || move.y > maxY)
      errors.push({
        line: move.line,
        text: move.text,
        reason: `Y = ${move.y.toFixed(2)} out of bounds (0-${maxY})`,
      });
    if (move.z < 0)
      errors.push({
        line: move.line,
        text: move.text,
        reason: `Z = ${move.z.toFixed(2)} below 0 (would crash)`,
      });
    if (move.z > maxZ)
      warnings.push({
        line: move.line,
        text: move.text,
        reason: `Z = ${move.z.toFixed(2)} exceeds max height (${maxZ})`,
      });
  });
  return { errors, warnings };
}

function drawBedPreview(moves, bedX, bedY) {
  const canvas = document.getElementById("bedCanvas");
  if (!canvas) return;
  const ctx = canvas.getContext("2d");
  const w = canvas.width,
    h = canvas.height;

  const gradient = ctx.createLinearGradient(0, 0, w, h);
  gradient.addColorStop(0, "#1a1a2e");
  gradient.addColorStop(1, "#16213e");
  ctx.fillStyle = gradient;
  ctx.fillRect(0, 0, w, h);

  const maxX = parseFloat(bedX),
    maxY = parseFloat(bedY);
  const padding = 10;
  const plateX = padding;
  const plateY = padding;
  const plateW = w - padding * 2;
  const plateH = h - padding * 2;

  function toCanvas(x, y) {
    return {
      x: plateX + (x / maxX) * plateW,
      y: plateY + plateH - (y / maxY) * plateH,
    };
  }

  ctx.fillStyle = "#2a2a3e";
  ctx.fillRect(plateX, plateY, plateW, plateH);

  ctx.strokeStyle = "#3a3a55";
  ctx.lineWidth = 0.5;
  const gridLines = 5;
  for (let i = 1; i <= gridLines; i++) {
    const x = plateX + (i / gridLines) * plateW;
    ctx.beginPath();
    ctx.moveTo(x, plateY);
    ctx.lineTo(x, plateY + plateH);
    ctx.stroke();
  }
  for (let i = 1; i <= gridLines; i++) {
    const y = plateY + (i / gridLines) * plateH;
    ctx.beginPath();
    ctx.moveTo(plateX, y);
    ctx.lineTo(plateX + plateW, y);
    ctx.stroke();
  }

  ctx.strokeStyle = "#00ffaa";
  ctx.lineWidth = 2;
  ctx.strokeRect(plateX, plateY, plateW, plateH);

  const origin = toCanvas(0, 0);
  ctx.beginPath();
  ctx.arc(origin.x, origin.y, 5, 0, Math.PI * 2);
  ctx.fillStyle = "#ffaa44";
  ctx.fill();
  ctx.fillStyle = "white";
  ctx.font = "bold 10px monospace";
  ctx.fillText("Home (0,0)", origin.x + 8, origin.y - 5);

  if (moves && moves.length > 0 && moves[0].x != null && moves[0].y != null) {
    const start = toCanvas(moves[0].x, moves[0].y);
    ctx.beginPath();
    ctx.arc(start.x, start.y, 6, 0, Math.PI * 2);
    ctx.fillStyle = "#00ffaa";
    ctx.fill();
    ctx.beginPath();
    ctx.arc(start.x, start.y, 3, 0, Math.PI * 2);
    ctx.fillStyle = "#ffffff";
    ctx.fill();
    ctx.fillStyle = "#00ffaa";
    ctx.font = "bold 10px monospace";
    ctx.fillText("START", start.x - 18, start.y - 8);
  }

  if (moves && moves.length > 0) {
    for (let i = 1; i < moves.length; i++) {
      const a = moves[i - 1],
        b = moves[i];
      if (a.x == null || b.x == null) continue;
      const p1 = toCanvas(a.x, a.y);
      const p2 = toCanvas(b.x, b.y);
      const inBounds = b.x >= 0 && b.x <= maxX && b.y >= 0 && b.y <= maxY;
      ctx.beginPath();
      ctx.moveTo(p1.x, p1.y);
      ctx.lineTo(p2.x, p2.y);
      ctx.strokeStyle = inBounds ? "#00ffaa" : "#ff4444";
      ctx.lineWidth = 2.5;
      ctx.stroke();
    }
  }

  const centerX = plateX + plateW / 2;
  const centerY = plateY + plateH / 2;
  ctx.beginPath();
  ctx.arc(centerX, centerY, 3, 0, Math.PI * 2);
  ctx.fillStyle = "#888888";
  ctx.fill();
}

function runBoundaryCheck() {
  const text = document.getElementById("macroInput").value;
  const x = document.getElementById("checkerBedX").value;
  const y = document.getElementById("checkerBedY").value;
  const z = document.getElementById("checkerBedZ").value;
  const resultsDiv = document.getElementById("checkerResults");
  if (!text.trim()) {
    resultsDiv.innerHTML =
      '<div class="checker-summary pass">Paste G-code to check boundaries</div>';
    return;
  }
  const moves = parseGcodeMoves(text);
  const { errors, warnings } = checkMovesAgainstBed(moves, x, y, z);
  let html = "";
  if (!errors.length && !warnings.length) {
    html += `<div class="checker-summary pass">SAFE — All ${escapeHtml(moves.length)} moves are within bounds (0-${escapeHtml(x)}, 0-${escapeHtml(y)}, 0-${escapeHtml(z)})</div>`;
  } else if (errors.length) {
    html += `<div class="checker-summary fail">${escapeHtml(errors.length)} critical error(s) found</div>`;
  } else if (warnings.length) {
    html += `<div class="checker-summary pass">${escapeHtml(warnings.length)} warning(s)</div>`;
  }
  errors.forEach((e) => {
    html += `<div class="checker-error">Line ${escapeHtml(e.line)}: ${escapeHtml(e.reason)}<br><span style="font-size: 11px; opacity: 0.7;">${escapeHtml(e.text)}</span></div>`;
  });
  warnings.forEach((w) => {
    html += `<div class="checker-warning">Line ${escapeHtml(w.line)}: ${escapeHtml(w.reason)}<br><span style="font-size: 11px; opacity: 0.7;">${escapeHtml(w.text)}</span></div>`;
  });
  resultsDiv.innerHTML = html;
  drawBedPreview(moves, x, y);
}

let currentMacroMode = "paste";
let currentExtractedGcode = "";

function setMacroMode(mode) {
  currentMacroMode = mode;

  const pasteModeArea = document.getElementById("pasteModeArea");
  const fileModeArea = document.getElementById("fileModeArea");
  const pasteBtn = document.getElementById("pasteModeBtn");
  const fileBtn = document.getElementById("fileModeBtn");

  if (!pasteModeArea || !fileModeArea) return;

  if (mode === "paste") {
    pasteModeArea.style.display = "block";
    fileModeArea.style.display = "none";
    if (pasteBtn) pasteBtn.classList.add("active");
    if (fileBtn) fileBtn.classList.remove("active");
  } else {
    pasteModeArea.style.display = "none";
    fileModeArea.style.display = "block";
    if (pasteBtn) pasteBtn.classList.remove("active");
    if (fileBtn) fileBtn.classList.add("active");
  }
}

// MACRO FILE UPLOAD FUNCTIONS. new addition since output via text has a bit of friction
function parseKlipperMacroFile(content) {
  const lines = content.split("\n");
  const gcodeLines = [];
  let insideGcode = false;

  for (let line of lines) {
    const trimmed = line.trim();
    if (trimmed === "") continue;
    if (trimmed.startsWith("#")) continue;
    if (trimmed.startsWith("[gcode_macro")) {
      insideGcode = false;
      continue;
    }
    if (trimmed === "gcode:" || trimmed.startsWith("gcode:")) {
      insideGcode = true;
      const afterColon = trimmed.substring(trimmed.indexOf(":") + 1).trim();
      if (afterColon && !afterColon.startsWith("#")) {
        gcodeLines.push(afterColon);
      }
      continue;
    }
    if (insideGcode) {
      if (
        trimmed.includes("{%") ||
        trimmed.includes("{{") ||
        trimmed.includes("%}") ||
        trimmed.includes("}}")
      ) {
        continue;
      }
      if (trimmed.startsWith("#")) {
        continue;
      }
      if (trimmed && !trimmed.startsWith("#")) {
        gcodeLines.push(trimmed);
      }
    } else {
      if (trimmed && !trimmed.startsWith(";") && !trimmed.startsWith("#")) {
        gcodeLines.push(trimmed);
      }
    }
  }

  return gcodeLines.join("\n");
}

function handleMacroFileUpload(file) {
  const reader = new FileReader();
  reader.onload = function (e) {
    const content = e.target.result;
    const extractedGcode = parseKlipperMacroFile(content);
    currentExtractedGcode = extractedGcode;

    const previewArea = document.getElementById("extractedGcodePreview");
    if (previewArea) {
      previewArea.value = extractedGcode || "# No G-code found in macro file";
    }

    if (extractedGcode.trim()) {
      runBoundaryCheckWithGcode(extractedGcode);
    } else {
      const resultsDiv = document.getElementById("checkerResults");
      if (resultsDiv) {
        resultsDiv.innerHTML =
          '<div class="checker-summary pass">No G-code found in macro file.</div>';
      }
    }
  };
  reader.readAsText(file);
}

function getCurrentMacroGcode() {
  if (currentMacroMode === "paste") {
    return document.getElementById("macroInput")?.value || "";
  } else {
    return currentExtractedGcode || "";
  }
}

function runBoundaryCheckWithGcode(gcodeText) {
  const x = document.getElementById("checkerBedX")?.value;
  const y = document.getElementById("checkerBedY")?.value;
  const z = document.getElementById("checkerBedZ")?.value;
  const resultsDiv = document.getElementById("checkerResults");

  if (!gcodeText || !gcodeText.trim()) {
    if (resultsDiv)
      resultsDiv.innerHTML =
        '<div class="checker-summary pass">No G-code to check</div>';
    return;
  }

  const moves = parseGcodeMoves(gcodeText);
  const { errors, warnings } = checkMovesAgainstBed(moves, x, y, z);

  const MAX_DISPLAY = 1000;

  let html = "";
  if (!errors.length && !warnings.length) {
    html += `<div class="checker-summary pass">SAFE — All ${escapeHtml(moves.length)} moves are within bounds (0-${escapeHtml(x)}, 0-${escapeHtml(y)}, 0-${escapeHtml(z)})</div>`;
  } else if (errors.length) {
    html += `<div class="checker-summary fail">${escapeHtml(errors.length)} critical error(s) found</div>`;
  } else if (warnings.length) {
    html += `<div class="checker-summary pass">${escapeHtml(warnings.length)} warning(s)</div>`;
  }

  errors.slice(0, MAX_DISPLAY).forEach((e) => {
    html += `<div class="checker-error">Line ${escapeHtml(e.line)}: ${escapeHtml(e.reason)}<br><span style="font-size: 11px; opacity: 0.7;">${escapeHtml(e.text)}</span></div>`;
  });

  if (errors.length > MAX_DISPLAY) {
    html += `<div class="checker-error">... and ${escapeHtml(errors.length - MAX_DISPLAY)} more errors not shown</div>`;
  }

  warnings.slice(0, MAX_DISPLAY).forEach((w) => {
    html += `<div class="checker-warning">Line ${escapeHtml(w.line)}: ${escapeHtml(w.reason)}<br><span style="font-size: 11px; opacity: 0.7;">${escapeHtml(w.text)}</span></div>`;
  });

  if (warnings.length > MAX_DISPLAY) {
    html += `<div class="checker-warning">... and ${escapeHtml(warnings.length - MAX_DISPLAY)} more warnings not shown</div>`;
  }

  if (resultsDiv) resultsDiv.innerHTML = html;
  drawBedPreview(moves, x, y);
}

const originalRunBoundaryCheck = runBoundaryCheck;
window.runBoundaryCheck = function () {
  const gcodeText = getCurrentMacroGcode();
  runBoundaryCheckWithGcode(gcodeText);
};

// ADDED THE BUTTONS TO UI
function addClearButtons() {
  const configCard = document.querySelector("#cfgForm")?.closest(".card");

  if (configCard) {
    const buttonGroup = configCard.querySelector(".button-group");
    if (buttonGroup && !document.getElementById("clearGeneratorBtn")) {
      const clearGeneratorBtn = document.createElement("button");
      clearGeneratorBtn.id = "clearGeneratorBtn";
      clearGeneratorBtn.className = "btn-secondary";
      clearGeneratorBtn.textContent = "Clear Generator";
      clearGeneratorBtn.onclick = handleClearGenerator;
      buttonGroup.appendChild(clearGeneratorBtn);
    }
  }

  const outputCard = document.querySelector("#outputCfg")?.closest(".card");

  if (outputCard) {
    const buttonGroup = outputCard.querySelector(".button-group");

    let outputButtonGroup = buttonGroup;

    if (!outputButtonGroup) {
      outputButtonGroup = document.createElement("div");
      outputButtonGroup.className = "button-group";
      outputButtonGroup.style.marginTop = "15px";

      const infoBar = outputCard.querySelector(".info-bar");
      if (infoBar) {
        infoBar.insertAdjacentElement("afterend", outputButtonGroup);
      } else {
        outputCard.appendChild(outputButtonGroup);
      }
    }

    if (!document.getElementById("clearConfigBtn")) {
      const clearConfigBtn = document.createElement("button");
      clearConfigBtn.id = "clearConfigBtn";
      clearConfigBtn.className = "btn-secondary";
      clearConfigBtn.textContent = "Clear Config";
      clearConfigBtn.onclick = handleClearConfig;
      outputButtonGroup.appendChild(clearConfigBtn);
    }
  }

  const checkerCard = document.getElementById("boundaryChecker");
  if (checkerCard) {
    const fileUploadGroup = checkerCard.querySelector(
      '.form-group:has(input[type="file"])',
    );
    if (fileUploadGroup && !document.getElementById("clearUploadBtn")) {
      const clearUploadBtn = document.createElement("button");
      clearUploadBtn.id = "clearUploadBtn";
      clearUploadBtn.className = "btn-secondary";
      clearUploadBtn.textContent = "Clear Upload";
      clearUploadBtn.style.marginTop = "8px";
      clearUploadBtn.style.width = "100%";
      clearUploadBtn.onclick = handleClearUpload;
      fileUploadGroup.appendChild(clearUploadBtn);
    }
  }
}

// INITIALIZATION
document.addEventListener("DOMContentLoaded", () => {
  const hasSession = loadSession();
  if (!hasSession || !document.getElementById("outputCfg").value.trim()) {
    document.getElementById("outputCfg").value = generateConfig();
    saveSession();
  }
  attachAutoSave();
  document
    .getElementById("generateBtn")
    .addEventListener("click", handleGenerate);
  document
    .getElementById("downloadBtn")
    .addEventListener("click", handleDownload);
  addClearButtons();

  const cfgUpload = document.getElementById("cfgUpload");
  const checkBoundsBtn = document.getElementById("checkBoundsBtn");
  const clearMacroBtn = document.getElementById("clearMacroBtn");
  const macroInput = document.getElementById("macroInput");
  const checkerResults = document.getElementById("checkerResults");

  const pasteModeBtn = document.getElementById("pasteModeBtn");
  const fileModeBtn = document.getElementById("fileModeBtn");
  const macroFileUpload = document.getElementById("macroFileUpload");

  if (cfgUpload)
    cfgUpload.addEventListener("change", (e) => {
      if (e.target.files[0]) handleCfgUpload(e.target.files[0]);
    });
  if (checkBoundsBtn)
    checkBoundsBtn.addEventListener("click", () => window.runBoundaryCheck());
  if (clearMacroBtn)
    clearMacroBtn.addEventListener("click", () => {
      if (macroInput) macroInput.value = "";
      if (checkerResults) checkerResults.innerHTML = "";
      if (macroFileUpload) macroFileUpload.value = "";
      if (document.getElementById("extractedGcodePreview")) {
        document.getElementById("extractedGcodePreview").value = "";
      }
      currentExtractedGcode = "";
      drawBedPreview(
        [],
        document.getElementById("checkerBedX")?.value || 235,
        document.getElementById("checkerBedY")?.value || 235,
      );
    });

  if (pasteModeBtn) {
    pasteModeBtn.addEventListener("click", () => setMacroMode("paste"));
  }
  if (fileModeBtn) {
    fileModeBtn.addEventListener("click", () => setMacroMode("file"));
  }
  if (macroFileUpload) {
    macroFileUpload.addEventListener("change", (e) => {
      if (e.target.files[0]) handleMacroFileUpload(e.target.files[0]);
    });
  }

  const syncBtn = document.createElement("button");
  syncBtn.textContent = "Sync from Generator";
  syncBtn.className = "toggle-btn";
  syncBtn.onclick = () => toggleSync(syncBtn);
  const row = document.querySelector("#checkerContent .row");
  if (row) {
    const wrap = document.createElement("div");
    wrap.className = "form-group";
    wrap.appendChild(syncBtn);
    row.parentNode.insertBefore(wrap, row.nextSibling);
  }

  ["bedX", "bedY", "bedZ"].forEach((id) => {
    document.getElementById(id)?.addEventListener("input", () => {
      if (syncEnabled) syncFromGenerator();
    });
  });
});

// CLEAR GENERATOR (clears output and bed dimensions to 0) : changed to "" instead of 0
function handleClearGenerator() {
  if (
    confirm(
      "Clear generator? This will clear the output and reset bed dimensions to 0. Control board and other settings will remain unchanged.",
    )
  ) {
    document.getElementById("outputCfg").value =
      "\n# Generate a new config using the form above.";

    document.getElementById("bedX").value = "";
    document.getElementById("bedY").value = "";
    document.getElementById("bedZ").value = "";

    saveSession();

    const btn = document.getElementById("clearGeneratorBtn");
    if (btn) {
      const originalText = btn.textContent;
      btn.textContent = "Cleared";
      setTimeout(() => {
        btn.textContent = originalText;
      }, 1500);
    }
  }
}
