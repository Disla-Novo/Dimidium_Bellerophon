package maindeveloper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import maindeveloper.core.PrinterProfile;
import maindeveloper.dialects.KlipperVisitor;
import maindeveloper.dialects.MarlinVisitor;
import maindeveloper.dialects.RepRapVisitor;

public class FirmwareAdapterTest {

    @Test
    void klipperVisitorConstructs() {
        var profile = new PrinterProfile();
        assertDoesNotThrow(() -> new KlipperVisitor(profile));
    }

    @Test
    void marlinVisitorConstructs() {
        var profile = new PrinterProfile();
        assertDoesNotThrow(() -> new MarlinVisitor(profile));
    }

    @Test
    void repRapVisitorConstructs() {
        var profile = new PrinterProfile();
        assertDoesNotThrow(() -> new RepRapVisitor(profile));
    }

    @Test
    void visitorsCanParseSameProgram() {
        String source = """
                M.title "test"
                Heat extruder = 200
                M.end
                """;

        var tree = TestUtils.parse(source);
        var profile = new PrinterProfile();

        var klipper = new KlipperVisitor(profile);
        var marlin = new MarlinVisitor(profile);
        var reprap = new RepRapVisitor(profile);

        assertDoesNotThrow(() -> klipper.visit(tree));
        assertDoesNotThrow(() -> marlin.visit(tree));
        assertDoesNotThrow(() -> reprap.visit(tree));
    }

    @Test
    void repRapVisitorEmitsMoveAndHeatCommands() {
        String source = """
                M.title "test_rrf"
                Heat extruder = 200
                MoveTo x=10 y=10
                M.end
                """;

        var tree = TestUtils.parse(source);
        var visitor = new RepRapVisitor(new PrinterProfile());
        String output = visitor.visit(tree);

        assertTrue(output.contains("M109 S200"), "expected a wait-for-temp heat command");
        assertTrue(output.contains("G1 X10.000 Y10.000"), "expected a G1 move command");
    }
}