package maindeveloper.core;

import java.util.Stack;

/**
 * Represents the state of the machine, including its current position,
 * target position, mode, DSL scope, and Brepeat bookkeeping.
 */
public class MachineState {

    // ---- Position ----
    public double currentX = 0;
    public double currentY = 0;
    public double currentZ = 0;

    // ---- Target scratch for the move currently being built in
    // visitCoordList ----
    public double targetX = Double.NaN;
    public double targetY = Double.NaN;
    public double targetZ = Double.NaN;
    public boolean hasManualE = false;
    public double manualEValue = 0.0;

    // ---- Mode ----
    public boolean relativeMode = false;

    // ---- DSL scope ----
    public boolean insideLayer = false;
    public boolean insideJrepeat = false;
    public Stack<Integer> iterationStack = new Stack<>();

    // ---- Brepeat bookkeeping ----
    public double centerX = 0;
    public double centerY = 0;

    /**
     * Clears all machine state. Called at the start of every macro so
     * that nothing from the previous macro - position, mode, or scope
     * flags left set by a loop/layer that never got a chance to
     * restore them - carries over into the next, independent macro.
     *
     * This is a superset of what visitMacro used to clear by hand
     * (currentX/Y/Z, relativeMode). It also clears the eight fields
     * visitMacro never touched (insideLayer, insideJrepeat,
     * iterationStack, targetX/Y/Z, hasManualE, manualEValue,
     * centerX/Y), which were previously correct only by accident -
     * every loop restored them before returning, and visitCoordList
     * overwrote the scratch fields at its own top. See the accompanying
     * writeup for the exception case where that accident didn't hold.
     */
    public void reset() {
        currentX = 0;
        currentY = 0;
        currentZ = 0;
        relativeMode = false;

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

    /**
     * Clears just the per-move scratch fields. Called at the start of
     * every coordList so a leftover target/E value from a previous
     * move never leaks into a move that doesn't set that axis.
     */
    public void resetTargets() {
        targetX = Double.NaN;
        targetY = Double.NaN;
        targetZ = Double.NaN;
        hasManualE = false;
        manualEValue = 0.0;
    }
}