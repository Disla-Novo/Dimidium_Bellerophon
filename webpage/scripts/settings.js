document.addEventListener("DOMContentLoaded", () => {
    const settingsBtn = document.getElementById("settings-btn");
    const modal = document.getElementById("settingsModal");
    const closeBtn = document.getElementById("closeSettingsBtn");
    const closeX = document.querySelector(".close-settings-modal");

    function getThemeLabel(key) {
        return window.THEME_MAP?.[key]?.label || key;
    }

    function renderThemes() {
        const container = document.getElementById("themeSelector");
        if (!container || !window.THEME_MAP) return;

        container.innerHTML = "";

        Object.entries(window.THEME_MAP).forEach(([key, theme]) => {
            const div = document.createElement("div");
            div.className = "theme-option";
            div.dataset.theme = key;

            // FIXED ------- Pull the color directly from your theme's tokens. // double fix, changed to theme.preview
            // Fallback to a neutral gray if the color is missing.
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
        document.querySelectorAll(".theme-option").forEach(el => {
            el.classList.toggle("active", el.dataset.theme === themeKey);
        });

        const desc = document.getElementById("themeDescription");
        if (desc) {
            desc.textContent = `Current theme: ${getThemeLabel(themeKey)}`;
        }
    }

    function open() {
        modal.style.display = "block";
        // grab fresh from storage
        const current = localStorage.getItem("bellerophon-theme") || "starter";
        renderThemes();
        setActiveTheme(current);
    }

    function close() {
        modal.style.display = "none";
    }

    settingsBtn?.addEventListener("click", open);
    closeBtn?.addEventListener("click", close);
    closeX?.addEventListener("click", close);

    window.addEventListener("click", e => {
        if (e.target === modal) close();
    });
});