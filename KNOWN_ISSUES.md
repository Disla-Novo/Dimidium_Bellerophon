# Known Issues & Limitations

This document tracks current, unresolved limitations and known issues in
Bellerophon. It is updated with each release. If something here is out of
date, please open an issue or a PR.

---

## Current Limitations

### Firmware Support
- **Supported:** Klipper, Marlin, RepRap

New firmware targets require extending `GCodeVisitor`. See
[CONTRIBUTING.md](CONTRIBUTING.md#adding-firmware-support).

### Language & Compiler
- **Macro recursion:** Calling a macro from itself is not supported.
- **Circular interpolation:** G-code generation uses linear moves only.

### Geometry & Safety
- **Coordinate limits:** All moves are validated against `PrinterProfile`
  bounds at compile time. Out-of-bounds moves throw an error.
  This is intentional; it prevents physical crashes.


### Gravity Hub (Beta)
- **Single network only:** All connected printers must be on the same
  local network. No WAN/cloud routing yet.
- **No job queuing:** Jobs execute immediately. Sequential printing
  requires external orchestration.
- **No failure recovery:** If a print fails mid-job, manual intervention
  is required to resume.


See [CONTRIBUTING.md](CONTRIBUTING.md#getting-help) for how to report issues and ask for help. If bugs were documented, please add them here!
