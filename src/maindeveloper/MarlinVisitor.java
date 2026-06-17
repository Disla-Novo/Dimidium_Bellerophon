package maindeveloper;

import jupitore.gen.JupitoreParser;

// here we just add overrides that arent supported. 
// these typically are just word commands that marlin doesn't support. 
// awaiting a few more, dont forget to add. 

public class MarlinVisitor extends GCodeVisitor {
    public MarlinVisitor(PrinterProfile profile) {
    super(profile); // calls the GCodeVisitor constructor above
}

    /** 
     * @param ctx
     * @return String
     */
    @Override
    public String visitMacro(JupitoreParser.MacroContext ctx) {
        // remove macro wrapper
        StringBuilder gcode = new StringBuilder();

        currentX = 0;
        currentY = 0;
        currentZ = 0;
        relativeMode = false;

        if (ctx.TITLE() != null && ctx.STRING() != null) {
            String macroName = ctx.STRING().getText().replace("\"", "");
            gcode.append("; ").append(macroName).append("\n");
        }

        if (ctx.statement() != null && !ctx.statement().isEmpty()) {
            for (JupitoreParser.StatementContext stmt : ctx.statement()) {
                String stmtCode = visit(stmt);
                if (stmtCode != null && !stmtCode.isEmpty()) {
                    gcode.append(stmtCode);
                }
            }
        }

        return gcode.toString();
    }

    /** 
     * @param ctx
     * @return String
     */
    // OVERRIDE KLIPPER-SPECIFIC COMMANDS

    @Override
    public String visitStatement(JupitoreParser.StatementContext ctx) {

        // adding cooldown stuff for marlin

        if (ctx.COOLDOWN() != null) {
            if (ctx.TARGET() == null) {
                // Turn off all heaters
                return "M104 S0\nM140 S0\nM141 S0\n";
            } else {
                // Turn off specific heater
                String t = ctx.TARGET().getText().toLowerCase();
                switch (t) {
                    case "extruder":
                        return "M104 S0\n";
                    case "bed":
                        return "M140 S0\n";
                    case "chamber":
                        return "M141 S0\n";
                }
            }
        }

        // NEW marlin specific. apparently it listens to 'R' and not 'S'.
        // 4/4/2026
        if (ctx.HEAT() != null) {
            if (ctx.TARGET() != null && ctx.expr() != null) {
                String target = ctx.TARGET().getText().toLowerCase();
                Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
                double value = compute.visit(ctx.expr());

                switch (target) {
                    case "extruder":

                        return "M109 R" + (int)value + "\n";
                    case "bed":

                        return "M190 R" + (int)value + "\n";
                    case "chamber":
                        return "M141 S" + (int)value + "\n";
                }
            }
        }

        // 4/4/2026
        if (ctx.WAITFORTEMP() != null && ctx.TARGET() != null) {
            String target = ctx.TARGET().getText().toLowerCase();
            switch (target) {
                case "extruder":
                    return "M109\n";
                case "bed":
                    return "M190\n";
                case "chamber":
                    return "M141\n";
            }
        }

        // SET_FAN...Marlin uses M106
        if (ctx.SET_FAN() != null && ctx.expr() != null) {
            Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
            double speed = compute.visit(ctx.expr());
            // Marlin fan speed is 0-255; Bellerophon uses 0-1 range
            int marlinSpeed = (int) (speed * 255);
            return "M106 S" + marlinSpeed + "\n";
        }

        // PAUSE
        if (ctx.PAUSE() != null) {
            return "M25\n"; // Marlin pause
        }

        // RESUME
        if (ctx.RESUME() != null) {
            return "M24\n"; // Marlin resume
        }

        // RESPOND
        if (ctx.RESPOND() != null && ctx.STRING() != null) {
            String message = ctx.STRING().getText().replace("\"", "");
            return "M117 " + message + "\n"; // Marlin LCD message
        }

        // LEVEL / BED_MESH_CALIBRATE
        if (ctx.LEVEL() != null || ctx.BED_MESH_CALIBRATE() != null) {
            return "G29\n"; // Marlin bed leveling
        }

        // PROBE_CALIBRATE (Marlin uses G30 for single probe, no direct equivalent)
        if (ctx.PROBE_CALIBRATE() != null) {
            return "G30\n"; // Single probe point (simplified)
        }

        // LOAD_BED_MESH
        if (ctx.LOAD_BED_MESH() != null && ctx.STRING() != null) {
            String mesh = ctx.STRING().getText().replace("\"", "");
            return "G29 L" + mesh + "\n"; // Marlin load mesh
        }

        // SET_PRESSURE_ADVANCE
        if (ctx.SET_PRESSURE_ADVANCE() != null && ctx.expr() != null) {
            Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
            double value = compute.visit(ctx.expr());
            return "M900 K" + value + "\n"; // Marlin linear advance
        }

        // TIMEOUT_SET
        if (ctx.TIMEOUT_SET() != null && ctx.expr() != null) {
            Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
            double seconds = compute.visit(ctx.expr());
            return "M84 S" + (int)seconds + "\n"; // Marlin idle timeout
        }

        // PRINTFILE
        if (ctx.PRINTFILE() != null && ctx.STRING() != null) {
            String file = ctx.STRING().getText().replace("\"", "");
            return "M23 " + file + "\nM24\n"; // Marlin select + start print
        }

        if (ctx.CALL() != null && ctx.STRING() != null) {
            String macroName = ctx.STRING().getText().replace("\"", "");
            System.err.println("Warning: Macro call '" + macroName + "' not supported in Marlin mode");
            return "; CALL " + macroName + " (ignored in Marlin)\n";
        }

        // For everything else, call parent
        return super.visitStatement(ctx);
    }

    /** 
     * @param ctx
     * @return String
     */
    @Override
    public String visitIf_statement(JupitoreParser.If_statementContext ctx) {
        String condition = ctx.condition().getText();

        // Optionally extract the condition to show what was skipped
        return "; Marlin does not support if statements\n; Skipped: if " + condition + "\n";
    }
}