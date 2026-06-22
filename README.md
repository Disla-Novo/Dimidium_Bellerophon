

<img width="1920" height="1080" alt="Screenshot 2026-06-21 16-41-05" src="https://github.com/user-attachments/assets/f183a2cc-0eb3-400e-80fb-a63b39963ac6" />



# Bellerophon – The Parametric DSL for Additive Manufacturing

Bellerophon is a compiled domain-specific language (DSL) and IDE designed to replace rigid G-code scripts with structured, parametric macros. By acting as a universal abstraction layer, it allows you to write complex procedural geometry and machine control logic once, and compile it directly into target-specific formats like Klipper, Marlin, or other types of firmware. 

Bellerophon IDE: A real-time syntax-validating editor for authoring multi-firmware macros.

CFG Generator & Boundary Checker: A visual configuration app that validates hardware pins and simulates G-code paths to prevent physical bed crashes.

Gravity Hub (Beta): A dynamic fleet deployment layer for managing and syncing compiled scripts across local network printers.

---
## What This Means for You

- **Write Once, Deploy Anywhere** – One script, all firmware.
- **Safety-First** – Simulate before you print. Prevent bed crashes.
- **Parametric Power** – Variables, math, loops. No more hardcoding.
- **100% Free & Local** – All local network features are and will remain free.
---
## Capabilities
<img width="1920" height="1080" alt="Screenshot 2026-06-17 21-07-27" src="https://github.com/user-attachments/assets/d352abdc-75a9-48d1-892c-2e84c8140ab8" />

### A Parametric Engine, Not Just G-Code

Stop hardcoding static values. Bellerophon gives you real programming power inside your manufacturing workflow:

- **Math & Logic:** Use variables, arithmetic, and functions (`sin`, `cos`, `sqrt`, `abs`) to define motion dynamically.
- **Loops & Iteration:** Generate complex geometry with `repeat` and `Brepeat` — including nested loops and iterator-based expressions.
- **Conditional Logic:** Use `if` statements to make your macros smart and responsive (firmware-specific).
- **Parametric Geometry:** Create circles, spirals, or arbitrary curves without manually calculating every point.

### Smart Extrusion

Bellerophon's `EnableAutoExtrude` engine calculates path-aware extrusion volumes automatically. Need manual control? You can override with precision. Either way, your extrusion is always optimized for the hardware you're targeting.

### Built-In Safety & Validation

<img width="595" height="493" alt="Screenshot 2026-04-12 163131" src="https://github.com/user-attachments/assets/4db23b1a-8f6c-4770-b37d-ebbde2541130" />

Before your macro ever reaches the printer, Bellerophon checks it:

- **Boundary Checking:** The compiler simulates your paths against your hardware dimensions, so you catch potential bed crashes before they happen.
- **Real-Time Syntax Validation:** The IDE highlights errors as you type, so you don't waste time debugging after compilation.
- **Build Log & Exceptions:** Transparent feedback on exactly what went wrong and where.
--
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

### Example

    M.title "move_left"
    Relative
    MoveTo x = -5
    Absolute
    M.end

---



## Full documentation
See [Official_Bellerophon Language _ Documentation.pdf](https://github.com/Juli132/Dimidium_Bellerophon/blob/05cd10f3a161eebd50cf3e2cab236c9d0c7ba68c/Bellerophon%20Language%20_%20Documentation.pdf)
 for the complete command reference, examples, and limitations.

---

## License
Dimidium is free for personal, educational, and non-commercial use! You are completely free to download, modify, and share this software.

Commercial use is strictly prohibited without explicit permission. If you are a company or individual wanting to use Dimidium for commercial purposes, please contact me directly at dislaofficialt@gmail.com  to discuss a commercial license.

Disclaimer:
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
