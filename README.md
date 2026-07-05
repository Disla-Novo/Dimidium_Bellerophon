<div align="center">
  <a href="https://github.com/Juli132/Dimidium_Bellerophon/">
<img width="1920" height="673" alt="2026-06-2421-59-46-ezgif com-crop" src="https://github.com/user-attachments/assets/c8ee376f-ee58-4ebb-901a-757a6417f868" />
  </a>
</div>
<p align="center">
  <!-- Row 1: Project Identity -->
  <a href="Bellerophon%20Language%20_%20Documentation.pdf"><img src="https://img.shields.io/badge/Bellerophon-DSL-black?labelColor=0d9488&style=flat" alt="Bellerophon: DSL"></a>
  <a href="https://github.com/Juli132/Dimidium_Bellerophon"><img src="https://img.shields.io/badge/GravityHub-Beta-black?labelColor=0d9488&style=flat" alt="Gravity Hub: Beta"></a>
  <br>

  <!-- Row 2: Firmware Support -->
<a href="https://github.com/Klipper3d" target="_blank" rel="noopener noreferrer">
  <img src="https://img.shields.io/badge/Klipper-Supported-black?labelColor=ff0000&style=flat" alt="Klipper" style="pointer-events: none;">
</a>

<a href="https://github.com/MarlinFirmware" target="_blank" rel="noopener noreferrer">
  <img src="https://img.shields.io/badge/Marlin-Supported-black?labelColor=orange&style=flat" alt="Marlin" style="pointer-events: none;">
</a>
  <br>

  <!-- Row 3: Environment & License -->
<a href="https://github.com/Juli132/Dimidium_Bellerophon">
  <img src="https://img.shields.io/badge/OS-Windows%20|%20Linux%20|%20macOS-black?labelColor=0078D4&style=flat" alt="Platform">
</a>
 <a href="LICENSE" style="text-decoration: none;">
  <img src="https://img.shields.io/badge/License-AGPL_3.0-blue.svg" alt="License" style="vertical-align: top;">
</a>
</p>

# Bellerophon: The Parametric DSL for Additive Manufacturing

Bellerophon is a compiled domain-specific language (DSL) and IDE designed to replace rigid G-code scripts with structured, parametric macros. By acting as a universal abstraction layer, it allows you to write complex procedural geometry and machine control logic once, and compile it directly into target-specific formats like Klipper, Marlin, or other types of firmware.

---


<img width="800" height="450" alt="ezgif com-video-to-gif-converter" src="https://github.com/user-attachments/assets/a1373966-a26f-45a7-bb5d-6fc54576433a" />



## First Steps: Your First Macro
```
M.title "first_line"
Absolute
SetSpeed = 2000
Home
MoveTo x=100 y=100 z=0.2
MoveTo x=150 y=100
MoveTo x=150 y=150
MoveTo x=100 y=150
MoveTo x=100 y=100
Home
M.end
```

### What this does
1. Home – Moves the printer to its origin.
2. Absolute – Uses absolute positioning so all coordinates are based on the printer’s origin.
3. SetSpeed = 2000 – Sets a moderate movement speed for the macro (mm/min).
4.  MoveTo x=100 y=100 z=0.2 – Moves to the starting point slightly above the bed.
5.  MoveTo x=150 y=100 – Moves along the X axis.
6.  MoveTo x=150 y=150 – Moves along the Y axis.
7.   MoveTo x=100 y=150 – Moves along the X axis.
8.   MoveTo x=100 y=100 – Returns to the starting point, completing a square path.
9.   Home – Returns the printer to its origin.


## Windows Security Prompts (What to Expect)



<img width="462" height="343" alt="Screenshot 2026-07-04 202921" src="https://github.com/user-attachments/assets/51dbfb1d-40c8-43fb-825a-30527755cf8d" />


*Example of the Windows security warning you may see when running for the first time.*


- **First-run firewall popup:** Because the app opens a local web server, Windows may display a "Windows Defender Firewall" dialog asking whether to allow the app to communicate on networks. This is normal! For the official release it is safe to click "Allow access".
- **SmartScreen / Defender warning:** For unsigned downloads you may see a "Windows protected your PC" SmartScreen message. If you see it, click "More info" then "Run anyway".

*The bundled JRE is runtime-only.*

## Which Release to Pick

| Release | Description | Best For |
|---------|-------------|----------|
| **User Release** (recommended) | Prebuilt package with bundled JRE (no system Java required) | End users who just want to run the app |
| *Devkit* | Includes JRE 19 for local development and building | Contributors who want to build from source |

## Developers

For local development instructions, see [CONTRIBUTING.md](CONTRIBUTING.md#local-development).

## Quick Start

1. **Download:** Grab the latest release from the [Releases page](https://github.com/Juli132/Dimidium_Bellerophon/releases).
2. **Run:** Unzip the folder and launch the application:
   - **Windows:** Double-click `run.bat`
   - **Mac/Linux:** Run `./run.sh`
3. **Compile:**
   - Paste your code into the editor.
   - Select your firmware dialect (**Klipper** or **Marlin**).
   - Click **Compile** to generate your G-code.
4. **Visualize (Optional):** Use the **CFG Generator** tab to preview your path on the printer bed.


---

## What This Means for You

- **Write Once, Deploy Anywhere** – One script, all firmware.
- **Safety-First** – Simulate before you print. Prevent bed crashes.
- **Parametric Power** – Variables, math, loops. No more hardcoding.
- **100% Local** – All local network features are and will remain free.

---

## Key Components

> **Bellerophon IDE** — A real-time syntax-validating editor for authoring multi-firmware macros.
>
> **CFG Generator & Boundary Checker** — A visual configuration app that validates hardware pins and simulates G-code paths to prevent physical bed crashes.
>
> **Gravity Hub (Beta)** — A dynamic fleet deployment layer for managing and syncing compiled scripts across local network printers.

---

## Compilation Architecture

Bellerophon uses a decoupled Firmware Adapter Architecture. Your `.bph` source code remains universal, while the compiler handles the conversion to firmware-specific instructions via targeted adapters.

- **Universal Source:** Write once, deploy anywhere.
- **Modular Adapters:** Choose firmware target in IDE before compiling.
- **Firmware Independence:** New adapters can be added without changing scripts.

---

## IDE & Workflow

- **Input:** `.bph` scripts with real-time validation
- **Debugging:** Build Log + Exceptions
- **Session Management:** Workspace persistence
- **Reference Panel:** Built-in token documentation

---

## Full documentation

See [Official_Bellerophon Language _ Documentation.pdf](Bellerophon%20Language%20_%20Documentation.pdf)
for the complete command reference, examples, and limitations.
