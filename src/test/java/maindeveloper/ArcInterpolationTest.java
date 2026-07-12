package maindeveloper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import maindeveloper.core.PrinterProfile;
import maindeveloper.dialects.KlipperVisitor;
import maindeveloper.dialects.MarlinVisitor;

public class ArcInterpolationTest {

    @Test
    void collapsesInlineCircleMathIntoAnArc() {
        String source = """
                M.title "circle"
                Absolute
                Brepeat 100
                    MoveTo x=160 + cos(i*3.6)*50 y=160 + sin(i*3.6)*50 z=10
                end
                M.end
                """;

        var tree = TestUtils.parse(source);
        var visitor = new MarlinVisitor(new PrinterProfile());
        String output = visitor.visit(tree);

        long g1Count = output.lines().filter(l -> l.trim().matches("G1 .*")).count();
        long arcCount = output.lines().filter(l -> l.trim().matches("G[23] .*")).count();

        assertEquals(1, g1Count, "only the entry move onto the circle should remain a G1");
        assertEquals(1, arcCount, "the rest of the loop should collapse into a single arc");
        assertTrue(output.contains("G3"), "increasing angle (i*3.6) is a counter-clockwise sweep");
        assertTrue(output.contains("I-50.000"), "center is 50mm in -X from the first circle point");
    }

    @Test
    void collapsesMultiStatementCircleMathIntoAnArc() {
        // this mirrors Input_Examples/myrandom_examples/parametric_circle.bph -
        // a common authoring style where pos_x/pos_y/angle are computed as
        // separate local-variable assignments before the MoveTo, rather than
        // inlining the cos/sin math directly in the MoveTo's coordinates
        String source = """
                M.title "parametric_circle"
                Absolute
                radius = 50
                center_x = 100
                center_y = 100
                angle = 0
                Brepeat 360
                    pos_x = center_x + radius * cos(angle)
                    pos_y = center_y + radius * sin(angle)
                    MoveTo x=pos_x y=pos_y z=0.2
                    angle = angle + 1
                end
                M.end
                """;

        var tree = TestUtils.parse(source);
        var visitor = new MarlinVisitor(new PrinterProfile());
        String output = visitor.visit(tree);

        long g1Count = output.lines().filter(l -> l.trim().matches("G1 .*")).count();
        long arcCount = output.lines().filter(l -> l.trim().matches("G[23] .*")).count();

        assertEquals(1, g1Count);
        assertEquals(1, arcCount);
    }

    @Test
    void splitsAFullyClosedLoopIntoTwoHalfArcs() {
        // Nine sampled points (0°..360° inclusive at 45° intervals).
        // This satisfies the minimum point requirement for arc detection
        // while still producing a closed loop that should be emitted as
        // two half-circle G2/G3 commands.
        String source = """
                M.title "closed_loop"
                Absolute
                Brepeat 9
                    MoveTo x=100+50*cos(i*45) y=100+50*sin(i*45)
                end
                M.end
                """;

        var tree = TestUtils.parse(source);
        var visitor = new MarlinVisitor(new PrinterProfile());
        String output = visitor.visit(tree);

        long arcCount = output.lines().filter(l -> l.trim().matches("G[23] .*")).count();
        assertEquals(2, arcCount, "a closed loop should split into two half-circle arcs, not one ambiguous full circle");
    }

    @Test
    void doesNotTreatMultipleMovesPerIterationAsAnArc() {
        String source = """
                M.title "square"
                Absolute
                MoveTo x=0 y=0
                Relative
                Brepeat 4
                    MoveTo x=20 y=0
                    MoveTo x=0 y=20
                end
                M.end
                """;

        var tree = TestUtils.parse(source);
        var visitor = new MarlinVisitor(new PrinterProfile());
        String output = visitor.visit(tree);

        long g1Count = output.lines().filter(l -> l.trim().matches("G1 .*")).count();
        assertEquals(9, g1Count, "1 initial move + 4 iterations of 2 moves each");
        assertFalse(output.lines().anyMatch(l -> l.trim().matches("G[23] .*")));
    }

    @Test
    void doesNotTreatCollinearPointsAsAnArc() {
        // 10 points so this genuinely exercises the collinear/degenerate-fit
        // rejection rather than just being caught by the minimum-point gate
        String source = """
                M.title "straight_line"
                Absolute
                Brepeat 10
                    MoveTo x=i*10 y=0
                end
                M.end
                """;

        var tree = TestUtils.parse(source);
        var visitor = new MarlinVisitor(new PrinterProfile());
        String output = visitor.visit(tree);

        assertFalse(output.lines().anyMatch(l -> l.trim().matches("G[23] .*")));
        long g1Count = output.lines().filter(l -> l.trim().matches("G1 .*")).count();
        assertEquals(10, g1Count);
    }

    @Test
    void doesNotTreatSmallSmoothCurvesAsAnArc() {
        // Found via real hardware testing on PR #47: with too few points,
        // a Kasa least-squares fit can match a non-circular curve to some
        // (often enormous-radius) circle almost exactly - this parabola-like
        // curve used to collapse into a single bogus G2 arc.
        String source = """
                M.title "small_smooth_curve"
                Relative
                Brepeat 5
                    MoveTo x=(i*2)+3 y=sqrt(i+1)
                end
                M.end
                """;

        var tree = TestUtils.parse(source);
        var visitor = new MarlinVisitor(new PrinterProfile());
        String output = visitor.visit(tree);

        long arcCount = output.lines().filter(l -> l.trim().matches("G[23] .*")).count();
        long g1Count = output.lines().filter(l -> l.trim().matches("G1 .*")).count();

        assertEquals(0, arcCount, "small smooth curves should not be mistaken for arcs");
        assertEquals(5, g1Count, "all original moves should remain when arc confidence is too low");
    }

    @Test
    void otherFirmwareTargetsAreUnaffected() {
        String source = """
                M.title "circle"
                Absolute
                Brepeat 100
                    MoveTo x=160 + cos(i*3.6)*50 y=160 + sin(i*3.6)*50 z=10
                end
                M.end
                """;

        var tree = TestUtils.parse(source);
        var visitor = new KlipperVisitor(new PrinterProfile());
        String output = visitor.visit(tree);

        long moveCount = output.lines().filter(l -> l.trim().matches("G1 .*")).count();
        assertEquals(100, moveCount, "arc optimization is Marlin-specific; Klipper should be untouched");
    }
}
