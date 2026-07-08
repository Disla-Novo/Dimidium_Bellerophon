package maindeveloper.dialects;

import maindeveloper.core.GCodeVisitor;
import maindeveloper.core.PrinterProfile;

// MVP/foundation target for RepRapFirmware (RRF, e.g. Duet boards). RRF
// speaks the same base G-code as Marlin for the commands implemented here
// (home/move/heat), so only what's needed to compile a simple test file is
// implemented - everything else is stubbed out until there's a real need
// for RRF's object-model config commands (M563/M307/M950, etc).
public class RepRapVisitor extends GCodeVisitor {

    public RepRapVisitor(PrinterProfile profile) {
        super(profile);
    }

    @Override
    protected String emitMacroHeader(String macroName) {
        return "; " + macroName + "\n";
    }

    @Override
    protected String emitHeat(String target, double value, boolean wait) {
        String setCmd = wait ? "M109" : "M104";
        switch (target) {
            case "extruder":
                return setCmd + " S" + (int) value + "\n";
            case "bed":
                return (wait ? "M190" : "M140") + " S" + (int) value + "\n";
            case "chamber":
                return (wait ? "M191" : "M141") + " S" + (int) value + "\n";
            default:
                return "";
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
    protected String emitHome(String coordList) {
        return "";
    }

    @Override
    protected String emitCooldown(String target) {
        return "";
    }

    @Override
    protected String emitWaitForTemp(String target) {
        return "";
    }

    @Override
    protected String emitSetSpeed(double value) {
        return "";
    }

    @Override
    protected String emitSetFan(double value) {
        return "";
    }

    @Override
    protected String emitAbsolute() {
        return "";
    }

    @Override
    protected String emitRelative() {
        return "";
    }

    @Override
    protected String emitRelativeExtrusion() {
        return "";
    }

    @Override
    protected String emitResetExtruder() {
        return "";
    }

    @Override
    protected String emitPause() {
        return "";
    }

    @Override
    protected String emitResume() {
        return "";
    }

    @Override
    protected String emitDwell(double milliseconds) {
        return "";
    }

    @Override
    protected String emitTimeoutSet(double seconds) {
        return "";
    }

    @Override
    protected String emitRespond(String message) {
        return "";
    }

    @Override
    protected String emitPrintFile(String filename) {
        return "";
    }

    @Override
    protected String emitMacroCall(String macroName) {
        return "";
    }

    @Override
    protected String emitBedMeshCalibrate() {
        return "";
    }

    @Override
    protected String emitLoadBedMesh(String profile) {
        return "";
    }

    @Override
    protected String emitProbeCalibrate() {
        return "";
    }

    @Override
    protected String emitSetPressureAdvance(double value) {
        return "";
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
        return "";
    }

    @Override
    protected String emitIfEnd() {
        return "";
    }

    @Override
    protected String emitLayerStart(int layer) {
        return "";
    }

    @Override
    protected String emitLayerEnd() {
        return "";
    }
}
