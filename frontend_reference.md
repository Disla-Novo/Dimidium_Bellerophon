# Frontend Reference – File-by-File Breakdown

This document is a companion to [`CONTRIBUTING.md`](./CONTRIBUTING.md). It provides a **file‑by‑file breakdown** of the Dimidium frontend — the IDE, CFG Generator, and Gravity Hub.

If you want to modify the UI, add a new theme, or understand how the frontend works, read on. For backend/engine details, see [`GUTS.md`](./GUTS.md).

---

> The project is actively evolving. If you notice any file paths in this document that do not match the current structure, please open an issue or submit a pull request with corrections.

---

## Frontend Overview

The Dimidium frontend is a collection of static HTML, CSS, and JavaScript files served by the embedded Spark Java server. All frontend files live in the `webpage/` directory.

**Key principle:** Frontend changes do not require rebuilding the JAR or performing a clean compile. Just refresh your browser.

---

## Safe Zones for Contributors

If you are new to the project, here are the easiest places to start:

| Area | What You Can Do |
| :--- | :--- |
| **Add New Themes** | Create a new CSS file in `webpage/styles/themes/` and register it in `webpage/scripts/colors_scripts/themes.js`. Do not modify existing themes. |
| **UI Tweaks** | Modify your own theme files (e.g., your custom theme CSS). Do not modify `webpage/styles/starter.css` or existing theme files unless an issue was made. |
| **Documentation** | Update `webpage/manual.html` to improve clarity or add additional info. |
| **CFG Generator** | Add new board definitions in `webpage/scripts/cfg_scripts/cfg-generator.js`. |

## Theme System Reference

Bellerophon uses a modular, data-driven theming system. Adding a new theme does not require modifying core layout logic.

### Adding a New Theme (Step-by-Step)

| Step | Location | Action |
| :--- | :--- | :--- |
| 1 | `webpage/styles/themes/` | Create a new CSS file (e.g., `forest.css`). |
| 2 | — | Scope all styles to a unique body class (e.g., `body.theme-forest`). |
| 3 | `webpage/scripts/colors_scripts/themes.js` | Add an entry to `window.THEME_MAP` with `label`, `preview` (hex), `css` (path), and `tokens` (color mappings). |
| 4 | — | Test locally and include a screenshot of your theme in your PR. |

### The Golden Rule

| Do | Don't |
| :--- | :--- |
| Create new theme files in `webpage/styles/themes/`. | Modify `starter.css` or core layout files. |
| Scope styles to a unique `body` class. | Modify `settings.js` (it is the engine). |
| Add entries to `window.THEME_MAP` in `themes.js`. | Modify the `colorhandling.js` script |
| One theme per Pull Request. | Submit multiple themes in one PR. |


---

## Core Pages

| File | Description |
| :--- | :--- |
| `webpage/index.html` | Landing page linking to the IDE, CFG Generator, and Gravity Hub. |
| `webpage/ide.html` | Main IDE page for writing, compiling, and testing `.bph` source. |
| `webpage/manual.html` | Bellerophon language documentation. |
| `webpage/library.html` | Page listing token references |
| `webpage/generator.html` | CFG Generator page with board selection, input form, and output panel. |
| `webpage/hub.html` | Gravity Hub main dashboard. Displays and manages printer groups (Klipper, Bambu, Prusa, Custom). |
| `webpage/group.html` | Group details page. Shows printers in a group and allows adding new printers by name and IP/URL. |
| `webpage/dash.html` | Printer dashboard. Embeds a printer's web interface (Mainsail/Fluidd) via iframe using the `ip` query parameter. |

---

## Styles (`webpage/styles/`)

### Core Styles

| File | Description |
| :--- | :--- |
| `starter.css` | Default theme for the IDE. Used by `ide.html`. |
| `generator.css` | Styling for the CFG Generator (`generator.html`). |
| `hub.css` | Styling for Gravity Hub dashboards, cards, and group layouts (`hub.html`). |
| `group.css` | Styling for Gravity Hub group details page (`group.html`). |
| `index.css` | Landing page styling (`index.html`). |
| `library.css` | references page styling (`library.html`). |
| `doc.css` | Documentation styling (`manual.html`). |

### Theme System (`webpage/styles/themes/`)

| File | Description |
| :--- | :--- |
| `themes/dark.css` | Dark theme variant. Extensible for future themes. |

> **Adding a new theme:** 
> - **UI Theme:** Create a new CSS file in `webpage/styles/themes/` (e.g., `forest.css`) for the IDE's visual styling.
> - **Syntax Colors:** Add a new entry to `window.THEME_MAP` in `webpage/scripts/colors_scripts/themes.js` with `label`, `preview`, `css`, and `tokens` (where `tokens` defines the syntax highlighting colors for that theme).

---

## Scripts (`webpage/scripts/`)

### CFG Generator Scripts (`cfg_scripts/`)

| File | Description |
| :--- | :--- |
| `cfg-generator.js` | CFG generation logic, board pin mapping, config string assembly, boundary checking, and file upload handling. |

### Color & Theme Scripts (`colors_scripts/`)

| File | Description |
| :--- | :--- |
| `colorhandling.js` | Core syntax highlighting engine and theme manager. Handles token color mapping, semantic variable detection, and applies CSS themes with `localStorage` persistence. |
| `themes.js` | Theme registry (`window.THEME_MAP`) with labels, CSS paths, preview colors, and token definitions for syntax highlighting. |

### Gravity Hub Scripts (`gravity_scripts/`)

| File | Description |
| :--- | :--- |
| `hub.js` | Hub dashboard interaction logic and user event handling. |
| `group.js` | Group page logic for Gravity Hub UI updates and interactions. |
| `hubfolder.js` | Hub navigation helpers and content loading support. |

### IDE Scripts (`ide_scripts/`)

| File | Description |
| :--- | :--- |
| `console-resize.js` | Enables drag-to-resize functionality for the console panel. Saves the current height to `localStorage` for persistence across sessions. |
| `errorHandler.js` | Frontend compiler orchestrator. Handles semantic validation, macro boundary checking, keyword spell-checking, half-statement detection, and compile orchestration with backend communication (`/compile`). Displays errors, warnings, and hints in the console with line-number highlighting. |
| `firmware.js` | Firmware selection and target switcher logic in the IDE. |
| `mscript.js` | Main IDE orchestration script. Handles profile management, reference storage, file operations (load/save/download), mode switching, console resizing, line number synchronization, auto-save, and G-code output highlighting. |

### Settings Scripts (`settings_scripts/`)

| File | Description |
| :--- | :--- |
| `settings.js` | Settings manager handling theme switching, persistence (`localStorage`), and modal UI. |

> **How themes work:** `settings.js` reads `window.THEME_MAP` from `themes.js` to render theme options. When a user selects a theme, it calls `window.applyTheme()` to load the CSS and token colors, then saves the preference to `localStorage`.

---

## Gravity Hub Pages

| File | Description |
| :--- | :--- |
| `webpage/hub.html` | Dashboard page for fleet and group management. |
| `webpage/group.html` | Group details page showing individual printer state and controls. |
| `webpage/dash.html` | Launcher and navigation entry. |
| `webpage/hub.text` | Legacy HTML (kept for reference). |

---


### Local Development

Please refer to [Local Development](https://github.com/Juli132/Dimidium_Bellerophon/tree/main?tab=contributing-ov-file#local-development) in contributing.md for instructions. 

---

## Questions?

If you have questions about the frontend architecture, need help with a UI change, or want to add a new feature, open a GitHub issue with the `question` tag.

Thank you for helping improve the Dimidium frontend!
