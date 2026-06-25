## File-by-File Categorization by Feature

This document is a companion to [`CONTRIBUTING.md`](./CONTRIBUTING.md). It provides a **file‑by‑file breakdown**, detailed architecture notes, and in‑depth contribution workflows for the Dimidium Ecosystem.

If you just want to get started quickly, see the main contributing guide. If you are planning to modify the parser, add a new firmware adapter, or understand the full stack, read on.

### Bellerophon Compiler Engine (Backend)

> *Note: The core parsing engine, ANTLR grammar, and generated syntax tree files currently use the legacy project name `Jupitore`. These operate as the core engine for the Bellerophon compiler.*

- `grammar/Jupitore.g4` — ANTLR grammar defining the Bellerophon DSL syntax and parse structure.
- `src/main/java/jupitore/gen/JupitoreLexer.java` — generated lexer that tokenizes Bellerophon source.
- `src/main/java/jupitore/gen/JupitoreParser.java` — generated parser that builds the parse tree from lexed tokens.
- `src/main/java/jupitore/gen/JupitoreVisitor.java` — generated visitor interface for parse tree traversal.
- `src/main/java/jupitore/gen/JupitoreBaseVisitor.java` — generated base visitor with default traversal behavior.
- `src/main/java/jupitore/gen/JupitoreListener.java` — generated listener interface for parse tree events.
- `src/main/java/jupitore/gen/JupitoreBaseListener.java` — generated base listener with empty event methods.
- `src/main/java/maindeveloper/GCodeVisitor.java` — core compiler engine that visits the AST and emits firmware-agnostic motion, macro, and extrusion commands.
- `src/main/java/maindeveloper/Compute.java` — expression evaluator used by the compiler for arithmetic, vector math, and parameter expansion.
- `src/main/java/maindeveloper/HardwareLimiter.java` — safety layer that enforces axis limits and rejects invalid motion during compile-time validation.
- `src/main/java/maindeveloper/LayerHandler.java` — helper for layer-specific macro generation and per-layer state management.
- `src/main/java/maindeveloper/PrinterSettings.java` — backend model capturing printer, extruder, and temperature settings.
- `src/main/java/maindeveloper/PrinterProfile.java` — printer profile model for build volume, nozzle characteristics, and motion limits.


### Configuration (CFG) Generator

- `webpage/generator.html` — CFG Generator page UI with board selection, input form, output panel, and file upload controls.
- `webpage/scripts/cfg-generator.js` — CFG generation logic, board pin mapping, config string assembly, boundary checking, and file upload handling.
 - `webpage/styles/generator.css` — used by `webpage/generator.html` (CFG Generator styling).

### Firmware Adapters

- `src/main/java/maindeveloper/KlipperVisitor.java` — Klipper firmware adapter implementing Bellerophon emission rules for Klipper macros and command syntax.
- `src/main/java/maindeveloper/MarlinVisitor.java` — Marlin firmware adapter implementing Bellerophon emission rules for Marlin-compatible G-code and macros.

### Bellerophon IDE (Frontend)

- `webpage/ide.html` — main IDE page for writing, compiling, and testing `.bph` source.
- `webpage/manual.html` — IDE documentation page describing language constructs and usage.
- `webpage/library.html` — page listing macro libraries and reusable example content.
- `webpage/index.html` — landing page that links to the IDE, CFG Generator, and Gravity Hub.
 - `webpage/styles/starter.css` — used by `webpage/ide.html` (IDE styling).

 - `webpage/styles/premade.css` — used by `webpage/premade.html` (premade examples styling).
 - `webpage/styles/library.css` — used by `webpage/library.html` (library/examples styling).
 - `webpage/styles/index.css` — used by `webpage/index.html` (landing page styling).
 - `webpage/styles/doc.css` — used by `webpage/manual.html` and `webpage/Documentation.html` (documentation styling).
- `webpage/scripts/colorhandling.js` — syntax highlighting, code colorization, and editor styling behavior.
- `webpage/scripts/console-resize.js` — dynamic resizing logic for output consoles and IDE panels.
- `webpage/scripts/firmware.js` — firmware selection and target switcher logic in the IDE.
- `webpage/scripts/errorHandler.js` — client-side UI error handling and display utilities.
- `webpage/scripts/demo.js` — demo interaction logic and supplemental frontend behavior.
- `webpage/scripts/mscript.js` — shared macro/script utilities used by the IDE.
- `webpage/scripts/multiprinter.js` — frontend support for managing multiple printer displays and state.

### Gravity Hub (MVP)

- `webpage/hub.html` — Gravity Hub dashboard page for fleet/group management and status overview.
- `webpage/group.html` — Gravity Hub group details page showing individual printer state and controls.
- `webpage/dash.html` — Gravity Hub launcher/dashboard page and navigation entry.
- `webpage/hub.text` — Legacy html
- `webpage/styles/hub.css` — styling for Gravity Hub dashboards, cards, and group layouts.
- `webpage/scripts/hub.js` — Hub dashboard interaction logic and user event handling.
- `webpage/scripts/group.js` — group page logic for Gravity Hub UI updates and interactions.
- `webpage/scripts/hubfolder.js` — Hub navigation helpers and content loading support.

### Core Server & Infrastructure

- `src/main/java/maindeveloper/WebServer.java` — Spark Java server startup, HTTP routing, compile/highlight endpoints, and static file hosting.
- `pom.xml` — Maven project configuration, dependency declarations, and build lifecycle settings.
- `run.bat` — Windows launch script that starts the bundled JRE and opens the local web app.
- `run.sh` — Unix launch script for starting the bundled JRE and serving the app locally.


## Adapter Architecture

The Bellerophon compiler uses a decoupled firmware adapter architecture:

- Source files are parsed by the ANTLR grammar into an AST.
- The compiler engine walks the tree using `GCodeVisitor`.
- Firmware-specific adapters (`KlipperVisitor`, `MarlinVisitor`) implement the abstract emission hooks.

Creating a new adapter is a well-scoped contribution: add a new visitor in `src/main/java/maindeveloper/` and implement the target-specific emission methods.

**How to add a new Firmware Target:**

- **Step 1 (Backend Logic):** Create a new Java class (e.g., `RepRapVisitor.java`) in `src/main/java/maindeveloper/` that extends `GCodeVisitor`.
- **Step 2 (Backend Routing):** Register the new visitor as a compilation target in `WebServer.java`.
- **Step 3 (Frontend UI):** Add the new firmware option to the target dropdown in `webpage/scripts/firmware.js`.
