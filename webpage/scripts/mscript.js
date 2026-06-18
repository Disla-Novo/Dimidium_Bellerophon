// IMPORTANT SCRIPT. This is the main script for the info tab, handling line numbers
// file loading/saving, and reference management. It also includes the console resizing code for convenience.

// mock line numbers cause idk 
const input = document.getElementById('code-input');
const gutter = document.getElementById('line-numbers');
const compileBtn = document.getElementById('compile-btn');
const log = document.getElementById('console-log');
const gcodeOutput = document.getElementById('gcode-output');
const codeInput = document.getElementById('code-input');
// adding mode state here
window.currentMode = "klipper";


// ---------- Profile Management ----------
let currentProfile = null;

function loadProfileFromStorage() {
    const saved = localStorage.getItem('dimidium_profile');
    if (saved) {
        currentProfile = JSON.parse(saved);
    } else {
        currentProfile = {
            name: "Default",
            maxX: 220, maxY: 220, maxZ: 250,
            nozzleDiameter: 0.4,
            filamentDiameter: 1.75,
            layerHeight: 0.2,
            extrusionMultiplier: 1.0
        };
         console.log("Using default profile:", currentProfile);
    }
    window.currentProfile = currentProfile; // make it global for errorHandler.js
}

function saveProfileToStorage() {
    if (currentProfile) {
        localStorage.setItem('dimidium_profile', JSON.stringify(currentProfile));
        addLogMessage(`Profile "${currentProfile.name}" saved.`);
    }
}

function openProfileModal() {
    document.getElementById('profileName').value = currentProfile.name;
    document.getElementById('profileMaxX').value = currentProfile.maxX;
    document.getElementById('profileMaxY').value = currentProfile.maxY;
    document.getElementById('profileMaxZ').value = currentProfile.maxZ;
    document.getElementById('profileNozzle').value = currentProfile.nozzleDiameter;
    document.getElementById('profileFilament').value = currentProfile.filamentDiameter;
    document.getElementById('profileLayerHeight').value = currentProfile.layerHeight;
    document.getElementById('profileExtrusionMultiplier').value = currentProfile.extrusionMultiplier;
    document.getElementById('profileModal').style.display = 'block';
}

function closeProfileModal() {
    document.getElementById('profileModal').style.display = 'none';
}

function saveProfileFromModal() {
    currentProfile = {
        name: document.getElementById('profileName').value,
        maxX: parseFloat(document.getElementById('profileMaxX').value),
        maxY: parseFloat(document.getElementById('profileMaxY').value),
        maxZ: parseFloat(document.getElementById('profileMaxZ').value),
        nozzleDiameter: parseFloat(document.getElementById('profileNozzle').value),
        filamentDiameter: parseFloat(document.getElementById('profileFilament').value),
        layerHeight: parseFloat(document.getElementById('profileLayerHeight').value),
        extrusionMultiplier: parseFloat(document.getElementById('profileExtrusionMultiplier').value)
    };
    saveProfileToStorage();
    window.currentProfile = currentProfile;
    closeProfileModal();
    addLogMessage("Profile updated.");
}


//-0- ---------- End Profile Management ----------




// Update download button! added 3/26/2026
function updateDownloadButton() {
    const downloadBtn = document.getElementById('download-cfg-btn');
    if (downloadBtn) {
        if (window.currentMode === 'marlin') {
            downloadBtn.textContent = 'Download .gcode';
        } else {
            downloadBtn.textContent = 'Download .cfg';
        }
    }
}

function addLogMessage(message) {
    const consoleLog = document.getElementById('console-log');
    if (consoleLog) {
        const timestamp = new Date().toLocaleTimeString();
        consoleLog.innerHTML += `<div class="log-entry">[${timestamp}] ${message}</div>`;
        consoleLog.scrollTop = consoleLog.scrollHeight;
    }
}

function clearErrorLines() {
    document.querySelectorAll(".semantic-error-line")
        .forEach(el => el.classList.remove("semantic-error-line"));
}

function updateLines() {
    if (!input || !gutter) return;

    const totalLines = input.value.split('\n').length;
    let html = '';
    for (let i = 1; i <= totalLines; i++) {
        html += `<div>${i}</div>`; // each line number in its own div
    }
    gutter.innerHTML = html;
    clearErrorLines(); // remove old semantic highlights whenever lines update
}

// this helps syncronize the line numbers with the coding input as well as the overlay for syntax highlighting.
//  it listens to scroll events and updates the scrollTop of the other elements to match. this way, 
// when you scroll through code, the line numbers and highlights stay in sync with the text.
// im letting vscode summarize this comment since it does a good job.

function syncScroll() {
    if (!input || !gutter) return;
    gutter.scrollTop = input.scrollTop;
    const overlay = document.getElementById('code-overlay');
    if (overlay) overlay.scrollTop = input.scrollTop;
}

if (input) {
    input.addEventListener('input', updateLines);
    input.addEventListener('scroll', syncScroll);
}

// Initial line numbers
window.addEventListener('DOMContentLoaded', updateLines);

// this is references 
let savedRefs = JSON.parse(localStorage.getItem('jupitoreRefs')) || [];

function updateLocalStorage() {
    localStorage.setItem('jupitoreRefs', JSON.stringify(savedRefs));
    renderReferences(); // live update on same page i hope
}

// --- Library.html: add + buttons
// our references system which allows users to save references.
//  these can be used for quick copy pasting or just as a way to remember important tokens.
document.querySelectorAll('.add-btn').forEach(btn => {
    btn.addEventListener('click', e => {
        e.preventDefault();
        const tokenText = btn.parentElement.textContent.replace('+','').trim();
        if (savedRefs.length >= 15) {
            alert("You can only save up to 15 references.");
            return;
        }
        if (!savedRefs.includes(tokenText)) {
            savedRefs.push(tokenText);
            updateLocalStorage();
           
        } else {
            alert(`"${tokenText}" is already in your references.`);
        }
    });
});

// --- Ide.html: render saved references with - buttons
// this section renders the saved references in the info tab, allowing users to manage them. each reference has a delete button next to it for easy removal.
//  the references are stored in local storage so they persist across sessions and can be shared between tabs. when a reference is deleted,
//  it's removed from both the UI and local storage immediately.
function renderReferences() {
    const savedReferencesDiv = document.getElementById('saved-references');
    if (!savedReferencesDiv) return;

    savedReferencesDiv.innerHTML = '';
    savedRefs = JSON.parse(localStorage.getItem('jupitoreRefs')) || [];

    savedRefs.forEach((ref, idx) => {
        const div = document.createElement('div');
        div.className = 'token-item';
        div.textContent = ref;

        const delBtn = document.createElement('a');
        delBtn.href = "#";
        delBtn.className = 'delete-btn';
        delBtn.textContent = '−';
        delBtn.style.marginLeft = '8px';
        delBtn.addEventListener('click', e => {
            e.preventDefault();
            savedRefs.splice(idx, 1);
            updateLocalStorage();
        });

        div.appendChild(delBtn);
        savedReferencesDiv.appendChild(div);
    });
}

// Initial render on page load
renderReferences();

// ======== between pages thing========
window.addEventListener('storage', (e) => {
    if (e.key === 'jupitoreRefs') {
        savedRefs = JSON.parse(e.newValue) || [];
        renderReferences();
    }
});

//  Auto-save editor content 
const STORAGE_KEY = "bellerophon_editor_content";

// Restore code when page loads
window.addEventListener("DOMContentLoaded", () => {
     loadProfileFromStorage(); 
    const savedCode = localStorage.getItem(STORAGE_KEY);

    if (savedCode && codeInput) {
        codeInput.value = savedCode;
        updateLines();
        codeInput.dispatchEvent(new Event('input'));

        if (log) {
            log.innerHTML += `<div class="log-entry system">> Restored previous session.</div>`;
        }
    }
    

        // added saves for the hardware limits 4/8/2026
    /// deleted in place of profile





// this is where i am adding the MODE TOGGLE IT IS HERE
   const klipperBtn = document.getElementById('klipper-mode-btn');
const marlinBtn = document.getElementById('marlin-mode-btn');

if (klipperBtn && marlinBtn) {
    klipperBtn.addEventListener('click', () => {
        window.currentMode = "klipper";
        klipperBtn.classList.add('active');
        marlinBtn.classList.remove('active');
        addLogMessage("Switched to Klipper output mode");
        updateDownloadButton();
    });
    
    marlinBtn.addEventListener('click', () => {
        window.currentMode = "marlin";
        marlinBtn.classList.add('active');
        klipperBtn.classList.remove('active');
        addLogMessage("Switched to Marlin output mode");
        updateDownloadButton();
    });
    }

  const profileBtn = document.getElementById('profileBtn');
    if (profileBtn) profileBtn.addEventListener('click', openProfileModal);

    // Modal close handlers
    const closeSpan = document.querySelector('.close-modal');
    if (closeSpan) closeSpan.addEventListener('click', closeProfileModal);
    const cancelBtn = document.getElementById('cancelProfileBtn');
    if (cancelBtn) cancelBtn.addEventListener('click', closeProfileModal);
    const saveBtn = document.getElementById('saveProfileBtn');
    if (saveBtn) saveBtn.addEventListener('click', saveProfileFromModal);

    // Close modal if clicking outside
    window.addEventListener('click', (event) => {
        const modal = document.getElementById('profileModal');
        if (event.target === modal) closeProfileModal();
    });




});

// Save whenever user types
if (codeInput) {
    codeInput.addEventListener("input", () => {
        localStorage.setItem(STORAGE_KEY, codeInput.value);
    });
}

// this section is responsible to editing filenames. loading etc. 
document.addEventListener('DOMContentLoaded', () => {
    const downloadjup = document.getElementById('download-jup-btn');
    const fileNameEditable = document.getElementById('file-name-editable');
    const codeInput = document.getElementById('code-input');
    const MAX_LENGTH = 20;



    
    if (!downloadjup || !fileNameEditable || !codeInput) return;

    // FIXED: editable filename 
    let lastValidValue = '';
    
    fileNameEditable.addEventListener('input', () => {
        let value = fileNameEditable.innerText;
        
        // Save cursor position before modifying
        const selection = window.getSelection();
        const range = selection.getRangeAt(0);
        const startOffset = range.startOffset;
        
        //
        let cleanedValue = value.replace(/\s+/g, '_').replace(/[^A-Za-z0-9_-]/g, '');
          if (cleanedValue === '' || cleanedValue === '_') {
        fileNameEditable.innerText = '';
        return;
    }
        
        // max length
        if (cleanedValue.length > MAX_LENGTH) {
            cleanedValue = cleanedValue.slice(0, MAX_LENGTH);
        }
        
        // Only update if value changed
        if (cleanedValue !== value) {
            fileNameEditable.innerText = cleanedValue;
            
            // Restore cursor position 
            const newRange = document.createRange();
            const textNode = fileNameEditable.firstChild || fileNameEditable;
            const newPosition = Math.min(startOffset, cleanedValue.length);
            newRange.setStart(textNode, newPosition);
            newRange.collapse(true);
            
            selection.removeAllRanges();
            selection.addRange(newRange);
        }
        
        lastValidValue = cleanedValue;
    });

    // .jup download
    downloadjup.addEventListener('click', () => {
        const jupContent = codeInput.value.trim();

        if (!jupContent && !confirm("The Jupitore file is empty. Save anyway?")) return;

        let fileName = fileNameEditable.innerText.trim();
        if (!fileName || fileName === 'filename') fileName = 'input';
        fileName += '.bph';

        const bloby = new Blob([jupContent], { type: 'text/plain' });
        const urly = URL.createObjectURL(bloby);

        const ah = document.createElement('a');
        ah.href = urly;
        ah.download = fileName;
        ah.click();

        URL.revokeObjectURL(urly);
    });
});


// added GCODE 
// download cfg/gcode file
const downloadBtn = document.getElementById('download-cfg-btn');

if (downloadBtn && gcodeOutput) {
    downloadBtn.addEventListener('click', () => {
        const content = gcodeOutput.textContent || gcodeOutput.innerText;

        if (!content.trim()) {
            if (!confirm("The file is empty. Save anyway?")) return;
        }
        
        // Get filename from editor
        const fileNameSpan = document.getElementById('file-name-editable');
        let baseName = fileNameSpan ? fileNameSpan.innerText.trim() : 'macro';
        if (!baseName || baseName === 'filename') baseName = 'macro';
        
        // Add extension based on mode
        const ext = window.currentMode === 'marlin' ? '.gcode' : '.cfg';
        const filename = baseName + ext;
        
        const blob = new Blob([content], { type: 'text/plain' });
        const url = URL.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        a.click();

        URL.revokeObjectURL(url);
    });
}


// adding loading buttons

const loadBtn = document.getElementById('load-jup-btn');
const loadInput = document.getElementById('load-jup-input');

loadBtn.addEventListener('click', () => loadInput.click());

loadInput.addEventListener('change', (e) => {
    const file = e.target.files[0];
    if (!file) return;
    
    const reader = new FileReader();
    reader.onload = () => {
        codeInput.value = reader.result;
        updateLines();       
        codeInput.dispatchEvent(new Event('input')); // overlay highlight

        let fileName = file.name;
        if (fileName.toLowerCase().endsWith('.bph')) {
            fileName = fileName.slice(0, -4); // Remove .bph extension
        }
        const fileNameEditable = document.getElementById('file-name-editable');
        if (fileNameEditable) {
            // Clean the filename for display 
            fileName = fileName.replace(/\s+/g, '_').replace(/[^A-Za-z0-9_-]/g, '');
            if (fileName.length > 20) fileName = fileName.slice(0, 20);
            fileNameEditable.innerText = fileName;
            
            // Set cursor to end
            const range = document.createRange();
            const sel = window.getSelection();
            range.selectNodeContents(fileNameEditable);
            range.collapse(false);
            sel.removeAllRanges();
            sel.addRange(range);
        }

        // FIX: Reset the file input so the same file can be loaded again... 
        loadInput.value = '';
    };
    reader.readAsText(file);
});

// clear everything
const clearBtn = document.getElementById('clear-btn');

const consoleLog = document.getElementById('console-log');

if (clearBtn) {
    clearBtn.addEventListener('click', () => {
        const hasCode = codeInput.value.trim().length > 0;
        const hasOutput = gcodeOutput.textContent.trim().length > 0;

        if ((hasCode || hasOutput) &&
            !confirm("Clear editor, output, and console?\nThis cannot be undone.")
        ) return;

       codeInput.value = '';
gcodeOutput.textContent = '';
localStorage.removeItem(STORAGE_KEY);

        if (consoleLog) {
            consoleLog.innerHTML =
                ``;
        }

        updateLines?.();
        codeInput.dispatchEvent(new Event('input'));
    });
}
const clearLogBtn = document.getElementById('clear-log-btn');


if (clearLogBtn && consoleLog) {
    clearLogBtn.addEventListener('click', () => {
        consoleLog.innerHTML =
            ``;
    });
}
const consoleEl = document.querySelector('.console');
const header = consoleEl.querySelector('.console-header');
let isResizing = false;

header.addEventListener('mousedown', (e) => {
    isResizing = true;
    document.body.style.cursor = 'ns-resize';
});

document.addEventListener('mousemove', (e) => {
    if (!isResizing) return;
    const newHeight = window.innerHeight - e.clientY; 
    consoleEl.style.height = `${newHeight}px`;
});

document.addEventListener('mouseup', () => {
    isResizing = false;
    document.body.style.cursor = 'default';
});

// Im adding a better coloring system for the output. I think it would be nice
window.highlightGCode = function(rawGcode) {
    const colors = {
        header: "#ffffff",
        comment: "#5c6370",
        command: "#56b6c2",
        custom: "#56b6c2",
        param: "#abb2bf",
        number: "#d19a66"
    };

    let formattedCode = rawGcode.replace(/</g, "&lt;").replace(/>/g, "&gt;");

    formattedCode = formattedCode.replace(/^(\[.*?\])/gm, `<span style="color: ${colors.header}">$1</span>`);
    formattedCode = formattedCode.replace(/^(gcode:)/gm, `<span style="color: ${colors.header}">$1</span>`);
    
    formattedCode = formattedCode.replace(/(;.*)/g, `<span style="color: ${colors.comment}">$1</span>`);

    formattedCode = formattedCode.replace(/\b([GM]\d+)\b/g, `<span style="color: ${colors.command}">$1</span>`);
    
    formattedCode = formattedCode.replace(/\b([A-Z_]+)(?=\s|$|=)/g, (match) => {
        if (['X', 'Y', 'Z', 'E', 'F', 'S', 'P', 'SPEED', 'TIMEOUT', 'ADVANCE'].includes(match)) return match;
        return `<span style="color: ${colors.custom}">${match}</span>`;
    });
    
    formattedCode = formattedCode.replace(/\b([XYZEFSP]|SPEED|TIMEOUT|ADVANCE)(=|\s*)?(-?\d+(\.\d+)?)/g, 
        `<span style="color: ${colors.param}">$1</span>$2<span style="color: ${colors.number}">$3</span>`
    );

    return formattedCode;
};