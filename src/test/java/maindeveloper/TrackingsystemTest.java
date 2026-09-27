package maindeveloper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import maindeveloper.core.PrinterProfile;
import maindeveloper.dialects.KlipperVisitor;

/* Tests for the tracking system in the Klipper visitor, ensuring correct handling of move commands, loops, and extrusion. */
public class TrackingsystemTest {

    private static List<String> moveLines(String output) {
        return output.lines().map(String::trim).filter(l -> l.startsWith("G1 ")).toList();
    }

    @Test
    void targetScratchDoesNotLeakBetweenMoves() {
        String source = """
                M.title "t"
                Absolute
                MoveTo x=10 y=10
                MoveTo y=20
                M.end
                """;

        var tree = TestUtils.parse(source);
        var output = new KlipperVisitor(new PrinterProfile()).visit(tree);
        var lines = moveLines(output);

        assertEquals(List.of("G1 X10.000 Y10.000", "G1 Y20.000"), lines,
                "the second move must not re-emit X10.000 just because the first move set it");
    }

    @Test
    void manualEDoesNotLeakBetweenMoves() {
        String source = """
                M.title "t"
                Absolute
                RelativeExtrusion
                MoveTo x=10 e=5
                MoveTo x=20
                M.end
                """;

        var tree = TestUtils.parse(source);
        var output = new KlipperVisitor(new PrinterProfile()).visit(tree);
        var lines = moveLines(output);

        assertEquals(List.of("G1 X10.000 E5.000", "G1 X20.000"), lines,
                "the second move must not repeat E5.000 just because the first move set an explicit E value");
    }

    @Test
    void innermostLoopsIteratorWinsWhenBrepeatsAreNested() {
       /* This test ensures that the innermost loop's iterator takes precedence when Brepeats are nested. */
        String source = """
                M.title "t"
                Absolute
                Brepeat 2
                    Brepeat 3
                        MoveTo x=i y=0
                    end
                end
                M.end
                """;

        var tree = TestUtils.parse(source);
        var output = new KlipperVisitor(new PrinterProfile()).visit(tree);
        var lines = moveLines(output);

        assertEquals(List.of(
                "G1 X0.000 Y0.000", "G1 X1.000 Y0.000", "G1 X2.000 Y0.000",
                "G1 X0.000 Y0.000", "G1 X1.000 Y0.000", "G1 X2.000 Y0.000"),
                lines, "inner Brepeat's own 0..2 should run twice, once per outer iteration - not continue counting up");
    }

    @Test
    void repeatNestedInBrepeatSeesTheEnclosingBrepeatsIteratorUnchanged() {
        /* This test ensures that a repeat nested inside a Brepeat sees the enclosing Brepeat's iterator unchanged. */
        String source = """
                M.title "t"
                Absolute
                Brepeat 2
                    repeat 3
                        MoveTo x=i y=0
                    end
                end
                M.end
                """;

        var tree = TestUtils.parse(source);
        var output = new KlipperVisitor(new PrinterProfile()).visit(tree);
        var lines = moveLines(output);

        assertEquals(List.of(
                "G1 X0.000 Y0.000", "G1 X0.000 Y0.000", "G1 X0.000 Y0.000",
                "G1 X1.000 Y0.000", "G1 X1.000 Y0.000", "G1 X1.000 Y0.000"),
                lines, "repeat doesn't have its own iterator, so all 3 passes should see the Brepeat's current i unchanged");
    }

    @Test
    void zAxisMovementInsideLayerBlockIsRejected() {
        String source = """
                M.title "t"
                Layer 2
                    MoveTo z=5
                end
                M.end
                """;

        var tree = TestUtils.parse(source);
        var visitor = new KlipperVisitor(new PrinterProfile());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> visitor.visit(tree));
        assertTrue(ex.getMessage().contains("not allowed inside Layer blocks"),
                "expected the Layer Z-axis guard, got: " + ex.getMessage());
    }

    @Test
    void zAxisMovementAllowedAgainAfterLayerBlockEnds() {
        String source = """
                M.title "t"
                Absolute
                Layer 2
                    MoveTo x=10 y=10
                end
                MoveTo z=5
                M.end
                """;

        var tree = TestUtils.parse(source);
        var output = new KlipperVisitor(new PrinterProfile()).visit(tree);

        assertTrue(output.lines().anyMatch(l -> l.trim().equals("G1 Z5.000")),
                "Z movement right after the Layer block should be allowed, not still blocked by a stuck insideLayer flag");
    }

    @Test
    void positionAndModeDoNotCarryOverToTheNextMacro() {
        String source = """
                M.title "first"
                Absolute
                MoveTo x=100 y=100 z=50
                Relative
                M.end

                M.title "second"
                MoveTo x=5 y=5
                M.end
                """;

        var tree = TestUtils.parse(source);
        var output = new KlipperVisitor(new PrinterProfile()).visit(tree);
        var lines = moveLines(output);

        assertTrue(lines.contains("G1 X5.000 Y5.000"),
                "second macro should start from (0,0) in absolute mode regardless of where the first macro left off");
        assertFalse(lines.stream().anyMatch(l -> l.contains("-95.000")),
                "second macro must not compute its move as an offset from the first macro's ending position");
    }
}