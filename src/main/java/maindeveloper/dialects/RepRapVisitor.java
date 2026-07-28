package maindeveloper.dialects;

import maindeveloper.core.GCodeVisitor;
import maindeveloper.core.PrinterProfile;

// MVP/foundation target for RepRapFirmware (RRF, e.g. Duet boards). RRF
// speaks the same base G-code as Marlin for the commands implemented here
// (home/move/heat), so only what's needed to compile a simple test file is
// implemented - everything else is stubbed out until there's a real need
// for RRF's object-model config commands (M563/M307/M950, etc).

// implemented issue #68

public class RepRapVisitor extends GCodeVisitor {
    private boolean toolSelected = false;

    public RepRapVisitor(PrinterProfile profile) {
        super(profile);
    }

    // if tool is selected, emit T0 once, not per-macro.

    private String ensureToolSelected() {
        if (!toolSelected) {
            toolSelected = true;
            return "T0\n";
        }
        return "";
    }

    @Override
    protected String emitMacroHeader(String macroName) {
        return "; " + macroName + "\n";
    }

    // adding the prefix
    @Override
    protected String emitHeat(String target, double value, boolean wait) {
        String setCmd = wait ? "M109" : "M104";
        switch (target) {
            case "extruder":
                return ensureToolSelected() + setCmd + " S" + (int) value + "\n";
            case "bed":
                return (wait ? "M190" : "M140") + " S" + (int) value + "\n";
            case "chamber":
                return (wait ? "M191" : "M141") + " S" + (int) value + "\n";
            default:
                return "";
        }
    }

    // adding the prefix
    @Override
    protected String emitSetHeater(String target, double value) {
        switch (target) {
            case "extruder":
                return ensureToolSelected() + "M104 S" + (int) value + "\n";
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

    // adding the prefix
    @Override
    protected String emitMoveTo(String coordList) {
        if (coordList == null || coordList.isEmpty()) {
            return "";
        }

        // Only select the tool if this move includes extrusion (E)
        String prefix = coordList.contains("E") ? ensureToolSelected() : "";
        return prefix + "G1 " + coordList + "\n";
    }

    // adding prefix
    @Override
    protected String emitHome(String coordList) {
        if (coordList == null || coordList.isEmpty()) {
            return "G28\n";
        }
        return "G28 " + coordList + "\n";
    }

    @Override
    protected String emitCooldown(String target) {

        if (target == null) {
            return "M104 S0\nM140 S0\nM141 S0\n";

        }
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

    @Override
    protected String emitWaitForTemp(String target) {
        switch (target) {
            case "extruder":
                // P0 targets Tool 0 (single-extruder MVP)
                return "M116 P0\n";
            case "bed":
                // H0 is the standard RRF bed heater index
                return "M116 H0\n";
            case "chamber":
                // Heater index unknown for chamber; bare M116 waits on
                // all heaters, which is probably fine for a single-chamber printer
                return "M116\n";
            default:
                return "";
        }
    }

    @Override
    protected String emitSetSpeed(double value) {
        return "G1 F" + (int) value + "\n";
    }

    @Override
    protected String emitSetFan(double value) {
        // so, RRF's S is 0.0 - 1.0 fraction by default. so we expect the call to pass
        // 0.0-1.0 value
        return "M106 S" + value + "\n";
    }

    @Override
    protected String emitAbsolute() {
        return "G90\n";
    }

    @Override
    protected String emitRelative() {
        return "G91\n"
                + "; Note: RRF does not set extrusion relative via G91, "
                + "use RelativeExtrusion (M83) explicitly if needed\n";
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
        return "M25\n";
    }

    @Override
    protected String emitResume() {
        return "M24\n";
    }

    @Override
    protected String emitDwell(double milliseconds) {
        return "G4 P" + (int) milliseconds + "\n";
    }

    @Override
    protected String emitTimeoutSet(double seconds) {
        return "M84 S" + (int) seconds + "\n";
    }

    @Override
    protected String emitRespond(String message) {
        return "M117 " + message + "\n";
    }

    @Override
    protected String emitPrintFile(String filename) {
        return "M32 " + filename + "\n";
    }

    @Override
    protected String emitMacroCall(String macroName) {
        // TODO: validate that this is the correct command for RRF
        if (macroName == null || macroName.isEmpty()) {
            return "";
        }
        
        // Strip quotes if they were preserved by the ANTLR STRING token
        String cleanMacro = macroName;
        if (cleanMacro.startsWith("\"") && cleanMacro.endsWith("\"")) {
            cleanMacro = cleanMacro.substring(1, cleanMacro.length() - 1);
        }
        
        // RRF calls macros using M98 and the file name/path
        return "M98 P\"" + cleanMacro + "\"\n";
    }

    @Override
    protected String emitBedMeshCalibrate() {
       // TODO: validate that this is the correct command for RRF
        // G29 in RRF executes the mesh.g macro to probe the bed. 
       return "G29\n";
    }

  @Override
protected String emitLoadBedMesh(String profile) {
    // TODO: validate that this is the correct command for RRF
    // RRF: G29 S1 loads a saved heightmap
    // P"name" specifies which profile to load
    if (profile == null || profile.isEmpty()) {
       
        return "G29 S1\n";
    }
    
    String cleanProfile = profile;
    if (cleanProfile.startsWith("\"") && cleanProfile.endsWith("\"")) {
        cleanProfile = cleanProfile.substring(1, cleanProfile.length() - 1);
    }
    
    return "G29 S1 P\"" + cleanProfile + "\"\n";
}


    @Override
    protected String emitProbeCalibrate() {
        return "G30\n";
    }

    @Override
    protected String emitSetPressureAdvance(double value) {
        // Default D0 for the
        // single-extruder MVP.
        return "M572 D0 S" + value + "\n";
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
        // RRF supports Meta-GCode (if/elif/else), but safely translating  
        // conditions to RRF's specific Object Model syntax is out of scope
        return "; Conditional skipped (not supported in RRF target)\n";
    }

    @Override
    protected String emitIfEnd() {
        return "; End conditional skipped\n";
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
