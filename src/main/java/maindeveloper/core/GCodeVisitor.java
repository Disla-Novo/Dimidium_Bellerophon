package maindeveloper.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;
import java.util.HashMap;
import java.util.Map;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

// 6/28/2026
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import jupitore.gen.*;

public abstract class GCodeVisitor extends JupitoreBaseVisitor<String> {
    // ADDED 4/10/2026
    protected PrinterSettings settings = new PrinterSettings();
    protected boolean enablePaging = false;
    protected boolean autoExtrudeEnabled = false;
    // 6/17/26
    // plain "x = expr" assignments - reset at the start of every macro, so a
    // variable set in one macro doesn't leak into the next
    protected Map<String, Double> localVariables = new HashMap<>();
    // "var x = expr" assignments - persist for the whole compilation unit,
    // visible from every macro
    protected Map<String, Double> globalVariables = new HashMap<>();
    // Temporary storage for a single move (used in visitCoordList)
    private double targetX = Double.NaN;
    private double targetY = Double.NaN;
    private double targetZ = Double.NaN;
    private boolean hasManualE = false;
    private double manualEValue = 0.0;
    private boolean hasAutoE = false; // user wrote "E" without value
    protected double centerX = 0; // <-- Jrepeat center for X
    protected double centerY = 0; // <-- Jrepeat center for Y
    protected boolean insideLayer = false;
    // Hardware safety limiter: enforces axis bounds at compile time.
    protected HardwareLimiter limiter;
    protected Stack<Integer> iterationStack = new Stack<>();
    protected boolean relativeMode = false;
    protected double currentX = 0;
    protected double currentY = 0;
    protected double currentZ = 0;
    protected boolean insideJrepeat = false;

  
    // ---- ABSTRACT FIRMWARE METHODS ----
    // NOTE: Some method names are Klipper-centric for historical reasons,
    // but their implementation intent is completely generic. They can be 
    // mapped to emit the correct G-code syntax for any target dialect 
    // (Marlin, RepRap, Klipper, etc.) without altering the core visitor logic.
    // -------------------------------------------------------------------
    protected abstract String emitMacroHeader(String macroName);
    protected abstract String emitHeat(String target, double value, boolean wait);
    protected abstract String emitSetHeater(String target, double value);
    protected abstract String emitCooldown(String target);
    protected abstract String emitWaitForTemp(String target);
    protected abstract String emitHome(String coordList);
    protected abstract String emitMove(String direction);
    protected abstract String emitMoveTo(String coordList);
    protected abstract String emitSetSpeed(double value);
    protected abstract String emitSetFan(double value);
    protected abstract String emitAbsolute();
    protected abstract String emitRelative();
    protected abstract String emitRelativeExtrusion();
    protected abstract String emitResetExtruder();
    protected abstract String emitPause();
    protected abstract String emitResume();
    protected abstract String emitDwell(double milliseconds);
    protected abstract String emitTimeoutSet(double seconds);
    protected abstract String emitRespond(String message);
    protected abstract String emitPrintFile(String filename);
    protected abstract String emitMacroCall(String macroName);
    protected abstract String emitBedMeshCalibrate();
    protected abstract String emitLoadBedMesh(String profile);
    protected abstract String emitProbeCalibrate();
    protected abstract String emitSetPressureAdvance(double value);
    @SuppressWarnings("unused")
    protected abstract String emitSetNozzle(double value);
    @SuppressWarnings("unused")
    protected abstract String emitSetFilament(double value);
    @SuppressWarnings("unused")
    protected abstract String emitSetLayerHeight(double value);
    @SuppressWarnings("unused")
    protected abstract String emitSetExtrusionMultiplier(double value);
    @SuppressWarnings("unused")
    protected abstract String emitEnableAutoExtrude(boolean enabled);
    protected abstract String emitIfStart(String condition);
    protected abstract String emitIfEnd();
    protected abstract String emitLayerStart(int layer);
    protected abstract String emitLayerEnd();

    // sourceFilePath for insert gcode
    protected String sourceFilePath = null; // Track the .bph file being compiled

    public void setEnablePaging(boolean enable) {
        System.out.println("VISITOR LOG: Paging has been set to: " + enable);
        this.enablePaging = enable;
    }

    public GCodeVisitor(PrinterProfile profile) {
        System.out.println("=== DEBUG: GCodeVisitor constructor ===");
        System.out.println("Profile maxX = " + profile.getMaxX());
        System.out.println("Profile maxY = " + profile.getMaxY());
        System.out.println("Profile maxZ = " + profile.getMaxZ());
        this.limiter = new HardwareLimiter(profile.getMaxX(), profile.getMaxY(), profile.getMaxZ());
        this.settings.setNozzleDiameter(profile.getNozzleDiameter());
        this.settings.setFilamentDiameter(profile.getFilamentDiameter());
        this.settings.setLayerHeight(profile.getLayerHeight());
        this.settings.setExtrusionMultiplier(profile.getExtrusionMultiplier());
    }

    // 4/10/2026 adding paging support to the visitor! lets see if this actually works
    @Override
    public String visitProgram(JupitoreParser.ProgramContext ctx) {

        // DEBUG: print all children of the program node
        System.out.println("=== visitProgram: " + ctx.getChildCount() + " children ===");
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            System.out.println("  child " + i + ": " + child.getClass().getSimpleName() + " -> " + child.getText());
        }

        if (this.enablePaging) {
            try {
                // Create a temporary file to store long gcodes
                File tempFile = File.createTempFile("bph_scratch_", ".gcode");
                System.out.println("PAGING ACTIVE: Writing to " + tempFile.getAbsolutePath());
                tempFile.deleteOnExit(); // JVM cleans up on exit

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                    for (JupitoreParser.MacroContext macro : ctx.macro()) {
                        String macroCode = visit(macro);
                        if (macroCode != null && !macroCode.isEmpty()) {
                            writer.write(macroCode);
                            writer.write("\n");
                            // This flushes the RAM so the garbage collector
                            // can take the space used by the 'macroCode' string.
                            writer.flush();
                        }
                    }
                }

                return "SUCCESS_PAGED:" + tempFile.getAbsolutePath();

            } catch (IOException e) {
                throw new RuntimeException("Memory Paging Failed: " + e.getMessage());
            }
        }

        StringBuilder gcode = new StringBuilder();
        for (JupitoreParser.MacroContext macro : ctx.macro()) {
            String macroCode = visit(macro);
            if (macroCode != null && !macroCode.isEmpty()) {
                gcode.append(macroCode).append("\n");
            }
        }

        String result = gcode.toString();
        return result.isEmpty() ? "" : result;
    }

    @Override
    public String visitMacro(JupitoreParser.MacroContext ctx) {
        // Reset positions for independent macro execution
        currentX = 0;
        currentY = 0;
        currentZ = 0;
        relativeMode = false; // start in absolute by default
        localVariables.clear(); // local variables don't carry over between macros

        StringBuilder gcode = new StringBuilder();

        if (ctx.TITLE() != null && ctx.STRING() != null) {
            String macroName = ctx.STRING().getText().replace("\"", "");
            gcode.append(emitMacroHeader(macroName));
        }

        if (ctx.statement() != null && !ctx.statement().isEmpty()) {
            for (JupitoreParser.StatementContext stmt : ctx.statement()) {
                String stmtCode = visit(stmt);
                if (stmtCode != null && !stmtCode.isEmpty()) {
                    for (String line : stmtCode.split("\n")) {
                        if (!line.isBlank()) {
                            gcode.append("  ").append(line).append("\n");
                        }
                    }
                }
            }
        }
        limiter.resetToGlobal(); // Reset limits after each macro to ensure safety for the next macro
        return gcode.toString();
    }

    @Override
    public String visitStatement(JupitoreParser.StatementContext ctx) {
        // ---- 6/17/2026: handle assignments ----
        if (ctx.assignment() != null) {
            System.out.println("DEBUG: Found assignment, calling visitAssignment");
            return visit(ctx.assignment());
        }

        if (ctx.global_assignment() != null) {
            System.out.println("DEBUG: Found global assignment, calling visitGlobal_assignment");
            return visit(ctx.global_assignment());
        }

        if (ctx.HOME() != null) {
            if (ctx.coordList() != null) {
                return emitHome(visit(ctx.coordList()));
            }
            return emitHome(null);
        }

        if (ctx.repeat_statement() != null) {
            return visit(ctx.repeat_statement());
        }

        if (ctx.brepeat_statement() != null) {
            return visit(ctx.brepeat_statement());
        }

        if (ctx.if_statement() != null) {
            return visit(ctx.if_statement());
        }

        if (ctx.MOVE() != null) {
            if (ctx.DIRECTION() != null) {
                return emitMove(ctx.DIRECTION().getText());
            }
        }

        if (ctx.SET_HEATER() != null) {
            if (ctx.TARGET() != null && ctx.expr() != null) {
                String target = ctx.TARGET().getText().toLowerCase();
                Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
                double value = compute.visit(ctx.expr());
                return emitSetHeater(target, value);
            }
        }

        if (ctx.HEAT() != null) {
            if (ctx.TARGET() != null && ctx.expr() != null) {
                String target = ctx.TARGET().getText().toLowerCase();
                Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
                double value = compute.visit(ctx.expr());
                return emitHeat(target, value, true);
            }
        }

        if (ctx.MOVEEX() != null) {
            if (ctx.coordList() != null) {
                return emitMoveTo(visit(ctx.coordList()));
            }
        }

        if (ctx.PAUSE() != null) {
            return emitPause();
        }

        if (ctx.RESUME() != null) {
            return emitResume();
        }

        if (ctx.RESPOND() != null) {
            if (ctx.STRING() != null) {
                String message = ctx.STRING().getText().replace("\"", "");
                return emitRespond(message);
            }
        }

        if (ctx.ABSOLUTE() != null) {
            relativeMode = false;
            return emitAbsolute();
        }

        if (ctx.RELATIVE() != null) {
            relativeMode = true;
            return emitRelative();
        }

        if (ctx.CALL() != null && ctx.STRING() != null) {
            String macroName = ctx.STRING().getText().replace("\"", "");
            return emitMacroCall(macroName);
        }

        if (ctx.WAITFORTEMP() != null && ctx.TARGET() != null) {
            String target = ctx.TARGET().getText().toLowerCase();
            return emitWaitForTemp(target);
        }

        if (ctx.LEVEL() != null || ctx.BED_MESH_CALIBRATE() != null) {
            return emitBedMeshCalibrate();
        }

        if (ctx.TIMEOUT_SET() != null && ctx.expr() != null) {
            Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
            double value = compute.visit(ctx.expr());
            return emitTimeoutSet(value);
        }

        if (ctx.RELATIVEEXTRUSION() != null) {
            return emitRelativeExtrusion();
        }

        if (ctx.LOAD_BED_MESH() != null && ctx.STRING() != null) {
            String pr = ctx.STRING().getText().replace("\"", "");
            return emitLoadBedMesh(pr);
        }

        if (ctx.RESET_EXTRUDER() != null) {
            return emitResetExtruder();
        }

        if (ctx.PROBE_CALIBRATE() != null) {
            return emitProbeCalibrate();
        }

        if (ctx.COOLDOWN() != null) {
            if (ctx.TARGET() == null) {
                return emitCooldown(null);
            }
            String t = ctx.TARGET().getText().toLowerCase();
            return emitCooldown(t);
        }

        if (ctx.DWELL() != null && ctx.expr() != null) {
            Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
            double value = compute.visit(ctx.expr());

            String unit = "ms";
            for (int i = 0; i < ctx.getChildCount(); i++) {
                ParseTree child = ctx.getChild(i);
                if (child instanceof TerminalNode) {
                    String text = child.getText();
                    if (text.equalsIgnoreCase("S") || text.equalsIgnoreCase("ms")) {
                        unit = text.toLowerCase();
                        break;
                    }
                }
            }

            if (unit.equals("s")) {
                value *= 1000;
            }

            return emitDwell(value);
        }

        if (ctx.SET_SPEED() != null && ctx.expr() != null) {
            Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
            double speed = compute.visit(ctx.expr());
            return emitSetSpeed(speed);
        }

        if (ctx.PRINTFILE() != null && ctx.STRING() != null) {
            String file = ctx.STRING().getText().replace("\"", "");
            return emitPrintFile(file);
        }

        if (ctx.SET_PRESSURE_ADVANCE() != null && ctx.expr() != null) {
            Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
            double value = compute.visit(ctx.expr());
            return emitSetPressureAdvance(value);
        }

        if (ctx.SET_FAN() != null && ctx.expr() != null) {
            Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
            double value = compute.visit(ctx.expr());
            return emitSetFan(value);
        }

        if (ctx.SET_NOZZLE() != null) {
            Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
            double val = compute.visit(ctx.expr());
            settings.setNozzleDiameter(val);
            return emitSetNozzle(val);
        }

        if (ctx.SET_FILAMENT() != null) {
            Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
            double val = compute.visit(ctx.expr());
            settings.setFilamentDiameter(val);
            System.out.println("DEBUG: Filament diameter set to " + val);
            return emitSetFilament(val);
        }

        if (ctx.SET_LAYER_HEIGHT() != null) {
            Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
            double val = compute.visit(ctx.expr());
            settings.setLayerHeight(val);
            System.out.println("DEBUG: Layer height set to " + val);
            return emitSetLayerHeight(val);
        }

        if (ctx.SET_EXTRUSION_MULTIPLIER() != null) {
            Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
            double val = compute.visit(ctx.expr());
            settings.setExtrusionMultiplier(val);
            System.out.println("DEBUG: Extrusion multiplier set to " + val);
            return emitSetExtrusionMultiplier(val);
        }

        if (ctx.ENABLE_AUTO_EXTRUDE() != null) {
            Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
            double val = compute.visit(ctx.expr());
            autoExtrudeEnabled = (val != 0.0);
            System.out.println("DEBUG: Auto-extrude enabled: " + autoExtrudeEnabled);
            return emitEnableAutoExtrude(autoExtrudeEnabled);
        }

        if (ctx.layer_statement() != null) {
            return visit(ctx.layer_statement());
        }

        // ---- 6/28/2026: INSERT G-CODE HANDLING ----
        if (ctx.insert_gcode_statement() != null) {
            JupitoreParser.Insert_gcode_statementContext insertCtx = ctx.insert_gcode_statement();
            String filePath = insertCtx.STRING().getText();
            boolean asReference = insertCtx.AS_REF() != null;
            return visitInsertGCode(filePath, asReference);
        }
        // -------------------------------------------

        System.out.println("DEBUG: Unhandled statement: " + ctx.getText());
        return "";
    }

    @Override
    public String visitRepeat_statement(JupitoreParser.Repeat_statementContext ctx) {
          int times = parseIntSafe(ctx.NUMBER().getText(), "repeat count");
        StringBuilder sb = new StringBuilder();

        boolean oldInsideJrepeat = insideJrepeat;
        insideJrepeat = false;

        for (int iteration = 0; iteration < times; iteration++) {
            for (JupitoreParser.StatementContext stmt : ctx.statement_block().statement()) {
                sb.append(visit(stmt));
            }
        }

        insideJrepeat = oldInsideJrepeat;
        return sb.toString();
    }

    @Override
    public String visitBrepeat_statement(JupitoreParser.Brepeat_statementContext ctx) {
          int times = parseIntSafe(ctx.NUMBER().getText(), "brepeat count");
        StringBuilder sb = new StringBuilder();

        double oldCenterX = centerX;
        double oldCenterY = centerY;
        

        centerX = currentX;
        centerY = currentY;

        for (int i = 0; i < times; i++) {
            iterationStack.push(i);
            insideJrepeat = true;

            for (JupitoreParser.StatementContext stmt : ctx.statement_block().statement()) {
                String stmtCode = visit(stmt);
                if (stmtCode != null && !stmtCode.isBlank()) {
                    sb.append(stmtCode);
                }
            }
            iterationStack.pop();
        }

        insideJrepeat = !iterationStack.isEmpty();
        centerX = oldCenterX;
        centerY = oldCenterY;
        return sb.toString();
    }

    @Override
    public String visitIf_statement(JupitoreParser.If_statementContext ctx) {
        String condition = visit(ctx.condition());
        StringBuilder sb = new StringBuilder();
        sb.append(emitIfStart(condition));

        for (JupitoreParser.StatementContext stmt : ctx.statement_block().statement()) {
            String inner = visit(stmt);
            if (inner != null && !inner.isBlank()) {
                for (String line : inner.split("\n")) {
                    if (!line.isBlank()) {
                        sb.append("  ").append(line).append("\n");
                    }
                }
            }
        }

        sb.append(emitIfEnd());
        return sb.toString();
    }

    @Override
    public String visitLayer_statement(JupitoreParser.Layer_statementContext ctx) {
         int layers = parseIntSafe(ctx.NUMBER().getText(), "layer count");
        StringBuilder sb = new StringBuilder();
         
        for (int layer = 0; layer < layers; layer++) {
            sb.append(emitLayerStart(layer));
            for (JupitoreParser.StatementContext stmt : ctx.statement_block().statement()) {
                String stmtCode = visit(stmt);
                if (stmtCode != null && !stmtCode.isBlank()) {
                    sb.append(stmtCode);
                }
            }
            sb.append(emitLayerEnd());
        }
         
        return sb.toString();
    }

    @Override
    public String visitCondition(JupitoreParser.ConditionContext ctx) {
        String target = ctx.TARGET().getText();
        String op = ctx.COMPARE().getText();
        String value = ctx.NUMBER().getText();

        String sensor;

        if (target.equals("extruder")) {
            sensor = "printer.extruder.temperature";
        } else if (target.equals("chamber")) {
            sensor = "printer.heater_chamber.temperature";
        } else {
            sensor = "printer.heater_bed.temperature";
        }

        return sensor + " " + op + " " + value;
    }

    @Override
    public String visitCoordList(JupitoreParser.CoordListContext ctx) {
        targetX = Double.NaN;
        targetY = Double.NaN;
        targetZ = Double.NaN;
        hasManualE = false;
        manualEValue = 0.0;

        for (JupitoreParser.CoordContext coordCtx : ctx.coord()) {
            visit(coordCtx);
        }

        StringBuilder sb = new StringBuilder();
        boolean isMove = false;
         
        if (!Double.isNaN(targetX)) {
            sb.append(" X").append(String.format("%.3f", targetX));
            isMove = true;
        }
        if (!Double.isNaN(targetY)) {
            sb.append(" Y").append(String.format("%.3f", targetY));
            isMove = true;
        }
        if (!Double.isNaN(targetZ)) {
            sb.append(" Z").append(String.format("%.3f", targetZ));
            isMove = true;
        }

        if (hasManualE) {
            sb.append(" E").append(String.format("%.3f", manualEValue));
        } else if (autoExtrudeEnabled && isMove) {
            double dx = Double.isNaN(targetX) ? 0 : targetX - currentX;
            double dy = Double.isNaN(targetY) ? 0 : targetY - currentY;
            double dz = Double.isNaN(targetZ) ? 0 : targetZ - currentZ;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            double autoE = settings.calculateExtrusion(distance);
            sb.append(" E").append(String.format("%.3f", autoE));
        }

        if (!Double.isNaN(targetX))
            currentX = targetX;
        if (!Double.isNaN(targetY))
            currentY = targetY;
        if (!Double.isNaN(targetZ))
            currentZ = targetZ;

        return sb.toString().trim();
    }

    @Override
    public String visitCoord(JupitoreParser.CoordContext ctx) {
        String axis = ctx.X() != null ? "X"
                : ctx.Y() != null ? "Y" : ctx.Z() != null ? "Z" : ctx.E() != null ? "E" : "";

        if (insideLayer && axis.equals("Z")) {
            throw new RuntimeException(
                    "ERROR: Z-axis movement is not allowed inside Layer blocks. " +
                            "Layer automatically manages Z-height.");
        }

        if (axis.equals("E")) {
            if (ctx.expr() != null) {
                String exprText = ctx.expr().getText();
                boolean isInLoop = !iterationStack.isEmpty();
                if (!isInLoop && exprText.matches(".*\\bi\\b.*")) {
                    throw new RuntimeException("ERROR: 'i' iterator is only allowed inside Brepeat loops.");
                }
                int activeIteration = isInLoop ? iterationStack.peek() : 0;
                Compute compute = new Compute(this, activeIteration);
                double value = compute.visit(ctx.expr());
                double finalE = value * settings.getExtrusionMultiplier();
                hasManualE = true;
                manualEValue = finalE;
            }
            return "";
        }

        String op = ctx.getChild(1).getText();
        String exprText = ctx.expr().getText();
        boolean isInLoop = !iterationStack.isEmpty();
        if (!isInLoop && exprText.matches(".*\\bi\\b.*")) {
            throw new RuntimeException("ERROR: 'i' iterator is only allowed inside Brepeat loops.");
        }
        int activeIteration = isInLoop ? iterationStack.peek() : 0;
        Compute compute = new Compute(this, activeIteration);
        double value = compute.visit(ctx.expr());

        double currentPos = getCurrent(axis);
        double newPos;

        if (relativeMode) {
            double delta = value;
            switch (op) {
                case "=":
                case "+=":
                    delta = value;
                    break;
                case "-=":
                    delta = -value;
                    break;
                case "*=":
                case "/=":
                    delta = value;
                    break;
            }
            newPos = currentPos + delta;
        } else {
            newPos = applyOp(currentPos, op, value);
        }

        switch (axis) {
            case "X":
                targetX = newPos;
                break;
            case "Y":
                targetY = newPos;
                break;
            case "Z":
                targetZ = newPos;
                break;
        }

        if (!Double.isNaN(targetX))
            limiter.checkAndMove("X", targetX);
        if (!Double.isNaN(targetY))
            limiter.checkAndMove("Y", targetY);
        if (!Double.isNaN(targetZ))
            limiter.checkAndMove("Z", targetZ);

        return "";
    }

    private double getCurrent(String axis) {
        switch (axis) {
            case "X":
                return currentX;
            case "Y":
                return currentY;
            case "Z":
                return currentZ;
            case "E":
                return 0;
        }
        return 0;
    }

    private double applyOp(double current, String op, double value) {
        switch (op) {
            case "=":
                return value;
            case "+=":
                return current + value;
            case "-=":
                return current - value;
            case "*=":
                return current * value;
            case "/=":
                return current / value;
            default:
                throw new RuntimeException("Unsupported operation: " + op);
        }
    }

    @Override
    protected String defaultResult() {
        return "";
    }

    @Override
    protected String aggregateResult(String aggregate, String nextResult) {
        if (aggregate == null)
            return nextResult;
        if (nextResult == null)
            return aggregate;
        return aggregate + nextResult;
    }

    // added to deal with variable errors 7/2/26
   @Override
public String visitAssignment(JupitoreParser.AssignmentContext ctx) {
    String varName = ctx.ID().getText();
    if ("x y z e".contains(varName.toLowerCase())) {
        throw new RuntimeException(
            "ERROR: '" + varName + "' is a reserved axis name. " +
            "Use a different variable name (e.g. pos_x, my_x).");
    }
    Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
    double value = compute.visit(ctx.expr());
    localVariables.put(varName, value);
    System.out.println("ASSIGN: " + varName + " = " + value);
    return "";
}

    // var x = expr - same rules as a plain assignment, but stored in global
    // scope so it's visible from every macro in the file
   @Override
public String visitGlobal_assignment(JupitoreParser.Global_assignmentContext ctx) {
    if (ctx.ID() == null) {
        // x/y/z/e tokenize as axis letters rather than a generic ID, so they
        // can never satisfy this rule - the parser leaves ID() unset instead
        // of throwing, so surface it as the same reserved-name error rather
        // than letting callers hit a NullPointerException
        throw new RuntimeException(
            "ERROR: 'var' needs a variable name that isn't a reserved axis letter (x, y, z, e). " +
            "Use a different variable name (e.g. pos_x, my_x).");
    }
    String varName = ctx.ID().getText();
    if ("x y z e".contains(varName.toLowerCase())) {
        throw new RuntimeException(
            "ERROR: '" + varName + "' is a reserved axis name. " +
            "Use a different variable name (e.g. pos_x, my_x).");
    }
    Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
    double value = compute.visit(ctx.expr());
    globalVariables.put(varName, value);
    System.out.println("ASSIGN (global): " + varName + " = " + value);
    return "";
}
    // ---- 6/28/2026: INSERT G-CODE IMPLEMENTATION ----
    public void setSourceFilePath(String path) {
        this.sourceFilePath = path;
        System.out.println("VISITOR LOG: Source file path set to: " + path);
    }

    protected File resolveFilePath(String filePath) {
        File absoluteFile = new File(filePath);
        if (absoluteFile.isAbsolute() && absoluteFile.exists()) {
            return absoluteFile;
        }

        if (sourceFilePath != null) {
            File folderFile = new File(sourceFilePath, filePath);
            if (folderFile.exists()) {
                return folderFile;
            }
        }

        File cwdFile = new File(filePath);
        if (cwdFile.exists()) {
            return cwdFile;
        }

        return absoluteFile.exists() ? absoluteFile : new File(filePath);
    }

    protected String visitInsertGCode(String filePath, boolean asReference) {
        String cleanPath = filePath.replace("\"", "");

        if (asReference) {
            return emitPrintFile(cleanPath);
        }

        File gcodeFile = resolveFilePath(cleanPath);

        if (!gcodeFile.exists()) {
            throw new RuntimeException(
                    "InsertGCode ERROR: File not found: " + cleanPath + "\n" +
                            "  Tried: " + gcodeFile.getAbsolutePath() + "\n" +
                            "  Source file: " + (sourceFilePath != null ? sourceFilePath : "unknown"));
        }

        if (gcodeFile.isDirectory()) {
            throw new RuntimeException(
                    "InsertGCode ERROR: Path is a directory, not a file: " + cleanPath);
        }

        if (!cleanPath.toLowerCase().endsWith(".gcode") &&
                !cleanPath.toLowerCase().endsWith(".g") &&
                !cleanPath.toLowerCase().endsWith(".gc")) {
            System.out.println("WARNING: InsertGCode file doesn't have standard G-code extension: " + cleanPath);
        }

        if (gcodeFile.length() == 0) {
            System.out.println("WARNING: InsertGCode file is empty: " + cleanPath);
            return "";
        }

        try {
            String content = Files.readString(gcodeFile.toPath());
            if (!content.endsWith("\n")) {
                content += "\n";
            }
            return content;
        } catch (IOException e) {
            throw new RuntimeException(
                    "InsertGCode ERROR: Failed to read file: " + cleanPath + "\n" +
                            "  " + e.getMessage());
        }
    }

    // added helper method 

    private int parseIntSafe(String value, String context) {
    try {
        return Integer.parseInt(value);
    } catch (NumberFormatException e) {
        throw new RuntimeException("Invalid number at " + context + ": " + value);
    }
}
}