# Gravity Hub Roadmap

This roadmap describes outcomes, not promised release dates. Work should move
forward when each phase's exit criteria are met. Reorder or split phases as
technical discovery and user feedback provide evidence.

## Phase 0: Define the first complete workflow

**Outcome:** One supported path is specified end to end, from a user's `.bph`
source to a print on a selected machine.

- Choose the first printer ecosystem and a real target device for validation.
- Document the user's flow: source selection, printer/profile selection,
  compile, output review, submission, and job status.
- Inventory that ecosystem's connection, authentication, upload, start,
  status, pause/resume, cancel, and error-reporting capabilities.
- Decide what the app does when compilation or printer communication fails.
- Keep credentials local and out of source files and logs.

**Exit criteria:** The workflow has acceptance criteria and the target's API
and device-validation requirements are understood well enough that
implementation does not assume behaviors the target has not verified.

## Phase 1: First end-to-end printer workflow

**Outcome:** A user can complete one useful Bellerophon-to-printer workflow
without leaving Gravity Hub for the core steps.

- Organize or select a `.bph` source file.
- Select a concrete printer and profile.
- Compile for that printer's target, report compiler errors clearly, and
  preserve the source.
- Review or download the generated output before submission.
- Connect to one selected printer on the local network and submit a job if its
  supported interface permits it.
- Show job status and surface communication or printer errors; only expose
  controls that have been implemented and tested for the target.

**Exit criteria:** The workflow passes on the chosen device, including
connection failure, compile failure, and job-submission failure cases.

## Phase 2: Reliable local printer management

**Outcome:** Users can manage multiple supported printers and jobs with
predictable behavior.

- Add and edit printer connections, verify reachability, and report connection
  state.
- Store per-printer identity, capabilities, profiles, and connection settings
  separately from firmware output dialects.
- Add job history and useful job details where the integration exposes them.
- Add pause, resume, cancel, and related controls only where their semantics
  are verified for that ecosystem.
- Improve error recovery, reconnection behavior, and actionable diagnostics.
- Test permission boundaries, credential storage, and local-network threat
  assumptions.

**Exit criteria:** Supported operations have automated coverage where
possible, documented device coverage, and clear unsupported-operation behavior.

## Phase 3: Expand ecosystem coverage

**Outcome:** Additional printer ecosystems are integrated without coupling
their behavior to the first integration.

Candidate investigations include Klipper via Moonraker (including Mainsail or
Fluidd installations), Prusa connectivity, Bambu connectivity, and
OctoPrint. Prioritize based on user need, supported API access, test hardware
availability, and maintenance cost.

- Define a shared integration boundary for printer discovery/connection,
  capabilities, file transfer, and job operations.
- Implement and validate one ecosystem at a time.
- Record model, firmware, API, authentication, and feature compatibility.
- Keep firmware compilation targets distinct from printer-management
  integrations: they overlap, but neither implies the other.

**Exit criteria:** Each integration has an explicit support matrix and has
been tested against representative hardware or a documented test environment.

## Ongoing work

- Improve Bellerophon compiler and firmware-target correctness independently
  of Hub connectivity.
- Maintain a capability and compatibility matrix in
  [PRINTER_CAPABILITIES.md](./PRINTER_CAPABILITIES.md).
- Gather user feedback and update priorities based on real workflows.
- Keep local-first operation and safe handling of printer actions as
  requirements throughout every phase.
- Keep speculative ideas that are outside the core printer-management scope
  separate from committed roadmap phases; see
  [FUTURE_INVESTIGATIONS.md](./FUTURE_INVESTIGATIONS.md).
