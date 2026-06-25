
# CONTRIBUTING to the Dimidium Ecosystem

Thank you for your interest in contributing! Whether you're a first‑time open source contributor or a seasoned developer, we welcome your help. This guide gives you the essentials to get started.

- For a **high‑level overview** and contribution ideas, read on.
- For **deep technical details** (file‑by‑file breakdown, adapter architecture, etc.), see [`guts.md`](./guts.md).

  
## Contributor License Agreement (Required)

By contributing to this repository, you agree to the Dimidium Contributor License Agreement (CLA).

The CLA grants permission for your contributions to be used in both:
- the open-source Bellerophon project (Apache 2.0)
- proprietary and commercial components of the Dimidium Ecosystem

This CLA does not transfer ownership of your contribution.

See [`CLA.md`](./CLA.md) for full terms.

If you do not agree, please do not submit a pull request.

## Project Context

Dimidium is a self-contained ecosystem for additive manufacturing tooling. It includes:

- **Bellerophon** — the compiler and IDE for a `.bph` domain-specific language that generates firmware-friendly G-code and printer macros.
- **CFG Generator** — a browser-based printer configuration generator and boundary checker for Klipper-style `printer.cfg` files.
- **Gravity Hub** — an MVP dashboard and group management layer surfaced in the local web UI.

This repository combines parser/compiler backend logic, firmware adapter architecture, local launch infrastructure, and a static web frontend.


## 🗺️ Current Milestones & Where to Contribute

If you are looking for a place to jump in, here are the active development milestones we are currently focusing on. Feel free to open a PR for any of these focus areas:

### 1. Bellerophon's Health (Compiler & Parser)
**Goal:** Make the compiler more forgiving, intuitive, and lexically robust.
*   **Whitespace Resilience:** Update the Lexer/Parser to treat arbitrary whitespace and indentation as non-functional. We want to move away from fragile parsing (where an extra space throws an exception) to a token-stream approach that safely skips whitespace.
*   **Error Reporting:** Ensure the error reporter tracks the actual code line accurately, regardless of leading blank lines or comments.

### 2. Variable Support (State Management)
**Goal:** Expand the stateful variable system beyond simple procedural geometry.
*   **Global Variables:** Phase 1 (local-scoped variables within macros) is implemented. The next step is transitioning state management to support global, unit-scoped user-defined variables that persist across the entire compilation unit.

### 3. Gravity Hub Deployment (Networking & Infrastructure)
**Goal:** Transition Gravity Hub from a prototype to a scalable, production-ready fleet manager.
*   **Dynamic Discovery:** Move away from static, manual IP management and implement a dynamic printer discovery and registration system.
*   **Group Logic & Syncing:** Build out the logic to ensure group-level public folders are correctly synced and deployed across the dynamic printer fleet.

### 4. Dimidium's Reach (System Architecture)
**Goal:** Expand ecosystem flexibility and system-level control.
*   **Configuration Management:** Strip out hardcoded defaults (ports, timeout intervals, host addresses) and migrate them to configurable system parameters.
*   **Optimization & Adaptability:** Reduce data handling latency and ensure the ecosystem can be deployed seamlessly across different hardware environments and network architectures.
  

## 🚀 Quick Start

- Want to get the Bellerophon IDE running immediately? Just run the bundled executable from the project root:
- Windows: `.\\run.bat`
- Mac/Linux: `./run.sh`
- Then open `http://localhost:4567` in your browser. (See the Local Environment Setup section for full build details).

## Local Environment Setup

1. Open the repository root in VS Code.
2. Use the bundled Java runtime in `jre/`.
3. Start the app with:

```powershell
.\run.bat
```

or on Unix-like systems:

```bash
./run.sh
```

4. Open the app in your browser at:

```text
http://localhost:4567
```

Notes:

- `WebServer.java` binds the server to `127.0.0.1:4567`.
- Static frontend files are served from the `webpage` folder.

## Submission Guidelines

### Getting Help

- **First, check [KNOWN_ISSUES.md](KNOWN_ISSUES.md)** — Common error messages, workarounds, and current limitations.
- Stuck on writing a new firmware adapter? Not sure how the AST evaluates math? Open a discussion in the GitHub repository or create an Issue tagged with `question`. We are happy to point you in the right direction.

### Reporting Bugs

When filing a bug, include:

- A short summary and reproduction steps.
- Whether you used the bundled JRE or a custom build.
- Example `.bph` input or `printer.cfg` that demonstrates the issue.
- Console logs or Spark server stack traces.

### Pull Requests

- Fork the repo and create a branch named `feature/xxx` or `fix/yyy`.
- Keep PRs focused and small.
- Describe what changed, why it changed, and the impact on Bellerophon, CFG Generator, or Gravity Hub.
- If your PR includes graphical or UI changes (HTML/CSS) to the IDE or Gravity Hub, you **must** include Before and After screenshots or a short GIF in your PR description.
- Reference the issue number with `Fixes #NN` or `Refs #NN`.
- Add examples or regression coverage when practical.
- Document API or behavior changes in the frontend docs if needed.
- By submitting a PR, you confirm you have agreed to the CLA.

### Updating Documentation

- The official Bellerophon manual is maintained via a specific HTML-to-PDF pipeline. To update the documentation:
- Make your text or structural changes directly in `webpage/manual.html`.
- Ensure `webpage/styles/doc.css` is applied correctly.
- Open the HTML file in a web browser and use the browser's native `Print to PDF` function to generate the final document.
- Submit both the updated `.html` file and the newly generated `.pdf` in your PR.

### Code Quality & Safety

Because Bellerophon generates physical motion commands, safety is our top priority. When submitting code:

- Ensure any generated G-code is syntactically valid for the target firmware.
- Ensure your logic plays nicely with the `HardwareLimiter` so axes are not commanded beyond their physical boundaries.
- Keep Java backend code clean and follow existing class structures.

### Review Expectations

- Maintain existing Java and frontend style.
- Prefer incremental, reviewable changes.
- Test the server and verify the app opens at `http://localhost:4567`.
