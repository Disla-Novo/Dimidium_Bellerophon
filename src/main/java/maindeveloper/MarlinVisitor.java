package main.java.maindeveloper;

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
                case "extruder": return "M109 R" + (int)value + "\n";
                case "bed":      return "M190 R" + (int)value + "\n";
                case "chamber":  return "M141 S" + (int)value + "\n";
                default: return "";
            }
        } else {
            switch (target) {
                case "extruder": return "M104 S" + (int)value + "\n";
                case "bed":      return "M140 S" + (int)value + "\n";
                case "chamber":  return "M141 S" + (int)value + "\n";
                default: return "";
            }
        }
    }

    @Override
    protected String emitSetHeater(String target, double value) {
        return emitHeat(target, value, false);
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
    protected String emitRespond(String message) {
        return "M117 " + message + "\n"; // Marlin LCD message
    }

    @Override
    protected String emitCooldown(String target) {
        // adding cooldown stuff for marlin
        if (target == null) {
            // Turn off all heaters
            return "M104 S0\nM140 S0\nM141 S0\n";
        } else {
            // Turn off specific heater
            switch (target) {
                case "extruder": return "M104 S0\n";
                case "bed":      return "M140 S0\n";
                case "chamber":  return "M141 S0\n";
                default: return "";
            }
        }
    }

    @Override
    protected String emitTimeoutSet(double seconds) {
        return "M84 S" + (int)seconds + "\n"; // Marlin idle timeout
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
    protected String emitSetFan(double value) {
        // SET_FAN...Marlin uses M106
        int marlinSpeed = (int)(value * 255);
        return "M106 S" + marlinSpeed + "\n";
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
    protected String emitPrintFile(String filename) {
        return "M23 " + filename + "\nM24\n"; // Marlin select + start print
    }

    @Override
    protected String emitIfStart(String condition) {
        // Optionally extract the condition to show what was skipped
        return "; Marlin does not support if statements\n; Skipped: if " + condition + "\n";
    }

    @Override
    protected String emitIfEnd() {
        return "";
    }
}