package maindeveloper.core;

import java.util.Stack;

/**
 * Represents the state of the machine, including its current position,
 * target position, mode, DSL scope, and Brepeat bookkeeping.
 */
public class MachineState {

    // Position
    public double currentX = 0;
    public double currentY = 0;
    public double currentZ = 0;

    // Target scratch for the move currently being built in
    // visitCoordList 
    public double targetX = Double.NaN;
    public double targetY = Double.NaN;
    public double targetZ = Double.NaN;
    public boolean hasManualE = false;
    public double manualEValue = 0.0;

    //  Mode 
    public boolean relativeMode = false;
    public boolean relativeExtrusionActive = false;

   
    public boolean autoExtrudeEnabled = false;
    public boolean autoRetractEnabled = false;
    public int autoRetractEnableLine = 0;
    public boolean hasEverExtruded = false;
    public double currentFeedrate = 0;

    // DSL scope 
    public boolean insideLayer = false;
    public boolean insideJrepeat = false;
    public Stack<Integer> iterationStack = new Stack<>();

    // Brepeat bookkeeping 
    public double centerX = 0;
    public double centerY = 0;
    /**
     * Resets the machine state to its initial values.
     */
    public void reset() {
        currentX = 0;
        currentY = 0;
        currentZ = 0;
        relativeMode = false;
        relativeExtrusionActive = false;
        autoExtrudeEnabled = false;
        autoRetractEnabled = false;
        autoRetractEnableLine = 0;
        hasEverExtruded = false;
        currentFeedrate = 0;

        targetX = Double.NaN;
        targetY = Double.NaN;
        targetZ = Double.NaN;
        hasManualE = false;
        manualEValue = 0.0;

        insideLayer = false;
        insideJrepeat = false;
        iterationStack.clear();

        centerX = 0;
        centerY = 0;
    }

    public void resetTargets() {
        targetX = Double.NaN;
        targetY = Double.NaN;
        targetZ = Double.NaN;
        hasManualE = false;
        manualEValue = 0.0;
    }
}