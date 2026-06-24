
<div align="center">
  <img src="https://github.com/user-attachments/assets/4a7732fe-02cb-4732-989d-df65d4456197" width="400" alt="Dimidium Logo">
</div>

# Bellerophon: The Parametric DSL for Additive Manufacturing

Bellerophon is a compiled domain-specific language (DSL) and IDE designed to replace rigid G-code scripts with structured, parametric macros. By acting as a universal abstraction layer, it allows you to write complex procedural geometry and machine control logic once, and compile it directly into target-specific formats like Klipper, Marlin, or other types of firmware. 

Bellerophon IDE: A real-time syntax-validating editor for authoring multi-firmware macros.

CFG Generator & Boundary Checker: A visual configuration app that validates hardware pins and simulates G-code paths to prevent physical bed crashes.

Gravity Hub (Beta): A dynamic fleet deployment layer for managing and syncing compiled scripts across local network printers.

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
M.title "Simple Square"
Home
Absolute
SetSpeed = 3000
MoveTo x=10 y=0 z=0.2
MoveTo x=10 y=10 z=0.2
MoveTo x=0 y=10 z=0.2
MoveTo x=0 y=0 z=0.2
Home
M.end
```

**What this does:**
1. `Home` – Return to origin (0,0,0)
2. `Absolute` – Use absolute positioning (vs `Relative`)
3. `SetSpeed = 3000` – Set print speed (mm/min)
4. Four `MoveTo` commands – Trace the perimeter of a 10mm square at a 0.2mm height. It moves right to create the bottom edge, up for the right edge, left for the top edge, and finally down to close the shape exactly at the origin.
5. `Home` – Return to origin when done

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
