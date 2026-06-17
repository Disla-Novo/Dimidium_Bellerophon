// token colors
// keep adding more as we go. 
// the ones that should be added is MSG 
// RelativeExtrusion 
// do i have an absoluteextrusion?
// LOADBEDMESH. BED_MESH_CALIBRATE, PROBE_CALIBRATE
// SET_PRESSURE_ADVANCE, RESET_EXTRUDER. there should be more to update! 
// colors to avoid. GREEN ARE FOR COMMENTS ONLY. red kind of for errors. but the dimmer reds are for heats, fans, and speeds.
// maybe blue and cyan for calls, responses, and other things. 
// might change x, y, z colors. seems good for now
const tokenColors = {
    TITLE: "#6b69e0",
    MEND: "#6b69e0",
    HOME: "#61afef",
    MOVE: "#61afef",
    ID: "#abb2bf", // to be caught!  so it should be dimmer than the default white. lets see if we can tell. 
    UNRECOGNIZED: "#e91d2e", // errors hopefully
    DIRECTION: "#c256bd",
    HEAT: "#e06c75",
    TARGET: "#98c379",
    LEVEL: "#61afef",
    CALL: "#56b6c2",
    IF: "#61afef",
    ENDIF: "#61afef",
    ARROW: "#56b6c2",
    EQUALS: "#abb2bf",
    NUMBER: "#d19a66",
    STRING: "#98c379",
    SEMICOLON: "#abb2bf",
    X: " #e5c07b",
    Y: "#e5c07b",
    Z: "#e5c07b",
    E: "#e5c07b",
    COMPARE: "#5c6370",
    PAUSE: "#d19a66",
    RESPOND: "#56b6c2",
    SET_HEATER: "#e06c75",
    WAITFORTEMP: "#e06c75",
    MOVEEX: "#61afef",
    ABSOLUTE: "#61afef",
    RELATIVE: "#61afef",
    COOLDOWN: "#e06c75",
    SET_SPEED: "#e06c75",
    SET_FAN: "#e06c75",
    PRINTFILE: "#d19a66",
    MSG: "#1dd3ca",
    REPEAT: "#c678dd", // purple-ish for loops
    END: "#c678dd",
    PLUSEQ: "#abb2bf",
    MINUSEQ: "#abb2bf",
    MULTEQ: "#abb2bf",
    DIVEQ: "#abb2bf",
    PLUS: "#abb2bf",
    MINUS: "#abb2bf",
    BREPEAT: "#c678dd", // purple-ish for loops



    
};


// Convert lexer type number to token name
function getTokenName(type) {
    return JupitoreLexer.VOCABULARY.getSymbolicName(type);
}

window.addEventListener('DOMContentLoaded', () => {
    const input = document.getElementById("code-input");
    const overlay = document.getElementById("code-overlay"); // this sucked on css but it seemed like the best option

   async function highlightCode() {
    const codeInput = input.value;

    const response = await fetch("/highlight", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ code: codeInput })
    });

    const tokens = await response.json();
    console.log("Tokens received:", tokens);
    let html = "";
    let lastIndex = 0;

    tokens.forEach(t => {
       



        if (t.start > lastIndex) {
            html += escapeHTML(codeInput.slice(lastIndex, t.start));
        }

        let color = tokenColors[t.name] || "white";   // WHITE
        


if (t.name === "ID") {
   
    let nextIndex = t.end + 1;
    while (nextIndex < codeInput.length && codeInput[nextIndex].match(/\s/)) {
        nextIndex++;
    }
    const nextChar = codeInput[nextIndex] || '';
    if (nextChar === '=') {
        color = "#dd9919"; // assignment target
    } else {
       
        let prevIndex = t.start - 1;
        while (prevIndex >= 0 && codeInput[prevIndex].match(/\s/)) {
            prevIndex--;
        }
        const prevChar = codeInput[prevIndex] || '';
        
        if (['=', '+', '-', '*', '/', '(', ','].includes(prevChar)) {
            color = "#dd9919";
        } else {
          
            color = tokenColors.ID || "#abb2bf";
        }
    }
}




        //  token in a span
        html += `<span style="color: ${color}">${escapeHTML(t.text)}</span>`;
        
        // inclusive or something
        lastIndex = t.end + 1;
    });

    //   remaining text at the end of the file
    if (lastIndex < codeInput.length) {
        html += escapeHTML(codeInput.slice(lastIndex));
    }


    overlay.innerHTML = html + (codeInput.endsWith('\n') ? ' ' : '');

    // Sync scrolling
    overlay.scrollTop = input.scrollTop;
    overlay.scrollLeft = input.scrollLeft;
}

    function escapeHTML(str) {
        return str.replace(/&/g, "&amp;")
                  .replace(/</g, "&lt;")
                  .replace(/>/g, "&gt;")
                  .replace(/"/g, "&quot;");
    }

    input.addEventListener("input", highlightCode);
   
function syncEditor() {
    overlay.scrollTop = input.scrollTop;
    overlay.scrollLeft = input.scrollLeft;
    
    // Also sync line numbers if they exist
    const gutter = document.getElementById('line-numbers');
    if (gutter) gutter.scrollTop = input.scrollTop;
}



input.addEventListener("scroll", syncEditor);
input.addEventListener("input", () => {
    highlightCode();
    // updateLines(); 
});
    // Initial highlight
    highlightCode();
});
