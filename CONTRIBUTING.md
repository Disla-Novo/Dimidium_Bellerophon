# CONTRIBUTING to the Dimidium Ecosystem

## Project Context

Dimidium is a self-contained ecosystem for additive manufacturing tooling. It includes:

- **Bellerophon** — the compiler and IDE for a `.bph` domain-specific language that generates firmware-friendly G-code and printer macros.
- **CFG Generator** — a browser-based printer configuration generator and boundary checker for Klipper-style `printer.cfg` files.
- **Gravity Hub** — an MVP dashboard and group management layer surfaced in the local web UI.

This repository combines parser/compiler backend logic, firmware adapter architecture, local launch infrastructure, and a static web frontend.

## 🚀 Quick Start

- Want to get the Bellerophon IDE running immediately? Just run the bundled executable from the project root:
- Windows: `.\\run.bat`
- Mac/Linux: `./run.sh`
- Then open `http://localhost:4567` in your browser. (See the Local Environment Setup section for full build details).

## File-by-File Categorization by Feature

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
- `src/main/java/maindeveloper/TestGCode.java` — local compiler/test harness for validating generated output and debugging compilation behavior.

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
- // Legacy `webpage/Documentation.html` — documentation page covering feature details and compiler behavior.
- `webpage/library.html` — page listing macro libraries and reusable example content.
-  // Legacy `webpage/premade.html` — preset macro examples page for prebuilt workflows.
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

## Local Environment Setup

1. Open the repository root in VS Code.
2. Use the bundled Java runtime in `jre/`.
3. Start the app with:

```powershell
.\run.bat
```

or on Unix-like systems:

```bash
./run.sh
```

4. Open the app in your browser at:

```text
http://localhost:4567
```

5. To build from source:

```bash
./mvnw -q package
java -jar target/SyntaxN.jar
```

6. For live development:

```bash
./mvnw -DskipTests compile exec:java -Dexec.mainClass=maindeveloper.WebServer
```

Notes:

- `WebServer.java` binds the server to `127.0.0.1:4567`.
- Static frontend files are served from the `webpage` folder.

## Submission Guidelines

### Getting Help

- Stuck on writing a new firmware adapter? Not sure how the AST evaluates math? Open a discussion in the GitHub repository or create an Issue tagged with `question`. We are happy to point you in the right direction.

### Reporting Bugs

When filing a bug, include:

- A short summary and reproduction steps.
- Whether you used the bundled JRE or a custom build.
- Example `.bph` input or `printer.cfg` that demonstrates the issue.
- Console logs or Spark server stack traces.

### Pull Requests

- Fork the repo and create a branch named `feature/xxx` or `fix/yyy`.
- Keep PRs focused and small.
- Describe what changed, why it changed, and the impact on Bellerophon, CFG Generator, or Gravity Hub.
- If your PR includes graphical or UI changes (HTML/CSS) to the IDE or Gravity Hub, you **must** include Before and After screenshots or a short GIF in your PR description.
- Reference the issue number with `Fixes #NN` or `Refs #NN`.
- Add examples or regression coverage when practical.
- Document API or behavior changes in the frontend docs if needed.

### Updating Documentation

- The official Bellerophon manual is maintained via a specific HTML-to-PDF pipeline. To update the documentation:
- Make your text or structural changes directly in `webpage/manual.html`.
- Ensure `webpage/styles/doc.css` is applied correctly.
- Open the HTML file in a web browser and use the browser's native `Print to PDF` function to generate the final document.
- Submit both the updated `.html` file and the newly generated `.pdf` in your PR.

### Code Quality & Safety

Because Bellerophon generates physical motion commands, safety is our top priority. When submitting code:

- Ensure any generated G-code is syntactically valid for the target firmware.
- Ensure your logic plays nicely with the `HardwareLimiter` so axes are not commanded beyond their physical boundaries.
- Keep Java backend code clean and follow existing class structures.

### Review Expectations

- Maintain existing Java and frontend style.
- Prefer incremental, reviewable changes.
- Test the server and verify the app opens at `http://localhost:4567`.
