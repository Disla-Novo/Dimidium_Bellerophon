
# CONTRIBUTING to the Dimidium Ecosystem

Thank you for your interest in contributing! Whether you're a first‑time open source contributor or a seasoned developer, we welcome your help. This guide gives you the essentials to get started.

- For a **high‑level overview** and contribution ideas, read on.
- For **deep technical details** (file‑by‑file breakdown, adapter architecture, etc.), see [`guts.md`](./guts.md).

  
## Contributor License Agreement (Required)

By contributing to this repository, you agree to the Dimidium Contributor License Agreement (CLA).

The CLA grants permission for your contributions to be used in both:
- the open-source Dimidium ecosystem (AGPL v3.0)
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


## 🌱 How You Can Help (Beginner Friendly!)

First time contributing to open source? Not a Java expert? Dimidium might have a complex compiler under the hood, but you don't need to touch any of that to make a massive impact on the project.

Here are the best ways to jump in:

<table>
  <tr>
    <td valign="top" width="50%">
      <h3>1. The Frontend (HTML & CSS)</h3>
      <p>Gravity Hub and the Bellerophon IDE use local web interfaces that always need visual polish.</p>
      <ul>
        <li><b>What you can do:</b> Fix button alignments, refine colors and spacing, add hover effects, implement themes or add helpful tooltips to the CFG Generator so users know what each setting does.</li>
        <li><b>Skills needed:</b> Basic HTML and CSS.</li>
      </ul>
      <br>
      <h3>2. Documentation & Typos</h3>
      <p>Clear documentation is the backbone of any good open-source project.</p>
      <ul>
        <li><b>What you can do:</b> Read through this README or <code>webpage/manual.html</code>. If you spot a typo, a confusing sentence, or a broken link, open a Pull Request to fix it!</li>
        <li><b>Skills needed:</b> Reading and writing English.</li>
      </ul>
    </td>
    <td valign="top" width="50%">
      <h3>3. Writing Example Scripts</h3>
      <p>The best way for people to learn the <code>.bph</code> language is by looking at examples, and we need more of them.</p>
      <ul>
        <li><b>What you can do:</b> Write short, clever Bellerophon scripts that generate cool procedural geometry or useful printer macros.</li>
        <li><b>Skills needed:</b> Basic understanding of 3D printing and a willingness to play with the Bellerophon IDE.</li>
      </ul>
      <br>
      <h3>4. Real-World Testing</h3>
      <p>Because Dimidium generates physical motion commands, we need people to test the output on real hardware.</p>
      <ul>
        <li><b>What you can do:</b> Run Bellerophon-generated G-code on your 3D printer. Did it move the way you expected? Was the hardware limits correct? Open a Discussion and let us know your results!</li>
        <li><b>Skills needed:</b> You own a 3D printer.</li>
      </ul>
    </td>
  </tr>
</table>

## 🎨 Theme Contributions
Bellerophon uses a data-driven theming system. Adding a new theme is modular and does not require modifying core layout logic.

### Steps to add a theme:
1. **Create your stylesheet:** Create a new file in `/styles/themes/` (e.g., `forest.css`). 
   * **Important:** All your theme-specific styles must be scoped to your unique body class. For example:
     ```css
     body.theme-forest {
         background-color: #0f1a0f;
         ...
     }
     ```
2. **Register the theme:** Open `/scripts/colors_scripts/themes.js`. Add a new entry to the `window.THEME_MAP` object, including the `label`, the `css` path, and a `preview` hex code for the settings modal.
3. **Submit:** Include a screenshot of your theme in your PR so we can verify the syntax highlighting and contrast.

> **⚠️ The Golden Rule:** Do **not** modify `starter.css` or the core layout files. We want to keep the base structure consistent for everyone. One theme per Pull Request, please!


## 🗺️ Current Milestones & Where to Contribute

If you are looking for a place to jump in, here are the active development milestones we are currently focusing on. Feel free to open a PR for any of these focus areas:

<table>
  <tr>
    <td valign="top" width="50%">
      <h3>1. Bellerophon's Health (Compiler & Parser)</h3>
      <p><b>Goal:</b> Make the compiler more forgiving, intuitive, and lexically robust.</p>
      <ul>
        <li><b>Whitespace Resilience:</b> Update the Lexer/Parser to treat arbitrary whitespace and indentation as non-functional. We want to move away from fragile parsing (where an extra space throws an exception) to a token-stream approach that safely skips whitespace.</li>
        <li><b>Error Reporting:</b> Ensure the error reporter tracks the actual code line accurately, regardless of leading blank lines or comments.</li>
      </ul>
      <br>
      <h3>2. Variable Support (State Management)</h3>
      <p><b>Goal:</b> Expand the stateful variable system beyond simple procedural geometry.</p>
      <ul>
        <li><b>Global Variables:</b> Phase 1 (local-scoped variables within macros) is implemented. The next step is transitioning state management to support global, unit-scoped user-defined variables that persist across the entire compilation unit.</li>
      </ul>
    </td>
    <td valign="top" width="50%">
      <h3>3. Gravity Hub Deployment (Networking & Infrastructure)</h3>
      <p><b>Goal:</b> Transition Gravity Hub from a prototype to a scalable, production-ready fleet manager.</p>
      <ul>
        <li><b>Dynamic Discovery:</b> Move away from static, manual IP management and implement a dynamic printer discovery and registration system.</li>
        <li><b>Group Logic & Syncing:</b> Build out the logic to ensure group-level public folders are correctly synced and deployed across the dynamic printer fleet.</li>
      </ul>
      <br>
      <h3>4. Dimidium's Reach (System Architecture)</h3>
      <p><b>Goal:</b> Expand ecosystem flexibility and system-level control.</p>
      <ul>
        <li><b>Configuration Management:</b> Strip out hardcoded defaults (ports, timeout intervals, host addresses) and migrate them to configurable system parameters.</li>
        <li><b>Optimization & Adaptability:</b> Reduce data handling latency and ensure the ecosystem can be deployed seamlessly across different hardware environments and network architectures.</li>
      </ul>
    </td>
  </tr>
  <tr>
    <td valign="top" width="50%">
      <h3>5. UI/UX & Ecosystem Polish</h3>
      <p><b>Goal:</b> Elevate the visual experience, accessibility, and consistency across the entire Dimidium Ecosystem.</p>
      <ul>
        <li><b>Design Unification:</b> Establish a shared visual language (colors, spacing, component behavior) that applies to Bellerophon, the CFG Generator, and Gravity Hub.</li>
        <li><b>Theming & Engine Robustness:</b> Improve the modularity of the theme-loading system to ensure it works seamlessly across all web-based tools in the ecosystem.</li>
        <li><b>CSS Scoping & Cleanup:</b> Eliminate visual inconsistencies by properly scoping styles and removing redundant code across <code>starter.css</code>, <code>dark.css</code>, and tool-specific stylesheets.</li>
        <li><b>Documentation Polish:</b> Update <code>manual.html</code> and other project docs to reflect our current UI/UX, ensuring that tutorials and guides are visually aligned with the software.</li>
        <li><b>Accessibility & Clarity:</b> Standardize hover states, contrast ratios, and layout patterns to make the ecosystem easier to navigate, regardless of the printer hardware or network environment.</li>
      </ul>
    </td>
    <td valign="top" width="50%">
      <h3>Why This Matters</h3>
      <p>We make the ecosystem more professional, more maintainable, and significantly easier to use. A polished UI builds trust with users while comprehensive documentation reduces support burden and lowers the barrier to entry for new contributors and users alike.</p>
      <br>
      <p><b>Key Deliverables:</b></p>
      <ul>
        <li>Unified CSS framework across all tools</li>
        <li>Fully functional theming system (Light/Dark + extensible)</li>
        <li>Updated <code>manual.html</code> with current UI</li>
        <li>User-friendly onboarding elements</li>
      </ul>
    </td>
  </tr>
</table>

---
## 🚀 Quick Start

- Want to get the Bellerophon IDE running immediately? Just run the bundled executable from the project root:
- Windows: `.\run.bat`
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
