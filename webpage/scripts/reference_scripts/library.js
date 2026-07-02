// Interactive documentation for Bellerophon commands
// This script adds the interactive demo functionality to library.html

const commandData = {
    'home': {
        label: 'Home',
        description: 'Homes the printer to its origin. Optionally with coordinates.',
        example: `M.title "Home Example"
    Absolute
    Home
M.end`
    },
    'home-coord': {
        label: 'Home with Coordinates',
        description: 'Homes the printer with optional coordinates.',
        example: `M.title "Home with Coordinates"
    Absolute
    Home -> x=0 y=0 z=0
M.end`
    },
    'move': {
        label: 'Move',
        description: 'Moves the print head in a direction by one unit.',
        example: `M.title "Move Example"
    Absolute
    Home
    Move left
    Move right
    Move center
M.end`
    },
    'moveto': {
        label: 'MoveTo',
        description: 'Moves the print head to absolute coordinates.',
        example: `M.title "MoveTo Example"
    Absolute
    Home
    MoveTo x=100 y=100 z=0.2
    MoveTo x=150 y=100
    MoveTo x=150 y=150
    MoveTo x=100 y=150
    MoveTo x=100 y=100
M.end`
    },
    'absolute': {
        label: 'Absolute',
        description: 'Sets absolute positioning mode (G90).',
        example: `M.title "Absolute Example"
    Absolute
    Home
    MoveTo x=100 y=100 z=0.2
M.end`
    },
    'relative': {
        label: 'Relative',
        description: 'Sets relative positioning mode (G91).',
        example: `M.title "Relative Example"
    Relative
    Home
    MoveTo x=10 y=10 z=0.2
M.end`
    },
    'heat': {
        label: 'Heat',
        description: 'Heats the target to temperature and waits until reached.',
        example: `M.title "Heat Example"
    Heat bed=60
    Heat extruder=210
M.end`
    },
    'set-heater': {
        label: 'Set_Heater_Temperature',
        description: 'Sets target temperature without waiting.',
        example: `M.title "Set Heater Example"
    Set_Heater_Temperature bed=60
    Set_Heater_Temperature extruder=210
M.end`
    },
    'cooldown': {
        label: 'Cooldown',
        description: 'Turns off heaters. Optionally targets a specific one.',
        example: `M.title "Cooldown Example"
    Heat bed=60
    Heat extruder=210
    Dwell 2000 ms
    Cooldown
M.end`
    },
    'setspeed': {
        label: 'SetSpeed',
        description: 'Sets the movement speed in mm/min.',
        example: `M.title "SetSpeed Example"
    Absolute
    Home
    SetSpeed = 1500
    MoveTo x=100 y=100 z=0.2
M.end`
    },
    'dwell': {
        label: 'Dwell',
        description: 'Pause execution for a specified time.',
        example: `M.title "Dwell Example"
    Heat bed=60
    Dwell 5000 ms
    Heat extruder=210
    Dwell 2 s
M.end`
    },
    'pause': {
        label: 'Pause',
        description: 'Pauses macro execution.',
        example: `M.title "Pause Example"
    Home
    Pause
    MoveTo x=100 y=100 z=0.2
    Resume
M.end`
    },
    'repeat': {
        label: 'Repeat',
        description: 'Repeats a block of code a specified number of times.',
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
M.end`
    },
    'brepeat': {
        label: 'Brepeat',
        description: 'Repeats a block with iterator variable i.',
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
M.end`
    },
    'if': {
        label: 'If / Endif',
        description: 'Conditional block based on temperature checks.',
        example: `M.title "If Example"
    Heat extruder=210
    if extruder > 200
        Respond MSG "Extruder is ready!"
    endif
    Cooldown
M.end`
    },
    'respond': {
        label: 'Respond MSG',
        description: 'Sends a message to the printer console.',
        example: `M.title "Respond Example"
    Home
    Respond MSG "Starting print job..."
    Heat bed=60
    Heat extruder=210
    Respond MSG "Heaters at target"
M.end`
    },
    'level': {
        label: 'Level',
        description: 'Runs bed leveling routine (BED_MESH_CALIBRATE).',
        example: `M.title "Level Example"
    Home
    Level
    LoadBedMesh "default"
M.end`
    },
    'loadbedmesh': {
        label: 'LoadBedMesh',
        description: 'Loads a saved bed mesh profile.',
        example: `M.title "LoadBedMesh Example"
    Home
    Level
    LoadBedMesh "default"
M.end`
    },
    'pressureadvance': {
        label: 'SetPressureAdvance',
        description: 'Sets pressure advance for the extruder.',
        example: `M.title "Pressure Advance Example"
    SetPressureAdvance 0.04
M.end`
    },
    'insertgcode': {
        label: 'InsertGCode',
        description: 'Inserts a G-code file. Use "reference" to print from SD card.',
        example: `M.title "InsertGCode Example"
    Home
    Heat bed=60
    Heat extruder=210
    InsertGCode "prints/part.gcode"
    InsertGCode "prints/part.gcode" reference
    Cooldown
M.end`
    },
    'printfile': {
        label: 'PRINTFILE',
        description: 'Prints a G-code file from SD card.',
        example: `M.title "PRINTFILE Example"
    Home
    Heat bed=60
    Heat extruder=210
    PRINTFILE "prints/part.gcode"
    Cooldown
M.end`
    }
};

let currentCommandId = null;
let isCompiling = false;

document.addEventListener('DOMContentLoaded', () => {
    const demoInput = document.getElementById('demo-input');
    const demoCompile = document.getElementById('demo-compile');
    const demoStatus = document.getElementById('demo-status');
    const demoOutput = document.getElementById('demo-output-content');
    const openInIde = document.getElementById('open-in-ide');

    // Click on grammar item -> load example
    document.querySelectorAll('.grammar-item[data-command]').forEach(item => {
        item.addEventListener('click', function(e) {
            // Don't trigger if clicking the add button
            if (e.target.closest('.add-btn')) return;

            const cmdId = this.dataset.command;
            selectCommand(cmdId);
        });

        // Also make the add button work (it already does via mscript.js)
        // Just ensure it doesn't interfere with the click handler
    });

    function selectCommand(id) {
        const cmd = commandData[id];
        if (!cmd) return;

        currentCommandId = id;

        // Update active state
        document.querySelectorAll('.grammar-item[data-command]').forEach(el => {
            el.classList.toggle('active', el.dataset.command === id);
        });

        // Update demo panel
        document.getElementById('demo-title').textContent = cmd.label;
        document.getElementById('demo-description').textContent = cmd.description;
        demoInput.value = cmd.example;

        // Reset output
        demoOutput.innerHTML = '<span class="placeholder">Compile to see output</span>';
        demoStatus.textContent = 'Ready';
        demoStatus.className = 'demo-status';
        openInIde.style.display = 'none';
    }

    // Compile button
    demoCompile.addEventListener('click', async () => {
        if (isCompiling) return;

        const code = demoInput.value.trim();
        if (!code) {
            demoStatus.textContent = 'No code to compile';
            demoStatus.className = 'demo-status error';
            return;
        }

        isCompiling = true;
        demoCompile.disabled = true;
        demoStatus.textContent = 'Compiling...';
        demoStatus.className = 'demo-status';

        try {
            let profile = {
                name: "Default",
                maxX: 220,
                maxY: 220,
                maxZ: 250,
                nozzleDiameter: 0.4,
                filamentDiameter: 1.75,
                layerHeight: 0.2,
                extrusionMultiplier: 1.0
            };
            const savedProfile = localStorage.getItem("dimidium_profile");
            if (savedProfile) {
                try {
                    profile = JSON.parse(savedProfile);
                } catch (e) {}
            }

            const payload = {
                code: code,
                mode: 'klipper',
                profile: profile,
                gcodeFolder: localStorage.getItem("bellerophon-gcode-folder") || ""
            };

            const res = await fetch('/compile', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            const data = await res.json();

            if (data.success) {
                let output = data.output;
                if (output.startsWith('SUCCESS_PAGED:')) {
                    output = '[Large output paged to disk. Preview not available.]';
                }
                demoOutput.textContent = output;
                demoStatus.textContent = '✓ Success';
                demoStatus.className = 'demo-status success';
                openInIde.style.display = 'inline-block';
            } else {
                demoOutput.textContent = 'Error: ' + (data.error || 'Unknown compiler error');
                demoStatus.textContent = '✗ Compilation failed';
                demoStatus.className = 'demo-status error';
                openInIde.style.display = 'none';
            }
        } catch (err) {
            demoOutput.textContent = 'Network error: ' + err.message;
            demoStatus.textContent = '✗ Connection error';
            demoStatus.className = 'demo-status error';
            openInIde.style.display = 'none';
        }

        isCompiling = false;
        demoCompile.disabled = false;
    });

    // Open in IDE
    openInIde.addEventListener('click', () => {
        const cmd = commandData[currentCommandId];
        if (!cmd) return;

        localStorage.setItem('bellerophon_demo_code', cmd.example);
        window.open('ide.html?load=demo', '_blank');
    });

    // Keyboard shortcut: Ctrl+Enter to compile
    demoInput.addEventListener('keydown', (e) => {
        if (e.ctrlKey && e.key === 'Enter') {
            e.preventDefault();
            demoCompile.click();
        }
    });

    // Auto-select first command
    const firstCmd = document.querySelector('.grammar-item[data-command]');
    if (firstCmd) {
        selectCommand(firstCmd.dataset.command);
    }
});