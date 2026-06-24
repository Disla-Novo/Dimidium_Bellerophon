<div align="center">
  <img src="https://github.com/user-attachments/assets/4a7732fe-02cb-4732-989d-df65d4456197" width="350" alt="Dimidium Logo">
</div>
<p align="center">
  <!-- Row 1: Project Identity -->
<img src="https://img.shields.io/badge/Bellerophon-DSL-black?labelColor=0d9488&style=flat" alt="Bellerophon: DSL">
  <img src="https://img.shields.io/badge/GravityHub-Beta-black?labelColor=0d9488&style=flat" alt="Gravity Hub: Beta">
  <!-- <a href="The link would go here">
  <img src="https://img.shields.io/badge/Community-Discord-black?labelColor=7289da&style=flat" alt="Discord"> </a>-->
  <br>
  <!-- Row 2: Firmware Support -->
  <img src="https://img.shields.io/badge/Klipper-Supported-black?labelColor=orange&style=flat" alt="Klipper">
  <img src="https://img.shields.io/badge/Marlin-Supported-black?labelColor=00B2D6&style=flat" alt="Marlin">
  <br>
  <!-- Row 4: Environment -->
<img src="https://img.shields.io/badge/OS-Windows%20|%20Linux%20|%20macOS-black?labelColor=0078D4&style=flat" alt="Platform">
</p>


# Bellerophon: The Parametric DSL for Additive Manufacturing

Bellerophon is a compiled domain-specific language (DSL) and IDE designed to replace rigid G-code scripts with structured, parametric macros. By acting as a universal abstraction layer, it allows you to write complex procedural geometry and machine control logic once, and compile it directly into target-specific formats like Klipper, Marlin, or other types of firmware.

---

### Key Components

> **Bellerophon IDE** — A real-time syntax-validating editor for authoring multi-firmware macros.
>
> **CFG Generator & Boundary Checker** — A visual configuration app that validates hardware pins and simulates G-code paths to prevent physical bed crashes.
>
> **Gravity Hub (Beta)** — A dynamic fleet deployment layer for managing and syncing compiled scripts across local network printers.

---
## What This Means for You

- **Write Once, Deploy Anywhere** – One script, all firmware.
- **Safety-First** – Simulate before you print. Prevent bed crashes.
- **Parametric Power** – Variables, math, loops. No more hardcoding.
- **100% Local** – All local network features are and will remain free.
---

## First Steps: Your First Macro

New to Bellerophon? Start with this simple example:

```bellerophon
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

## What this does

1. `Home` – Moves the printer to its origin.
2. `Absolute` – Uses absolute positioning so all coordinates are based on the printer’s origin.
3. `SetSpeed = 2000` – Sets a moderate movement speed for the macro (mm/min).
4. `MoveTo x=100 y=100 z=0.2` – Moves to the starting point slightly above the bed.
5. `MoveTo x=150 y=100` – Moves along the X axis.
6. `MoveTo x=150 y=150` – Moves along the Y axis.
7. `MoveTo x=100 y=150` – Moves along the X axis.
8. `MoveTo x=100 y=100` – Returns to the starting point, completing a square path.
9. `Home` – Returns the printer to its origin.

**To try it:**
1. Open the **IDE** at `localhost:4567` (or run `./run.bat` / `./run.sh`)
2. Paste the above code
3. Select **Klipper** or **Marlin** firmware
4. Click **Compile** → see generated G-code
5. (Optional) Use the **CFG Generator** tab to visualize your path on your printer's bed

See [Input_Examples/](Input_Examples/) for more complex examples, or read [KNOWN_ISSUES.md](KNOWN_ISSUES.md) for common patterns and troubleshooting.

---
## Full documentation
See [Official_Bellerophon Language _ Documentation.pdf](https://github.com/Juli132/Dimidium_Bellerophon/blob/5b9c211964540af6fb8f43d44403dcac3e63377a/Bellerophon%20Language%20_%20Documentation.pdf)
 for the complete command reference, examples, and limitations.
