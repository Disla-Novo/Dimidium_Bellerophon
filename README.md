
<img width="1920" height="1080" alt="Screenshot 2026-06-11 19-19-17" src="https://github.com/user-attachments/assets/bea8d5c2-7f7d-42e5-8f4d-ba8d3aeaa7f8" />


# Bellerophon – A Compiled DSL for 3D Printer Macros

Bellerophon is a compiled domain-specific language (DSL) and IDE designed to replace rigid G-code scripts with structured, parametric macros. By acting as a universal abstraction layer, it allows you to write complex procedural geometry and machine control logic once, and compile it directly into target-specific formats like Klipper and Marlin.

Built entirely as a local-first, cloud-free toolchain, the overarching Dimidium Ecosystem provides a complete end-to-end manufacturing pipeline:

Bellerophon IDE: A real-time syntax-validating editor for authoring multi-firmware macros.

CFG Generator & Boundary Checker: A visual configuration app that validates hardware pins and simulates G-code paths to prevent physical bed crashes.

Gravity Hub (Beta): A dynamic fleet deployment layer for managing and syncing compiled scripts across local network printers.
Note: The server runs locally on port 4567. No internet connection required after installation.

---

## Program Structure


A Bellerophon program consists of one or more macros. Each macro starts with M.title and ends with M.end.

    M.title "Macro Name"
        [statement 1]
        [statement 2]
        [statement 3]
    M.end

- Each command must be on its own line.
- Indentation is optional but recommended.
- Comments start with #.

### Example – a simple move

    M.title "move_left"
    Relative
    MoveTo x = -5
    Absolute
    M.end

---
<img width="1920" height="1080" alt="Screenshot 2026-06-17 21-07-27" src="https://github.com/user-attachments/assets/d352abdc-75a9-48d1-892c-2e84c8140ab8" />

## Capabilities

- Klipper: support for most Bellerophon features, including if statements, advanced temperature commands, and Klipper-specific G-code macros.
-Marlin: Simpler output that omits features not available in Marlin firmware. Conditional statements (if/endif) are not supported in Marlin mode; however, all other movement, heating, looping, and macro commands work as expected (loops are automatically expanded at compile time).

- Bellerophon accepts only .bph source files.
- The IDE provides real-time syntax validation, highlighting errors as you type.
-Line-Oriented Parsing: In the current build, the compiler utilizes strict line-by-line parsing. Each command must exist on a continuous, contiguous line without empty line breaks separating execution statements.
- Supported commands include: movement, temperature control, heaters, fans, waits, pauses, macro calls, repeat loops, Brepeat loops, conditional execution (Klipper only), extruder control, and bed/probe operations.

- Compiles .bph scripts into either Klipper macro .cfg files or Marlin .gcode files.
- Compiled macros can be deployed via Gravity Hub or directly from Mainsail (Klipper) or any Marlin host.
- The IDE provides Build Log and Exceptions tabs for debugging compilation errors.

- Arithmetic expressions in coordinates and parameters – globally available.
- Mathematical functions: sin, cos, tan, sqrt, abs, sign, power operator ^, and constant pi.
- Parametric loops using repeat (static repetition) and Brepeat (dynamic, iteration-aware), including nested loops and iterator-based expressions.
- Parametric Geometry – generate circles, spirals, or arbitrary curves.
- Conditional execution with if (Klipper only).
- Temperature management for extruder, bed, and chamber (Set_Heater_Temperature, Heat, WaitForTemp).
- Movement commands: absolute/relative positioning, X/Y/Z moves, extruder control, dwell/wait.
- Macro calls within macros (M.call).
- Bed and probe management: BED_MESH_CALIBRATE, PROBE_CALIBRATE, LoadBedMesh.
- Fan control, pressure advance, speed settings, idle timeout (TIMEOUT_SET).
- Pause, resume, cooldown, SD card file printing (PRINTFILE).

- IDE Reference Panel – displays all Bellerophon tokens. Users can pin commands to a sidebar for quick access while typing.
- Session Restoration – the IDE automatically restores the previous editing session to prevent accidental data loss.
- Dimidium Ecosystem Integration (Beta/Roadmap): Compiled macros are designed to be deployed and managed across a dynamic fleet via Gravity Hub (currently in active pipeline development).
- Printer Profile UI: The IDE provides a graphical interface to set hardware limits and default printing parameters (nozzle diameter, filament
diameter, layer height, extrusion multiplier). These settings are used as defaults unless overridden by macro commands.
- Local user variables – define and reuse variables inside macros.
- Hardware Configuration: Includes a built-in CFG Generator and visual Boundary Checker web app to generate foundational machine configurations and simulate G-code paths against physical bed dimensions to prevent mechanical crashes.

---


## Full documentation
<img width="595" height="493" alt="Screenshot 2026-04-12 163131" src="https://github.com/user-attachments/assets/4db23b1a-8f6c-4770-b37d-ebbde2541130" />


See [Official_Bellerophon Language _ Documentation.pdf](https://github.com/user-attachments/files/28858644/Official_Bellerophon.Language._.Documentation.pdf)
 for the complete command reference, examples, and limitations.

---

## License
Dimidium is free for personal, educational, and non-commercial use! You are completely free to download, modify, and share this software.

Commercial use is strictly prohibited without explicit permission. If you are a company or individual wanting to use Dimidium for commercial purposes, please contact me directly at dislaofficialt@gmail.com  to discuss a commercial license.

Disclaimer:
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
