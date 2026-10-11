# Gravity Hub Roadmap

No release dates are promised here. The next step is to prove a complete
workflow on a real printer, then expand from there.

## 1. Pick a printer and map the workflow

Choose the first printer ecosystem and device to support. Confirm how it
connects, handles credentials, accepts files, starts jobs, and reports status
or errors. Write down the user's path from choosing a `.bph` file through
reviewing and submitting the compiled output.

Before implementation, verify the printer's API and agree on what happens when
compilation or printer communication fails. Keep credentials out of source
files and logs.

## 2. Support one complete print workflow

Let users select a `.bph` file, printer, and profile; compile for the target;
review or download the output; and submit it if the printer supports it. Show
compiler, connection, and printer errors clearly. Only offer job controls
that have been tested on the chosen device.

The workflow should be tested on that device, including compile, connection,
and submission failures.

## 3. Make local printer management reliable

- Add and edit printer connections, and show whether each is reachable.
- Keep printer settings and capabilities separate from firmware output
  dialects.
- Add job history and details when the printer makes them available.
- Support pause, resume, or cancel only when their behavior is verified.
- Improve reconnection, error recovery, and diagnostics.
- Test credential storage, permissions, and local-network security.

Document which devices and operations are supported, and make unsupported
features clear.

## 4. Add more printer ecosystems

Investigate Klipper via Moonraker (including Mainsail and Fluidd), Prusa,
Bambu, and OctoPrint. Prioritize based on user demand, API access, available
test hardware, and maintenance effort. Add and test one ecosystem at a time,
recording model, firmware, authentication, and feature compatibility.

Keep printer integrations separate from firmware compilation targets: support
for one does not guarantee support for the other. Maintain a support matrix
and test each integration on representative hardware or a documented test
environment.

## Ongoing

- Improve Bellerophon compiler and firmware-target correctness.
- Maintain [PRINTER_CAPABILITIES.md](./PRINTER_CAPABILITIES.md).
- Use feedback from real workflows to set priorities.
- Keep local-first operation and safe printer actions in mind throughout.
