package maindeveloper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

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

        assertDoesNotThrow(() -> klipper.visit(tree));
        assertDoesNotThrow(() -> marlin.visit(tree));
    }
}