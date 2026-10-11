# Gravity Hub Vision

## The idea

Gravity Hub is the printer-management layer of the Dimidium ecosystem. It
should let a user take a Bellerophon (`.bph`) source file from authoring to a
managed print workflow across supported printer and firmware ecosystems.

The long-term experience is more than a launcher for printer web interfaces:

1. Create, import, organize, and reuse Bellerophon source files.
2. Choose a specific printer and its profile, not just a broad firmware label.
3. Compile the source for that machine's supported target.
4. Review the generated output and relevant safety checks before sending it.
5. Connect to printers on the user's local network, submit jobs, and monitor
   and control those jobs where the printer integration allows it.
6. Manage multiple printers and their configurations from one coherent
   interface.

Potential integrations include Klipper installations managed through Mainsail
or Fluidd, Prusa printers, Bambu printers, and OctoPrint-connected printers.
These are distinct ecosystems, not interchangeable connection methods. Each
may have different APIs, authentication, file formats, job controls, and
firmware behavior.

## Product principles

- **Local-first:** LAN workflows should be a first-class use case and should
  not require a cloud account. Any remote-access or cloud feature needs a
  separate security and privacy design.
- **Explicit target behavior:** Show which printer, profile, and output target
  will be used. Do not imply that one generated file or command set works on
  every machine.
- **Review before action:** Make the generated output and the destination
  clear before a job is submitted or a printer is controlled.
- **Safe by default:** Validate what can be validated, surface uncertainty,
  and require clear user intent for consequential printer actions.
- **Adapters at the boundaries:** Keep Bellerophon compilation, printer
  connectivity, and job management separable so one ecosystem does not dictate
  the design of all others.
- **Support honesty:** Distinguish implemented, tested, and
  device-validated capabilities from planned or experimental work.

## What Gravity Hub is today

The current Hub is an early local UI for printer groups and links to printer
web interfaces. It is a starting point, not the complete product described
above. See [ROADMAP.md](./ROADMAP.md) for the core staged path and
[PRINTER_CAPABILITIES.md](./PRINTER_CAPABILITIES.md) for a view of what exists
and what still needs investigation.
