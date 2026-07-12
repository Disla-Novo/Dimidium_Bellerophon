package maindeveloper.dialects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jupitore.gen.JupitoreParser;
import maindeveloper.core.GCodeVisitor;
import maindeveloper.core.PrinterProfile;

// here we just add overrides that arent supported.
// these typically are just word commands that marlin doesn't support.
// awaiting a few more, dont forget to add.

public class MarlinVisitor extends GCodeVisitor {
    public MarlinVisitor(PrinterProfile profile) {
        super(profile); // calls the GCodeVisitor constructor above
    }

    // ---- Arc interpolation (G2/G3) for Brepeat loops that trace a circle ----
    //
    // A Brepeat body that moves along a circle (whether written as one
    // MoveTo with inline cos/sin math, or a few statements that compute
    // pos_x/pos_y and then MoveTo) produces one G1 line per iteration on
    // dense curves - hundreds of micro-segments Marlin boards can stutter
    // on. If every iteration's output is exactly one non-extruding G1 move
    // at a constant Z, and the resulting points fit a circle tightly, this
    // replaces the whole loop with one (or two, for a closed loop) G2/G3
    // arc commands instead. Anything less than fully confident about the
    // fit falls back to the original per-point output - never guesses.

    private static final Pattern ARC_CANDIDATE_LINE = Pattern.compile(
            "G1 X(?<x>-?\\d+\\.\\d+) Y(?<y>-?\\d+\\.\\d+)(?: Z(?<z>-?\\d+\\.\\d+))?");

    // tolerances are intentionally tight - this only kicks in for genuine
    // circular motion, never for "close enough" approximations
    private static final double RADIUS_TOLERANCE_MM = 0.02;
    private static final double ANGLE_STEP_TOLERANCE_DEG = 1.0;
    private static final double CLOSED_LOOP_TOLERANCE_MM = 0.05;
    // a handful of sparse/short-sweep points can be least-squares fit to
    // *some* circle even when the actual path isn't one (a near-straight
    // line of points fits an arbitrarily large-radius circle almost
    // exactly) - these two gates were added after a real false positive
    // (a 5-point parabola-like curve got collapsed into a bogus G2) found
    // via hardware testing on PR #47
    private static final int MIN_ARC_POINTS = 8;
    private static final double MIN_SWEEP_DEG = 15.0;

    @Override
    public String visitBrepeat_statement(JupitoreParser.Brepeat_statementContext ctx) {
        String arcOutput = tryEmitArc(ctx);
        return arcOutput != null ? arcOutput : super.visitBrepeat_statement(ctx);
    }

    private String tryEmitArc(JupitoreParser.Brepeat_statementContext ctx) {
        int times;
        try {
            times = Integer.parseInt(ctx.NUMBER().getText());
        } catch (NumberFormatException e) {
            return null; // let the base class produce the real parse error
        }

        double oldCenterX = centerX;
        double oldCenterY = centerY;
        centerX = currentX;
        centerY = currentY;

        double[] xs = new double[times];
        double[] ys = new double[times];
        String[] iterationLines = new String[times];
        Double fixedZ = null;
        boolean eligible = times >= MIN_ARC_POINTS;
        StringBuilder fallback = new StringBuilder();

        // the loop always runs to completion, exactly like the base
        // implementation - bailing out early on ineligibility would leave
        // currentX/currentY (and any variables the body sets) wrong for
        // whatever comes after the loop
        for (int i = 0; i < times; i++) {
            iterationStack.push(i);
            insideJrepeat = true;

            StringBuilder iterationOutput = new StringBuilder();
            for (JupitoreParser.StatementContext stmt : ctx.statement_block().statement()) {
                String stmtCode = visit(stmt);
                if (stmtCode != null && !stmtCode.isBlank()) {
                    iterationOutput.append(stmtCode);
                }
            }
            iterationStack.pop();
            fallback.append(iterationOutput);
            iterationLines[i] = iterationOutput.toString();

            if (!eligible) {
                continue;
            }

            Matcher m = ARC_CANDIDATE_LINE.matcher(iterationOutput.toString().trim());
            if (!m.matches()) {
                eligible = false; // not exactly one plain, non-extruding G1 XY(Z) move
                continue;
            }

            String zGroup = m.group("z");
            if (zGroup != null) {
                double z = Double.parseDouble(zGroup);
                if (fixedZ == null) {
                    fixedZ = z;
                } else if (Math.abs(fixedZ - z) > 1e-6) {
                    eligible = false; // Z changes mid-loop - not a flat arc
                    continue;
                }
            }

            xs[i] = currentX;
            ys[i] = currentY;
        }

        insideJrepeat = !iterationStack.isEmpty();
        centerX = oldCenterX;
        centerY = oldCenterY;

        if (!eligible) {
            return fallback.toString();
        }

        // fit the circle through the points the loop itself traced, not
        // wherever the tool happened to be before the loop started - there's
        // no guarantee (and often no reason to expect) that a prior move
        // already parked the tool on the circle
        CircleFit fit = fitCircle(xs, ys);
        if (fit == null) {
            return fallback.toString();
        }

        // iteration 0's own move (from wherever the tool was, onto the
        // circle) is kept as-is; only the remaining iterations - the actual
        // circular travel - become the arc
        StringBuilder result = new StringBuilder();
        result.append(iterationLines[0]);
        result.append(emitArc(xs[0], ys[0], xs[times - 1], ys[times - 1], fit, fixedZ));
        return result.toString();
    }

    private static final class CircleFit {
        final double centerX;
        final double centerY;
        final double radius;
        final boolean clockwise;

        CircleFit(double centerX, double centerY, double radius, boolean clockwise) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.radius = radius;
            this.clockwise = clockwise;
        }
    }

    // fits a circle through every point the loop itself computed, then
    // verifies every point actually lies on that circle within tolerance
    // and that the angular step between points is consistent - both are
    // required before this is trusted as a real arc
    private CircleFit fitCircle(double[] xs, double[] ys) {
        int n = xs.length;
        if (n < MIN_ARC_POINTS) {
            return null; // not enough points for reliable circle detection
        }

        // Kasa algebraic circle fit: solve the normal equations for
        // x^2+y^2 = D*x + E*y + F
        double sx = 0, sy = 0, sxx = 0, syy = 0, sxy = 0, sxz = 0, syz = 0, sz = 0;
        for (int i = 0; i < n; i++) {
            double x = xs[i];
            double y = ys[i];
            double z = x * x + y * y;
            sx += x;
            sy += y;
            sxx += x * x;
            syy += y * y;
            sxy += x * y;
            sxz += x * z;
            syz += y * z;
            sz += z;
        }

        double[][] a = {
            { sxx, sxy, sx },
            { sxy, syy, sy },
            { sx, sy, n }
        };
        double[] b = { sxz, syz, sz };
        double[] solved = solve3x3(a, b);
        if (solved == null) {
            return null; // points are collinear/degenerate, not a circle
        }

        double centerX = solved[0] / 2;
        double centerY = solved[1] / 2;
        double radiusSq = solved[2] + centerX * centerX + centerY * centerY;
        if (radiusSq <= 0) {
            return null;
        }
        double radius = Math.sqrt(radiusSq);
        double tolerance = Math.max(RADIUS_TOLERANCE_MM, radius * 0.001);

        double[] angles = new double[n];
        for (int i = 0; i < n; i++) {
            double dx = xs[i] - centerX;
            double dy = ys[i] - centerY;
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (Math.abs(dist - radius) > tolerance) {
                return null; // this point doesn't actually sit on the circle
            }
            angles[i] = Math.toDegrees(Math.atan2(dy, dx));
        }

        double totalSweep = 0;
        Boolean clockwise = null;
        for (int i = 1; i < n; i++) {
            double delta = normalizeAngleDelta(angles[i] - angles[i - 1]);
            if (Math.abs(delta) < 1e-6) {
                return null; // two consecutive points didn't move angularly
            }
            boolean stepClockwise = delta < 0;
            if (clockwise == null) {
                clockwise = stepClockwise;
            } else if (clockwise != stepClockwise) {
                return null; // direction reversed - not a steady arc
            }
            totalSweep += delta;
        }

        double avgStep = totalSweep / (n - 1);
        for (int i = 1; i < n; i++) {
            double delta = normalizeAngleDelta(angles[i] - angles[i - 1]);
            if (Math.abs(delta - avgStep) > ANGLE_STEP_TOLERANCE_DEG) {
                return null; // angular spacing isn't consistent
            }
        }

        if (Math.abs(totalSweep) > 360.0 + ANGLE_STEP_TOLERANCE_DEG) {
            return null; // more than one full lap in a single Brepeat - out of scope for one arc command
        }

        if (Math.abs(totalSweep) < MIN_SWEEP_DEG) {
            return null; // arc is too shallow to justify optimization
        }

        return new CircleFit(centerX, centerY, radius, clockwise);
    }

    private static double normalizeAngleDelta(double delta) {
        while (delta > 180) delta -= 360;
        while (delta < -180) delta += 360;
        return delta;
    }

    // solves a 3x3 linear system via Cramer's rule; returns null if the
    // system is singular (degenerate/collinear points)
    private static double[] solve3x3(double[][] a, double[] b) {
        double det = determinant3x3(a);
        if (Math.abs(det) < 1e-9) {
            return null;
        }
        double[] result = new double[3];
        for (int col = 0; col < 3; col++) {
            double[][] replaced = {
                { a[0][0], a[0][1], a[0][2] },
                { a[1][0], a[1][1], a[1][2] },
                { a[2][0], a[2][1], a[2][2] }
            };
            for (int row = 0; row < 3; row++) {
                replaced[row][col] = b[row];
            }
            result[col] = determinant3x3(replaced) / det;
        }
        return result;
    }

    private static double determinant3x3(double[][] m) {
        return m[0][0] * (m[1][1] * m[2][2] - m[1][2] * m[2][1])
                - m[0][1] * (m[1][0] * m[2][2] - m[1][2] * m[2][0])
                + m[0][2] * (m[1][0] * m[2][1] - m[1][1] * m[2][0]);
    }

    private String emitArc(double startX, double startY, double endX, double endY, CircleFit fit, Double z) {
        String zSuffix = z != null ? " Z" + String.format("%.3f", z) : "";
        boolean closedLoop = Math.hypot(endX - startX, endY - startY) < CLOSED_LOOP_TOLERANCE_MM;

        if (!closedLoop) {
            return arcCommand(fit, startX, startY, endX, endY) + zSuffix + "\n";
        }

        // a full circle back to the start is split into two half-circles:
        // most Marlin builds treat a G2/G3 whose endpoint equals its start
        // as a zero-length move rather than a full revolution
        double midX = 2 * fit.centerX - startX;
        double midY = 2 * fit.centerY - startY;

        StringBuilder sb = new StringBuilder();
        sb.append(arcCommand(fit, startX, startY, midX, midY)).append(zSuffix).append("\n");
        sb.append(arcCommand(fit, midX, midY, endX, endY)).append(zSuffix).append("\n");
        return sb.toString();
    }

    private String arcCommand(CircleFit fit, double fromX, double fromY, double toX, double toY) {
        String gcode = fit.clockwise ? "G2" : "G3";
        double i = fit.centerX - fromX;
        double j = fit.centerY - fromY;
        return gcode
                + " X" + String.format("%.3f", toX)
                + " Y" + String.format("%.3f", toY)
                + " I" + String.format("%.3f", i)
                + " J" + String.format("%.3f", j);
    }

    @Override
    protected String emitMacroHeader(String macroName) {
        return "; " + macroName + "\n";
    }

    @Override
    protected String emitHeat(String target, double value, boolean wait) {
        // NEW marlin specific. apparently it listens to 'R' and not 'S'.
        // 4/4/2026
        if (wait) {
            switch (target) {
                case "extruder":
                    return "M109 R" + (int) value + "\n";
                case "bed":
                    return "M190 R" + (int) value + "\n";
                case "chamber":
                    return "M141 S" + (int) value + "\n";
                default:
                    return "";
            }
        } else {
            switch (target) {
                case "extruder":
                    return "M104 S" + (int) value + "\n";
                case "bed":
                    return "M140 S" + (int) value + "\n";
                case "chamber":
                    return "M141 S" + (int) value + "\n";
                default:
                    return "";
            }
        }
    }

    @Override
    protected String emitSetHeater(String target, double value) {
        switch (target) {
            case "extruder":
                return "M104 S" + (int) value + "\n";
            case "bed":
                return "M140 S" + (int) value + "\n";
            case "chamber":
                return "M141 S" + (int) value + "\n";
            default:
                return "";
        }
    }

    @Override
    protected String emitWaitForTemp(String target) {
        switch (target) {
            case "extruder":
                return "M109\n";
            case "bed":
                return "M190\n";
            case "chamber":
                return "M141\n";
            default:
                return "";
        }
    }

    @Override
    protected String emitHome(String coordList) {
        if (coordList != null && !coordList.isEmpty()) {
            return "G28 " + coordList + "\n";
        }
        return "G28\n";
    }

    @Override
    protected String emitMove(String direction) {
        switch (direction) {
            case "left":
                return "G1 X-1\n";
            case "right":
                return "G1 X1\n";
            case "center":
                return "G1 X0 Y0\n";
            case "up":
                return "G1 Z1\n";
            case "down":
                return "G1 Z-1\n";
            default:
                return "";
        }
    }

    @Override
    protected String emitMoveTo(String coordList) {
        if (coordList != null && !coordList.isEmpty()) {
            return "G1 " + coordList + "\n";
        }
        return "";
    }

    @Override
    protected String emitSetSpeed(double value) {
        return "G1 F" + (int) value + "\n";
    }

    @Override
    protected String emitSetFan(double value) {
        int marlinSpeed = (int) (value * 255);
        return "M106 S" + marlinSpeed + "\n";
    }

    @Override
    protected String emitAbsolute() {
        return "G90\n";
    }

    @Override
    protected String emitRelative() {
        return "G91\n";
    }

    @Override
    protected String emitRelativeExtrusion() {
        return "M83\n";
    }

    @Override
    protected String emitResetExtruder() {
        return "G92 E0\n";
    }

    @Override
    protected String emitPause() {
        return "M25\n"; // Marlin pause
    }

    @Override
    protected String emitResume() {
        return "M24\n"; // Marlin resume
    }

    @Override
    protected String emitDwell(double milliseconds) {
        return "G4 P" + (int) milliseconds + "\n";
    }

    @Override
    protected String emitTimeoutSet(double seconds) {
        return "M84 S" + (int) seconds + "\n"; // Marlin idle timeout
    }

    @Override
    protected String emitRespond(String message) {
        return "M117 " + message + "\n"; // Marlin LCD message
    }

    @Override
    protected String emitPrintFile(String filename) {
        return "M23 " + filename + "\nM24\n"; // Marlin select + start print
    }

    @Override
    protected String emitMacroCall(String macroName) {
        System.err.println("Warning: Macro call '" + macroName + "' not supported in Marlin mode");
        return "; CALL " + macroName + " (ignored in Marlin)\n";
    }

    @Override
    protected String emitBedMeshCalibrate() {
        return "G29\n"; // Marlin bed leveling
    }

    @Override
    protected String emitLoadBedMesh(String profile) {
        return "G29 L" + profile + "\n"; // Marlin load mesh
    }

    @Override
    protected String emitProbeCalibrate() {
        return "G30\n"; // Single probe point (simplified)
    }

    @Override
    protected String emitSetPressureAdvance(double value) {
        return "M900 K" + value + "\n"; // Marlin linear advance
    }

    @Override
    protected String emitSetNozzle(double value) {
        return "";
    }

    @Override
    protected String emitSetFilament(double value) {
        return "";
    }

    @Override
    protected String emitSetLayerHeight(double value) {
        return "";
    }

    @Override
    protected String emitSetExtrusionMultiplier(double value) {
        return "";
    }

    @Override
    protected String emitEnableAutoExtrude(boolean enabled) {
        return "";
    }

    @Override
    protected String emitIfStart(String condition) {
        return "; Marlin does not support if statements\n; Skipped: if " + condition + "\n";
    }

    @Override
    protected String emitIfEnd() {
        return "";
    }

    @Override
    protected String emitCooldown(String target) {
        if (target == null) {
            return "M104 S0\nM140 S0\nM141 S0\n";
        } else {
            switch (target) {
                case "extruder":
                    return "M104 S0\n";
                case "bed":
                    return "M140 S0\n";
                case "chamber":
                    return "M141 S0\n";
                default:
                    return "";
            }
        }
    }

    @Override
    protected String emitLayerStart(int layer) {
        double z = layer * settings.getLayerHeight();
        return "G1 Z" + String.format("%.3f", z) + "\n";
    }

    @Override
    protected String emitLayerEnd() {
        return "";
    }
}