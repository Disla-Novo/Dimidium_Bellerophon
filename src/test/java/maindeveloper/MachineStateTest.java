package maindeveloper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import maindeveloper.core.MachineState;

// Tests MachineState directly. This means no parser, no visitor, no firmware dialect.
// We are exercising the tracking system
// on its own instead of only ever observing it indirectly through compiled
// G-code output.
public class MachineStateTest {

    @Test
    void resetClearsAllFourteenFields() {
        MachineState state = new MachineState();

        // dirty every field with a non-default value
        state.currentX = 12;
        state.currentY = 34;
        state.currentZ = 56;
        state.targetX = 1;
        state.targetY = 2;
        state.targetZ = 3;
        state.hasManualE = true;
        state.manualEValue = 7;
        state.relativeMode = true;
        state.insideLayer = true;
        state.insideJrepeat = true;
        state.iterationStack.push(9);
        state.centerX = 99;
        state.centerY = 88;

        state.reset();

        assertEquals(0.0, state.currentX, 0.0001);
        assertEquals(0.0, state.currentY, 0.0001);
        assertEquals(0.0, state.currentZ, 0.0001);
        assertTrue(Double.isNaN(state.targetX), "targetX should reset to NaN, matching visitCoordList's 'no target set yet' sentinel");
        assertTrue(Double.isNaN(state.targetY));
        assertTrue(Double.isNaN(state.targetZ));
        assertFalse(state.hasManualE);
        assertEquals(0.0, state.manualEValue, 0.0001);
        assertFalse(state.relativeMode, "a fresh macro should start in absolute mode");
        assertFalse(state.insideLayer);
        assertFalse(state.insideJrepeat);
        assertTrue(state.iterationStack.isEmpty(), "a leftover loop index from a previous macro must not survive reset()");
        assertEquals(0.0, state.centerX, 0.0001);
        assertEquals(0.0, state.centerY, 0.0001);
    }

    @Test
    void resetTargetsOnlyClearsPerMoveScratchFields() {
        MachineState state = new MachineState();

        // fields that resetTargets() must NOT touch
        state.currentX = 12;
        state.currentY = 34;
        state.relativeMode = true;
        state.insideLayer = true;
        state.centerX = 5;
        state.iterationStack.push(2);

        // fields it must clear
        state.targetX = 1;
        state.targetY = 2;
        state.targetZ = 3;
        state.hasManualE = true;
        state.manualEValue = 7;

        state.resetTargets();

        assertTrue(Double.isNaN(state.targetX));
        assertTrue(Double.isNaN(state.targetY));
        assertTrue(Double.isNaN(state.targetZ));
        assertFalse(state.hasManualE);
        assertEquals(0.0, state.manualEValue, 0.0001);

        // everything outside the five per-move scratch fields is untouched
        assertEquals(12.0, state.currentX, 0.0001);
        assertEquals(34.0, state.currentY, 0.0001);
        assertTrue(state.relativeMode);
        assertTrue(state.insideLayer);
        assertEquals(5.0, state.centerX, 0.0001);
        assertEquals(1, state.iterationStack.size());
    }
}