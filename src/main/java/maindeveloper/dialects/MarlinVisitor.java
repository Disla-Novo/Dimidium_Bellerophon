package maindeveloper.dialects;

import maindeveloper.core.GCodeVisitor;
import maindeveloper.core.PrinterProfile;

// here we just add overrides that arent supported. 
// these typically are just word commands that marlin doesn't support. 
// awaiting a few more, dont forget to add. 

public class MarlinVisitor extends GCodeVisitor {
    public MarlinVisitor(PrinterProfile profile) {
        super(profile); // calls the GCodeVisitor constructor above
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