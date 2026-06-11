
<img width="1920" height="1080" alt="Screenshot 2026-06-11 19-19-17" src="https://github.com/user-attachments/assets/bea8d5c2-7f7d-42e5-8f4d-ba8d3aeaa7f8" />


# Bellerophon – A Compiled DSL for 3D Printer Macros

Bellerophon is a compiled domain-specific language (DSL) for 3D printer macros, with an IDE that compiles your code to Klipper or Marlin G-code. It lets you write structured, parametric programs and compile them into firmware-compatible G-code.

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
<img width="1247" height="670" alt="newsttestbng" src="https://github.com/user-attachments/assets/1919befb-fc9b-4076-a43f-34ec8fd9ffd2" />

## Capabilities

- Klipper: support for most Bellerophon features, including if statements, advanced temperature commands, and Klipper-specific G-code macros.
- Marlin:  Simpler output that omits features not available in Marlin firmware. Conditional statements (if/endif) are not supported in Marlin mode;
- all other movement, heating, looping, and macro commands work as expected (loops are expanded at compile time).

- Bellerophon accepts only .bph source files.
- The IDE provides real-time syntax validation, highlighting errors as you type.
- Programs are line-oriented: each command on its own line; blank lines are ignored.
  This is a known issue! Bellerophon does not accept blank spaces in between. 
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
- Dimidium Ecosystem Integration – compiled macros can be deployed and managed through Gravity Hub or executed directly within Mainsail (Klipper) or any Marlin host. [this was left unfinished] 

---


## Full documentation
<img width="595" height="493" alt="Screenshot 2026-04-12 163131" src="https://github.com/user-attachments/assets/4db23b1a-8f6c-4770-b37d-ebbde2541130" />


See [Official_Bellerophon Language _ Documentation.pdf](https://github.com/user-attachments/files/28858644/Official_Bellerophon.Language._.Documentation.pdf)
 for the complete command reference, examples, and limitations.

---

## License

