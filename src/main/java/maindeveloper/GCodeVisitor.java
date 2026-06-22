// this program is the heart of the compiler. it traverses the parse tree and generates G-code based on the rules defined in the visitor methods. 
// each method corresponds to a specific rule in the grammar and is responsible for translating that rule into G-code. 
// the visitor pattern allows us to separate the logic of code generation from the structure of the parse tree
//  making it easier to maintain and extend in the future.
package main.java.maindeveloper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;
import java.util.HashMap;
import java.util.Map;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;


import jupitore.gen.*;

public abstract class GCodeVisitor extends JupitoreBaseVisitor<String> {
// ADDED 4/10/2026
protected PrinterSettings settings = new PrinterSettings();
protected boolean enablePaging = false;
protected boolean autoExtrudeEnabled = false;
// 6/17/26
protected Map<String, Double> variables = new HashMap<>();
// Temporary storage for a single move (used in visitCoordList)
private double targetX = Double.NaN;
private double targetY = Double.NaN;
private double targetZ = Double.NaN;
private boolean hasManualE = false;
private double manualEValue = 0.0;
private boolean hasAutoE = false;   // user wrote "E" without value
protected double centerX = 0; // <-- Jrepeat center for X
protected double centerY = 0; // <-- Jrepeat center for Y
//NEW LIMITER TO ENSURE SAFETY. THIS IS A HARD LIMITER THAT THROWS AN ERROR IF EXCEEDED. THE FRONTEND SHOULD CATCH THIS AND ALERT THE USER.
protected HardwareLimiter limiter;
// new function
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

    // Abstract firmware-specific methods ----
    protected abstract String emitMacroHeader(String macroName);
    protected abstract String emitHeat(String target, double value, boolean wait);
    protected abstract String emitSetHeater(String target, double value);
    protected abstract String emitPause();
    protected abstract String emitResume();
    protected abstract String emitRespond(String message);
    protected abstract String emitCooldown(String target); // null = all off
    protected abstract String emitTimeoutSet(double seconds);
    protected abstract String emitLoadBedMesh(String profile);
    protected abstract String emitProbeCalibrate();
    protected abstract String emitSetPressureAdvance(double value);
    protected abstract String emitSetFan(double value);
    protected abstract String emitMacroCall(String macroName);
    protected abstract String emitBedMeshCalibrate();  // LEVEL or BED_MESH_CALIBRATE
    protected abstract String emitPrintFile(String filename);
    protected abstract String emitIfStart(String condition);
    protected abstract String emitIfEnd();

    /** 
     * @param ctx
     * @return String
     */
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
            tempFile.deleteOnExit(); // OS cleans this up when the IDE closes... right? 

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                for (JupitoreParser.MacroContext macro : ctx.macro()) {
                    String macroCode = visit(macro);
                    if (macroCode != null && !macroCode.isEmpty()) {
                        writer.write(macroCode);
                        writer.write("\n");
                        // This flushes the RAM so the garbage collector 
                        //can take the space used by the 'macroCode' string.
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

 /** 
  * @param ctx
  * @return String
  */
 @Override
public String visitMacro(JupitoreParser.MacroContext ctx) {
    // Reset positions for independent macro execution
    currentX = 0;
    currentY = 0;
    currentZ = 0;
    relativeMode = false;  // start in absolute by default

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

    /** 
     * @param ctx
     * @return String
     */
    @Override
    public String visitStatement(JupitoreParser.StatementContext ctx) {
// ---- 6/17/2026: handle assignments ----
if (ctx.assignment() != null) {
    System.out.println("DEBUG: Found assignment, calling visitAssignment");
    return visit(ctx.assignment());
}
        //System.out.println("DEBUG: Visiting statement: " + ctx.getText());

        if (ctx.HOME() != null) {
            if (ctx.coordList() != null) {
                return "G28 " + visit(ctx.coordList()) + "\n";
            }
            return "G28\n";
        }
        if (ctx.repeat_statement() != null) {
            return visit(ctx.repeat_statement());
        }

        if (ctx.brepeat_statement() != null) {
            return visit(ctx.brepeat_statement());
        } 

        // added, i forgot this
          if (ctx.if_statement() != null) {
        return visit(ctx.if_statement());
    }
        if (ctx.MOVE() != null) {
            StringBuilder gcode = new StringBuilder("G1 ");
            if (ctx.DIRECTION() != null) {
                switch (ctx.DIRECTION().getText()) {
                    case "left":
                        gcode.append("X-1");
                        break;
                    case "right":
                        gcode.append("X1");
                        break;
                    case "center":
                        gcode.append("X0 Y0");
                        break;
                    case "up":
                        gcode.append("Z1");
                        break;
                    case "down":
                        gcode.append("Z-1");
                        break;
                }
            }

            return gcode.toString() + "\n";
        }

        // here is where i added REPEAT statement implementation

        // SET_HEATER: set temperature and does not wait. REMEMEBR
        // added chamber
      if (ctx.SET_HEATER() != null) {
    if (ctx.TARGET() != null && ctx.expr() != null) {
        String target = ctx.TARGET().getText().toLowerCase();
        Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
        double value = compute.visit(ctx.expr());
        return emitSetHeater(target, value);
    }
}

        // HEAT: set temperature AND wait. ADDED CHAMBER
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
                return "G1 " + visit(ctx.coordList()) + "\n";
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
        // added relativemode to fix the coords issue
        if (ctx.ABSOLUTE() != null) {
            relativeMode = false;
            return "G90\n";
        }

        if (ctx.RELATIVE() != null) {
            relativeMode = true;
            return "G91\n";
        }

        // add the rest here

        // klipper macro caller
        if (ctx.CALL() != null && ctx.STRING() != null) {
            String macroName = ctx.STRING().getText().replace("\"", "");
            return emitMacroCall(macroName);
        }
        // this is just testing

        // not to be confused with heat. this one waits for an existing temperature
        // have to remind myself. so it does not change STATE. ADDED CHAMBER

        if (ctx.WAITFORTEMP() != null && ctx.TARGET() != null) {
            String target = ctx.TARGET().getText().toLowerCase();
            switch (target) {
                case "extruder":
                    return "M109\n";
                case "bed":
                    return "M190\n";
                case "chamber":
                    return "M141\n"; // Wait for chamber

            }
        }

        // semantic different but the same. for convenience. might change later.
        if (ctx.LEVEL() != null || ctx.BED_MESH_CALIBRATE() != null) {
            return emitBedMeshCalibrate();
        }

        // timeout
       if (ctx.TIMEOUT_SET() != null && ctx.expr() != null) {
    Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
    double value = compute.visit(ctx.expr());
    return emitTimeoutSet(value);
}

        // RELATIVE EXTRUSION check documentation
        if (ctx.RELATIVEEXTRUSION() != null) {

            return "M83\n";
        }

        // LOADING MESH
        if (ctx.LOAD_BED_MESH() != null && ctx.STRING() != null) {
            String pr = ctx.STRING().getText().replace("\"", "");
            return emitLoadBedMesh(pr);

        }
        // reeset extruder
        if (ctx.RESET_EXTRUDER() != null) {
            return "G92 E0\n";

        }

        // PROBE CALIBRATE
        if (ctx.PROBE_CALIBRATE() != null) {
            return emitProbeCalibrate();

        }

        // cooldown

        if (ctx.COOLDOWN() != null) {
            // no target? turn off all heaters plz
            if (ctx.TARGET() == null) {
                return emitCooldown(null);
            }

            // target exists? handle specific heater
            String t = ctx.TARGET().getText().toLowerCase();
            return emitCooldown(t);
        }
        // dwell
       if (ctx.DWELL() != null && ctx.expr() != null) {
    Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
    double value = compute.visit(ctx.expr());

    // unit
    String unit = "ms"; // default
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
        value *= 1000; // convert seconds to milliseconds
    }

    return "G4 P" + ((int) value) + "\n";
}

        // set speed. tested 6/17/26 with user defined variables
       if (ctx.SET_SPEED() != null && ctx.expr() != null) {
    Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
    double speed = compute.visit(ctx.expr());
    return "G1 F" + (int) speed + "\n";
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
    return "";
}
if (ctx.SET_FILAMENT() != null) {
    Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
    double val = compute.visit(ctx.expr());
    settings.setFilamentDiameter(val);
    System.out.println("DEBUG: Filament diameter set to " + val);
    return "";
}
if (ctx.SET_LAYER_HEIGHT() != null) {
    Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
    double val = compute.visit(ctx.expr());
    settings.setLayerHeight(val);
     System.out.println("DEBUG: Layer height set to " + val);  
    return "";
}
if (ctx.SET_EXTRUSION_MULTIPLIER() != null) {
    Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
    double val = compute.visit(ctx.expr());
    settings.setExtrusionMultiplier(val);
    System.out.println("DEBUG: Extrusion multiplier set to " + val);
    return "";
}

if (ctx.ENABLE_AUTO_EXTRUDE() != null) {
    Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
    double val = compute.visit(ctx.expr());
    autoExtrudeEnabled = (val != 0.0);
     System.out.println("DEBUG: Auto-extrude enabled: " + autoExtrudeEnabled);
    return "";
}



//-------------------------------------------


        // end
        System.out.println("DEBUG: Unhandled statement: " + ctx.getText());
        return "";



    }

/** 
 * @param ctx
 * @return String
 */
@Override
public String visitRepeat_statement(JupitoreParser.Repeat_statementContext ctx) {
    int times = Integer.parseInt(ctx.NUMBER().getText());
    StringBuilder sb = new StringBuilder();

    // repeat does not allow 'i' or functions
    boolean oldInsideJrepeat = insideJrepeat;
    insideJrepeat = false;

    for (int iteration = 0; iteration < times; iteration++) {
        for (JupitoreParser.StatementContext stmt : ctx.statement_block().statement()) {
            // temporarily disable 'i' and function usage checks
            sb.append(visit(stmt));
        }
    }

    // restore previous state just in case
    insideJrepeat = oldInsideJrepeat;

    return sb.toString();
}


  // we want to nest iterators so im going to test a stack set up
  protected Stack<Integer> iterationStack = new Stack<>();
    //protected int currentIteration = 0; // current "i" for Jrepeat

/** 
 * @param ctx
 * @return String
 */
@Override
public String visitBrepeat_statement(JupitoreParser.Brepeat_statementContext ctx) {

    int times = Integer.parseInt(ctx.NUMBER().getText());
    StringBuilder sb = new StringBuilder();

    // Save state
    double oldCenterX = centerX;
    double oldCenterY = centerY;
    double oldX = currentX;
    double oldY = currentY;

    // Set Jrepeat center
    centerX = currentX;
    centerY = currentY;
       // stack added 4/7/2026 to support nested Brepeats. 
    for (int i = 0; i < times; i++) {

       // currentIteration = i;
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
 // fixed 4/6/2026
    // Restore state
    centerX = oldCenterX;
    centerY = oldCenterY;
    //currentX = oldX;
   // currentY = oldY;
    // do not restore current x and y in this case. so the tracker stays at the final position
    return sb.toString();
}
    /** 
     * @param ctx
     * @return String
     */
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



// Handle LAYER block
public String visitLayer_statement(JupitoreParser.Layer_statementContext ctx) {
    int layers = Integer.parseInt(ctx.NUMBER().getText());
    LayerHandler layerHandler = new LayerHandler(this, settings);
    return layerHandler.handleLayer(layers, ctx.statement_block());
}

    /** 
     * @param ctx
     * @return String
     */
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

    /** 
     * @param ctx
     * @return String
     */
@Override
public String visitCoordList(JupitoreParser.CoordListContext ctx) {
    // Reset temporary storage
    targetX = Double.NaN;
    targetY = Double.NaN;
    targetZ = Double.NaN;
    hasManualE = false;
    manualEValue = 0.0;

    // First pass: collect all coordinates
    for (JupitoreParser.CoordContext coordCtx : ctx.coord()) {
        visit(coordCtx);
    }

    // Build the output string
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

    // Decide about extrusion
    if (hasManualE) {
        sb.append(" E").append(String.format("%.3f", manualEValue));
    } else if (autoExtrudeEnabled && isMove) {
        // Auto‑extrude: compute distance from current position to target
        double dx = Double.isNaN(targetX) ? 0 : targetX - currentX;
        double dy = Double.isNaN(targetY) ? 0 : targetY - currentY;
        double dz = Double.isNaN(targetZ) ? 0 : targetZ - currentZ;
        double distance = Math.sqrt(dx*dx + dy*dy + dz*dz);
        double autoE = settings.calculateExtrusion(distance);
        sb.append(" E").append(String.format("%.3f", autoE));
    }
    // else: no extrusion (travel move)

    // Update current position
    if (!Double.isNaN(targetX)) currentX = targetX;
    if (!Double.isNaN(targetY)) currentY = targetY;
    if (!Double.isNaN(targetZ)) currentZ = targetZ;

    return sb.toString().trim();
}
    protected boolean relativeMode = false;

    protected double currentX = 0;
    protected double currentY = 0;
    protected double currentZ = 0;
    protected boolean insideJrepeat = false;

/** 
 * @param ctx
 * @return String
 */
@Override
public String visitCoord(JupitoreParser.CoordContext ctx) {
    String axis = ctx.X() != null ? "X" :
                  ctx.Y() != null ? "Y" :
                  ctx.Z() != null ? "Z" :
                  ctx.E() != null ? "E" : "";

    // Handle E axis separately
    if (axis.equals("E")) {
        if (ctx.expr() != null) {
            // Manual extrusion value
            String op = ctx.getChild(1).getText();
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
        // If there is no expression, we ignore (auto‑extrude will be handled later if enabled)
        return "";
    }

    // For X, Y, Z axes
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
        // In relative mode, the expression gives the delta to add
        double delta = value;
        // Apply operation (though for relative, =, +=, -= are usually the same as delta)
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
        // Absolute mode: apply operation (e.g., +=, =, etc.) to current position
        newPos = applyOp(currentPos, op, value);
    }

    // Store the target position (do NOT update currentX/Y/Z yet)
    switch (axis) {
        case "X": targetX = newPos; break;
        case "Y": targetY = newPos; break;
        case "Z": targetZ = newPos; break;
    }
    // After updating current position, check limits
if (!Double.isNaN(targetX)) limiter.checkAndMove("X", targetX);
if (!Double.isNaN(targetY)) limiter.checkAndMove("Y", targetY);
if (!Double.isNaN(targetZ)) limiter.checkAndMove("Z", targetZ);
    return "";
}

/** 
 * @param axis
 * @return double
 */
private double getCurrent(String axis) {
    switch (axis) {
        case "X": return currentX;
        case "Y": return currentY;
        case "Z": return currentZ;
        case "E": return 0;
    }
    return 0;
}


  /** 
   * @param current
   * @param op
   * @param value
   * @return double
   */
  // Helper to apply the operation and update current position
private double applyOp(double current, String op, double value) {
    switch (op) {
        case "=": return value;        // Set the value
        case "+=": return current + value;
        case "-=": return current - value;
        case "*=": return current * value;
        case "/=": return current / value;
        default: throw new RuntimeException("Unsupported operation: " + op);
    }
}

    /** 
     * @return String
     */
    @Override
    protected String defaultResult() {
        return "";
    }

    /** 
     * @param aggregate
     * @param nextResult
     * @return String
     */
    @Override
    protected String aggregateResult(String aggregate, String nextResult) {
        if (aggregate == null)
            return nextResult;
        if (nextResult == null)
            return aggregate;
        return aggregate + nextResult;
    }

@Override
public String visitAssignment(JupitoreParser.AssignmentContext ctx) {
    String varName = ctx.ID().getText();
    Compute compute = new Compute(this, iterationStack.isEmpty() ? 0 : iterationStack.peek());
    double value = compute.visit(ctx.expr());
    variables.put(varName, value);
    System.out.println("ASSIGN: " + varName + " = " + value);
    return "";
}
    
}