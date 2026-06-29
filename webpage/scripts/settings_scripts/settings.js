document.addEventListener("DOMContentLoaded", () => {
  const settingsBtn = document.getElementById("settings-btn");
  const modal = document.getElementById("settingsModal");
  const closeBtn = document.getElementById("closeSettingsBtn");
  const closeX = document.querySelector(".close-settings-modal");

  // GCODE FOLDER SETTINGS VARIABLES
  let gcodeFolderPath = localStorage.getItem("bellerophon-gcode-folder") || "";
  let gcodeFileList = [];
  let fileListOpen = false;

  // file list from storage on script load so autocomplete works immediately
  const savedFilesOnLoad = localStorage.getItem("bellerophon-gcode-files");
  if (savedFilesOnLoad) {
    gcodeFileList = JSON.parse(savedFilesOnLoad);
    window.gcodeFileList = gcodeFileList;
  }

  function getThemeLabel(key) {
    return window.THEME_MAP?.[key]?.label || key;
  }

  function renderThemes() {
    const container = document.getElementById("themeSelector");
    if (!container || !window.THEME_MAP) return;

    container.innerHTML = "";

  
    container.style.maxHeight = "400px";
    container.style.overflowY = "auto";
    container.style.paddingRight = "20px"; 
    container.style.scrollbarWidth = "thin";
container.style.scrollbarColor = "#000000 #d3c9c6";

    Object.entries(window.THEME_MAP).forEach(([key, theme]) => {
      const div = document.createElement("div");
      div.className = "theme-option";
      div.dataset.theme = key;

      const previewColor = theme.preview || "#666";

      div.innerHTML = `
                <div class="theme-preview" style="background-color: ${previewColor};"></div>
                <span>${theme.label}</span>
            `;

      div.addEventListener("click", () => {
        if (window.applyTheme) {
          window.applyTheme(key);
        }
        setActiveTheme(key);
        localStorage.setItem("bellerophon-theme", key);
      });

      container.appendChild(div);
    });
  }

  function setActiveTheme(themeKey) {
    document.querySelectorAll(".theme-option").forEach((el) => {
      el.classList.toggle("active", el.dataset.theme === themeKey);
    });

    const desc = document.getElementById("themeDescription");
    if (desc) {
      desc.textContent = `Current theme: ${getThemeLabel(themeKey)}`;
    }
  }

  function renderGcodeSettings() {
    //  from storage every render so the list survives page refresh
    const savedFiles = localStorage.getItem("bellerophon-gcode-files");
    if (savedFiles && gcodeFileList.length === 0) {
      gcodeFileList = JSON.parse(savedFiles);
      window.gcodeFileList = gcodeFileList;
    }

    const container = document.getElementById("gcodeSettingsContainer");
    if (!container) return;

    const hasFolder = gcodeFolderPath && gcodeFolderPath.length > 0;
    const hasFiles = gcodeFileList && gcodeFileList.length > 0;
    const fileCount = gcodeFileList.length;

    container.innerHTML = `
      <div class="gcode-folder-setting">
        <div class="folder-status-row">
          <div class="folder-status">
            ${hasFolder ? 
              `<span class="folder-active">${gcodeFolderPath}</span>` : 
              `<span class="folder-inactive">No folder selected</span>`
            }
          </div>
          <div class="folder-actions">
            ${hasFolder ? `<button id="removeFolderBtn" class="remove-folder-btn">✕ Remove</button>` : ''}
          </div>
        </div>
        
        <!-- Manual path input -->
        <div class="folder-manual-input">
          <input type="text" id="manualFolderPath" 
                 value="${gcodeFolderPath}" 
                 placeholder="Paste full path: D:/gcode_folder or /home/user/gcode"
                 class="folder-path-input" />
          <button id="saveManualFolderBtn" class="save-folder-btn">Save Path</button>
        </div>
        
        ${hasFolder ? `
          <div class="folder-file-count">
            ${hasFiles ? 
              `<span class="file-count">${fileCount} .gcode files found</span>` : 
              `<span class="file-count-empty">No .gcode files in this folder</span>`
            }
          </div>
        ` : ''}
        
        ${hasFolder && hasFiles ? `
          <div class="gcode-file-list-container">
            <button id="gcodeFileToggle" class="gcode-file-list-toggle">
              <span>📄 Show Files (${fileCount})</span>
              <span class="arrow ${fileListOpen ? 'open' : ''}">▼</span>
            </button>
            <div id="gcodeFileDropdown" class="gcode-file-list-dropdown ${fileListOpen ? 'open' : ''}">
              ${gcodeFileList.map(f => `
                <div class="file-item">
                  <span class="file-icon"></span>
                  <span class="file-name">${f}</span>
                </div>
              `).join('')}
            </div>
          </div>
        ` : ''}
      </div>
    `;

    const removeBtn = document.getElementById("removeFolderBtn");
    if (removeBtn) {
      removeBtn.addEventListener("click", removeFolder);
    }

    const toggleBtn = document.getElementById("gcodeFileToggle");
    if (toggleBtn) {
      toggleBtn.addEventListener("click", () => {
        fileListOpen = !fileListOpen;
        renderGcodeSettings();
      });
    }

    // Manual path save handler
    const saveManualBtn = document.getElementById("saveManualFolderBtn");
    const manualInput = document.getElementById("manualFolderPath");
    if (saveManualBtn && manualInput) {
      saveManualBtn.addEventListener("click", () => {
        const path = manualInput.value.trim();
        if (path) {
          gcodeFolderPath = path;
          localStorage.setItem("bellerophon-gcode-folder", path);
          scanFolderViaBackend(path);
          renderGcodeSettings();
          addLogMessage(`G-code folder set to: ${path}`);
        }
      });
      manualInput.addEventListener("keydown", (e) => {
        if (e.key === "Enter") {
          saveManualBtn.click();
        }
      });
    }
  }

  function removeFolder() {
    gcodeFolderPath = "";
    gcodeFileList = [];
    fileListOpen = false;
    
    localStorage.removeItem("bellerophon-gcode-folder");
    localStorage.removeItem("bellerophon-gcode-files");
    
    window.gcodeFileList = [];
    
    renderGcodeSettings();
    
    addLogMessage("G-code folder removed.");
  }

  // Backend scan for manual path
  function scanFolderViaBackend(folderPath) {
    fetch('/scan-folder', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ folderPath })
    })
    .then(res => res.json())
    .then(data => {
      if (data.success) {
        gcodeFileList = data.files;
        localStorage.setItem("bellerophon-gcode-files", JSON.stringify(gcodeFileList));
        updateGcodeAutocomplete(gcodeFileList);
        renderGcodeSettings();
        addLogMessage(`Found ${gcodeFileList.length} .gcode files in "${folderPath}"`);
      } else {
        addLogMessage("Error scanning folder: " + data.error);
      }
    })
    .catch(err => {
      console.error("Error scanning folder:", err);
      addLogMessage("Error scanning folder: " + err.message);
    });
  }

  function updateGcodeAutocomplete(files) {
    window.gcodeFileList = files;
  }

  function open() {
    modal.style.display = "block";

    //  folder path and file list from storage every time the modal opens
    gcodeFolderPath = localStorage.getItem("bellerophon-gcode-folder") || "";
    const savedFiles = localStorage.getItem("bellerophon-gcode-files");
    if (savedFiles) {
      gcodeFileList = JSON.parse(savedFiles);
      window.gcodeFileList = gcodeFileList;
    }

    const current = localStorage.getItem("bellerophon-theme") || "starter";
    renderThemes();
    setActiveTheme(current);
    renderGcodeSettings();
  }

  function close() {
    modal.style.display = "none";
  }

  settingsBtn?.addEventListener("click", open);
  closeBtn?.addEventListener("click", close);
  closeX?.addEventListener("click", close);

  window.addEventListener("click", (e) => {
    if (e.target === modal) close();
  });
});