package maindeveloper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class CompilerTest {

    @Test
    void compilesMaurerRose() {
        String source = """
            M.title "Bellerophon_Maurer_Rose"
            Absolute
            SetSpeed = 6000
            Heat extruder = 160
            Heat bed = 65
            Home
            Heat extruder = 215
            MoveTo x=10 y=20 z=0.15
            Relative
            RelativeExtrusion
            SetSpeed = 3000
            MoveTo x=200 e=10
            Absolute
            MoveTo x=110 y=110 z=0.15
            SetFan = 1
            SetSpeed = 2200
            RelativeExtrusion
            Brepeat 1200
                MoveTo x+=cos(i*71)*30 y+=sin(i*71)*30 z+=0.04 e=1.8
            end
            Absolute
            SetSpeed = 7000
            MoveTo z=10 x=110 y=220
            Cooldown
            Heat bed = 35
            Relative
            SetSpeed = 2000
            MoveTo y=-210
            Absolute
            Home
            M.end
            """;

        var tree = TestUtils.parse(source);
        var visitor = new KlipperVisitor(new PrinterProfile());
        assertDoesNotThrow(() -> visitor.visit(tree));
    }

    @Test
    void compilesFullExpressionTest() {
        String source = """
            M.title "full_expression_test"
            base_temp = 200
            fan_speed = 0.75
            nozzle = 0.6
            filament = 2.85
            layer_h = 0.3
            multiplier = 0.95
            auto = 1
            timeout = 120
            pressure = 0.04
            dwell_ms = 500
            Heat extruder = base_temp + 10
            Set_Heater_Temperature bed = base_temp - 30
            SetNozzle = nozzle * 1.0
            SetFilament = filament
            SetLayerHeight = layer_h * 1.5
            SetExtrusionMultiplier = multiplier - 0.05
            EnableAutoExtrude = auto
            SetFan = fan_speed * 2
            TIMEOUT_SET = timeout + 30
            SetPressureAdvance = pressure * 2.5
            Dwell = dwell_ms / 2 ms
            Absolute
            SetSpeed = 3000
            MoveTo x=100 y=100
            MoveTo x=100+50 y=100+50
            M.end
            """;

        var tree = TestUtils.parse(source);
        var visitor = new KlipperVisitor(new PrinterProfile());
        assertDoesNotThrow(() -> visitor.visit(tree));
    }

    @Test
    void compilesRRepeat() {
        String source = """
            M.title "r_repeat"
            MoveTo x=160 y=160 z=10
            Relative
            SetSpeed = 6500
            repeat 4
                MoveTo x=20 y=0
                MoveTo x=0 y=20
                MoveTo x=-20 y=0
                MoveTo x=0 y=-20
            end
            Respond MSG "Square corner completed"
            Home
            Absolute
            M.end
            """;

        var tree = TestUtils.parse(source);
        var visitor = new KlipperVisitor(new PrinterProfile());
        assertDoesNotThrow(() -> visitor.visit(tree));
    }

    @Test
    void compilesCircle() {
        String source = """
            M.title "circle"
            Absolute
            SetSpeed = 24000
            Brepeat 3
                Brepeat 100
                    MoveTo x=160 + cos(i*3.6)*50 y=160 + sin(i*3.6)*50 z=10
                end
            end
            Home
            M.end
            """;

        var tree = TestUtils.parse(source);
        var visitor = new KlipperVisitor(new PrinterProfile());
        assertDoesNotThrow(() -> visitor.visit(tree));
    }

    @Test
    void compilesLayerTest() {
        String source = """
            M.title "test"
            Absolute
            MoveTo z=5
            Layer 3
                MoveTo x=100 y=50
                MoveTo x=120 y=50
            end
            M.end
            """;

        var tree = TestUtils.parse(source);
        var visitor = new KlipperVisitor(new PrinterProfile());
        assertDoesNotThrow(() -> visitor.visit(tree));
    }
}