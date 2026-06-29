package maindeveloper.dialects;

import maindeveloper.core.GCodeVisitor;
import maindeveloper.core.PrinterProfile;

public class KlipperVisitor extends GCodeVisitor {

    public KlipperVisitor(PrinterProfile profile) {
        super(profile);
    }

    @Override
    protected String emitMacroHeader(String macroName) {
        return "[gcode_macro " + macroName + "]\ngcode:\n";
    }

    @Override
    protected String emitHeat(String target, double value, boolean wait) {
        if (wait) {
            switch (target) {
                case "extruder":
                    return "M109 S" + (int) value + "\n";
                case "bed":
                    return "M190 S" + (int) value + "\n";
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
    protected String emitCooldown(String target) {
        if (target == null) {
            return "TURN_OFF_HEATERS\n";
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
        return "SET_FAN SPEED=" + (int) value + "\n";
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
        return "PAUSE\n";
    }

    @Override
    protected String emitResume() {
        return "RESUME\n";
    }

    @Override
    protected String emitDwell(double milliseconds) {
        return "G4 P" + (int) milliseconds + "\n";
    }

    @Override
    protected String emitTimeoutSet(double seconds) {
        return "SET_IDLE_TIMEOUT TIMEOUT=" + (int) seconds + "\n";
    }

    @Override
    protected String emitRespond(String message) {
        return "RESPOND MSG=\"" + message + "\"\n";
    }

    @Override
    protected String emitPrintFile(String filename) {
        return "SDCARD_PRINT_FILE FILENAME=\"" + filename + "\"\n";
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
        return "{% if " + condition + " %}\n";
    }

    @Override
    protected String emitIfEnd() {
        return "{% endif %}\n";
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