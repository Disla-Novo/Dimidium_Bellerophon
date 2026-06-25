package maindeveloper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class HardwareLimiterTest {

    @Test
    void acceptsValidMove() {
        var limiter = new HardwareLimiter(220, 220, 250);
        assertDoesNotThrow(() -> limiter.checkAndMove("X", 100));
    }

    @Test
    void rejectsMoveBeyondMaxX() {
        var limiter = new HardwareLimiter(220, 220, 250);
        assertThrows(RuntimeException.class, () -> limiter.checkAndMove("X", 500));
    }

    @Test
    void rejectsNegativeMove() {
        var limiter = new HardwareLimiter(220, 220, 250);
        assertThrows(RuntimeException.class, () -> limiter.checkAndMove("Y", -5));
    }

    @Test
    void respectsTemporaryLimits() {
        var limiter = new HardwareLimiter(220, 220, 250);
        limiter.setTemporaryLimits(50, 50, 50);
        assertThrows(RuntimeException.class, () -> limiter.checkAndMove("X", 100));
    }
}