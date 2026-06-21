package main.java.maindeveloper;

public class KlipperVisitor extends GCodeVisitor {

    public KlipperVisitor(PrinterProfile profile) {
        super(profile);
    }

    // ---- Klipper-specific
    @Override
    protected String emitMacroHeader(String macroName) {
        return "[gcode_macro " + macroName + "]\ngcode:\n";
    }

    @Override
    protected String emitHeat(String target, double value, boolean wait) {
        if (wait) {
            switch (target) {
                case "extruder": return "M109 S" + (int)value + "\n";
                case "bed":      return "M190 S" + (int)value + "\n";
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
        return "PAUSE\n";
    }

    @Override
    protected String emitResume() {
        return "RESUME\n";
    }

    @Override
    protected String emitRespond(String message) {
        return "RESPOND MSG=\"" + message + "\"\n";
    }

    @Override
    protected String emitCooldown(String target) {
        if (target == null) {
            return "TURN_OFF_HEATERS\n";
        } else {
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
        return "SET_IDLE_TIMEOUT TIMEOUT=" + (int)seconds + "\n";
    }

    @Override
    protected String emitLoadBedMesh(String profile) {
        return "BED_MESH_PROFILE LOAD=" + profile + "\n";
    }

    @Override
    protected String emitProbeCalibrate() {
        return "PROBE_CALIBRATE\n";
    }

    @Override
    protected String emitSetPressureAdvance(double value) {
        return "SET_PRESSURE_ADVANCE ADVANCE=" + value + "\n";
    }

    @Override
    protected String emitSetFan(double value) {
        return "SET_FAN SPEED=" + (int)value + "\n";
    }

    @Override
    protected String emitMacroCall(String macroName) {
        return macroName + "\n";
    }

    @Override
    protected String emitBedMeshCalibrate() {
        return "BED_MESH_CALIBRATE\n";
    }

    @Override
    protected String emitPrintFile(String filename) {
        return "SDCARD_PRINT_FILE FILENAME=\"" + filename + "\"\n";
    }

    @Override
    protected String emitIfStart(String condition) {
        return "{% if " + condition + " %}\n";
    }

    @Override
    protected String emitIfEnd() {
        return "{% endif %}\n";
    }
}