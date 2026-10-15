# Printer Ecosystem Capability 

This is a planning document, not a compatibility guarantee. The current
Gravity Hub is an early UI prototype. Its printer page loads a user-provided
printer web-interface URL in an iframe; it does not integrate with the
printer's management API. Do not treat that iframe as printer-management
support.

## Status definitions

- **Prototype UI only:** A user-facing screen exists, but it does not implement
  the API operations needed for printer management.
- **Research needed:** The API, authentication, supported devices, or behavior
  needs investigation.
- **Not implemented:** No Hub API integration or end-user workflow exists.

## Current Hub prototype

| Area | What exists now | What this does not provide |
| --- | --- | --- |
| Printer groups | A local UI for organizing groups and entering printer names and addresses. | API-based discovery, capability detection, or verified connection state. |
| Printer page | Loads the entered HTTP(S) URL inside an iframe, currently styled to look like a Mainsail interface. | Direct communication with the printer or management service API. |
| File chooser | Opens a local file picker and reads a selected file in the browser. | Sending that file to the printer; the current helper only logs the selected name/content. |
| Bellerophon compiler | The IDE has firmware compiler targets including Klipper, Marlin, and RepRapFirmware. | An integrated Hub workflow that selects a printer, compiles for its profile, and submits the generated output. |
| Job management | Not implemented in Gravity Hub. | Hub-managed upload, start, pause, resume, cancel, progress, history, or printer telemetry. |

The current UI may be useful as a launcher or demonstration, but it is not an
API-backed printer-management implementation.

**The integrations below are candidates for investigation, not commitments.
Their APIs, device support, output formats, and available operations must be
researched and validated before they are treated as supported.**

## Intended API integration direction

The actual printer-management system should communicate with each supported
printer ecosystem through its documented API or protocol, rather than relying
on embedding that ecosystem's web UI. For example, Mainsail and Fluidd are
Klipper frontends; an integration should investigate the underlying Klipper
management API (commonly Moonraker) and validate the supported operations
directly. Other ecosystems need their own API research and device validation.

| Integration candidate | Current Hub API integration | Discovery work |
| --- | --- | --- |
| Klipper / Moonraker (including Mainsail and Fluidd setups) | Not implemented. The Hub can load a supplied web UI URL in an iframe. | Investigate Moonraker connection/authentication, file upload, job lifecycle, status, and supported versions. |
| Prusa | Not implemented. | Identify supported printer families and their documented LAN/API capabilities, authentication, and output requirements. |
| Bambu | Not implemented. | Identify supported models, LAN access and authentication requirements, job operations, and any file/package constraints. |
| OctoPrint | Not implemented. | Investigate its API, authentication, plugin dependencies, file handling, and job lifecycle. |
| RepRapFirmware / Duet | Not implemented. | Identify the appropriate network interface/API, authentication, file operations, and job/status capabilities. |

## Per-integration discovery checklist

For each ecosystem, research and test must be done before commitment:

| Capability | Questions to answer |
| --- | --- |
| Supported devices | Which printer models, firmware versions, and server versions are in scope? |
| Connection | What LAN protocol/API is used? Is discovery supported, or must users enter an address? |
| Authentication | How are credentials or tokens created, stored, refreshed, and revoked? |
| Printer identity | How does the app distinguish printers and retrieve their capabilities and profiles? |
| Output format | Does the printer accept generated G-code directly, or require a slicer, project bundle, or other format? |
| File transfer | How is a file uploaded, named, validated, and replaced? What are the size limits? |
| Job lifecycle | Which start, pause, resume, cancel, and status operations exist, and what do they mean on this target? |
| Telemetry | Which temperatures, progress values, errors, and completion signals can be read reliably? |
| Failure behavior | What happens after timeout, network loss, printer restart, or partial upload? |
| Testing | What hardware, firmware/API versions, and repeatable test procedure validate the integration? |
| Security and privacy | What is stored locally? What network access is required? Is any information sent off-device? |

## Architectural distinction

Keep these concepts separate even when they share a product label:

- **Compiler target:** Translates Bellerophon source into commands or files
  for a firmware dialect, such as Klipper, Marlin, or RepRapFirmware.
- **Printer API integration:** Connects to a printer or its management service
  through an API/protocol and performs supported operations such as listing
  files, uploading output, starting a job, and reading status. This is the
  intended future Hub architecture; it is not implemented by the current
  iframe.
- **Machine profile:** Describes a particular printer's capabilities and
  limits; a firmware name alone is not a complete machine profile.

A compiler target does not automatically provide printer connectivity, and a
printer API integration does not guarantee that arbitrary generated output is
valid for that printer. The end-to-end workflow must explicitly associate a
source file, output target, printer profile, and supported submission path.
