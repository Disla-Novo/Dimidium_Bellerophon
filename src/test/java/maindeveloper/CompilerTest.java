package maindeveloper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import maindeveloper.core.PrinterProfile;
import maindeveloper.dialects.KlipperVisitor;
import maindeveloper.dialects.MarlinVisitor;
import maindeveloper.dialects.RepRapVisitor;

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

    @Test
    void plainAssignmentDoesNotLeakAcrossMacros() {
        String source = """
                M.title "first"
                speed = 3000
                Heat extruder = speed
                M.end

                M.title "second"
                Heat extruder = speed + 10
                M.end
                """;

        var tree = TestUtils.parse(source);
        var visitor = new KlipperVisitor(new PrinterProfile());
        String output = visitor.visit(tree);

        assertTrue(output.contains("M109 S3000"), "first macro should see its own local speed");
        // second macro never set 'speed', so it should read as 0, not the first macro's 3000
        assertTrue(output.contains("M109 S10"), "second macro should NOT inherit the first macro's local variable");
        assertFalse(output.contains("M109 S3010"), "local variables must not leak between macros");
    }

    @Test
    void globalVariablePersistsAcrossMacros() {
        String source = """
                M.title "first"
                var speed = 3000
                Heat extruder = speed
                M.end

                M.title "second"
                Heat extruder = speed + 10
                M.end
                """;

        var tree = TestUtils.parse(source);
        var visitor = new KlipperVisitor(new PrinterProfile());
        String output = visitor.visit(tree);

        assertTrue(output.contains("M109 S3000"), "first macro should see the global speed it just set");
        assertTrue(output.contains("M109 S3010"), "second macro should see the global speed from the first macro");
    }

    @Test
    void localAssignmentShadowsGlobalOfSameName() {
        String source = """
                M.title "first"
                var speed = 3000
                Heat extruder = speed
                M.end

                M.title "second"
                speed = 100
                Heat extruder = speed
                M.end

                M.title "third"
                Heat extruder = speed
                M.end
                """;

        var tree = TestUtils.parse(source);
        var visitor = new KlipperVisitor(new PrinterProfile());
        String output = visitor.visit(tree);

        assertTrue(output.contains("M109 S3000"), "first macro reads the global");
        assertTrue(output.contains("M109 S100"), "second macro's local assignment shadows the global");
        // third macro never reassigned locally, so it should fall through to the
        // global (still 3000) rather than keeping the second macro's local 100
        long occurrencesOf3000 = output.lines().filter(line -> line.contains("M109 S3000")).count();
        assertTrue(occurrencesOf3000 == 2, "third macro should see the unmodified global again, not the second macro's local value");
    }
@Test
void relativeModeOutputsRelativeCoordinatesForMarlin() {
    String source = """
            M.title "relative_test_marlin"
            Absolute
            MoveTo x=50 y=50
            Relative
            MoveTo x=20 y=20
            M.end
            """;

    var tree = TestUtils.parse(source);
    var visitor = new MarlinVisitor(new PrinterProfile());
    String output = visitor.visit(tree);

    assertTrue(output.contains("G1 X20.000 Y20.000"),
            "Marlin: Relative move should output X20.000 Y20.000 (offset)");
    assertFalse(output.contains("G1 X70.000 Y70.000"),
            "Marlin: Should NOT output absolute coordinate X70.000 Y70.000 in relative mode");
}

@Test
void relativeModeOutputsRelativeCoordinatesForRepRap() {
    String source = """
            M.title "relative_test_reprap"
            Absolute
            MoveTo x=50 y=50
            Relative
            MoveTo x=20 y=20
            M.end
            """;

    var tree = TestUtils.parse(source);
    var visitor = new RepRapVisitor(new PrinterProfile());
    String output = visitor.visit(tree);

    assertTrue(output.contains("G1 X20.000 Y20.000"),
            "RepRap: Relative move should output X20.000 Y20.000 (offset)");
    assertFalse(output.contains("G1 X70.000 Y70.000"),
            "RepRap: Should NOT output absolute coordinate X70.000 Y70.000 in relative mode");
}

@Test
void plainAssignmentToReservedAxisNameRaisesClearError() {
    String source = """
            M.title "t"
            x = 5
            Home
            M.end
            """;

    var tree = TestUtils.parse(source);
    var visitor = new KlipperVisitor(new PrinterProfile());

    RuntimeException ex = assertThrows(RuntimeException.class, () -> visitor.visit(tree));
    assertTrue(ex.getMessage().contains("reserved axis name"),
            "expected a reserved-axis-name error, got: " + ex.getMessage());
}

@Test
void plainAssignmentToEachReservedAxisLetterRaisesClearError() {
    for (String axis : new String[] {"x", "y", "z", "e"}) {
        String source = """
                M.title "t"
                %s = 5
                Home
                M.end
                """.formatted(axis);

        var tree = TestUtils.parse(source);
        var visitor = new KlipperVisitor(new PrinterProfile());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> visitor.visit(tree),
                "expected '" + axis + " = 5' to raise an error");
        assertTrue(ex.getMessage().contains("'" + axis + "'"),
                "expected the error to name '" + axis + "', got: " + ex.getMessage());
    }
}

@Test
void globalAssignmentToReservedAxisNameStillRaisesClearError() {
    // no regression on the existing #45 behavior now that plain
    // assignments to axis letters are also handled
    String source = """
            M.title "t"
            var x = 5
            Home
            M.end
            """;

    var tree = TestUtils.parse(source);
    var visitor = new KlipperVisitor(new PrinterProfile());

    RuntimeException ex = assertThrows(RuntimeException.class, () -> visitor.visit(tree));
    assertTrue(ex.getMessage().contains("reserved axis letter"),
            "expected the existing global_assignment reserved-axis error, got: " + ex.getMessage());
}


@Test
void repRapVisitorEmitsRRFSpecificCommands() {
    String source = """
            M.title "RRF_Specific"
            Heat extruder = 210
            MoveTo x=10 y=0 e=0.5
            WaitForTemp bed
            SetFan = 0.5
            SetPressureAdvance 0.04
            Relative
            PRINTFILE "test.gcode"
            Cooldown
            M.end
            """;

    var tree = TestUtils.parse(source);
    var visitor = new RepRapVisitor(new PrinterProfile());
    String output = visitor.visit(tree);

    assertTrue(output.contains("T0"), "T0 should be emitted for extruder heat");
    long t0Count = output.lines().filter(line -> line.trim().equals("T0")).count();
    assertTrue(t0Count == 1, "T0 should be emitted exactly once, got " + t0Count);
    assertTrue(output.contains("M109 S210"), "Heat extruder: M109 S210");
    assertTrue(output.contains("G1 X10.000 Y0.000 E0.500"), "MoveTo with E: G1 ... E");
    assertTrue(output.contains("M116 H0"), "WaitForTemp bed: M116 H0");
    assertTrue(output.contains("M106 S0.5"), "SetFan: M106 S0.5 (fraction)");
    assertTrue(output.contains("M572 D0 S0.04"), "SetPressureAdvance: M572 D0 S0.04");
    assertTrue(output.contains("G91"), "Relative: G91");
    assertTrue(output.contains("; Note: RRF does not set extrusion relative via G91"), "Relative comment");
    assertTrue(output.contains("M32 test.gcode"), "PRINTFILE: M32 (single command)");
    assertTrue(output.contains("M104 S0"), "Cooldown extruder: M104 S0");
    assertTrue(output.contains("M140 S0"), "Cooldown bed: M140 S0");
    assertTrue(output.contains("M141 S0"), "Cooldown chamber: M141 S0");
}

@Test
void repRapVisitorEmitsPartTwoCommands() {
    String source = """
            M.title "RRF_Part2"
            BedMeshCalibrate
            LoadBedMesh
            LoadBedMesh "custom_mesh"
            ProbeCalibrate
            M.call "SubMacro"
            if extruder > 200
                Respond MSG "Hot enough"
            endif
            M.end
            """;

    var tree = TestUtils.parse(source);
    var visitor = new RepRapVisitor(new PrinterProfile());
    String output = visitor.visit(tree);

    assertTrue(output.contains("G29"), "BedMeshCalibrate: G29");
    assertTrue(output.contains("G29 S1"), "LoadBedMesh (no profile): G29 S1");
    assertFalse(output.contains("P\"<;missing STRING>\""), "LoadBedMesh without profile should not emit the parser placeholder");
    assertTrue(output.contains("G29 S1 P\"custom_mesh\""), "LoadBedMesh with profile: G29 S1 P\"custom_mesh\"");
    assertTrue(output.contains("G30"), "ProbeCalibrate: G30");
    assertTrue(output.contains("M98 P\"SubMacro\""), "MacroCall: M98 P\"SubMacro\"");
    assertTrue(output.contains("; Conditional skipped"), "IfStart: conditional comment");
    assertTrue(output.contains("; End conditional skipped"), "IfEnd: end conditional comment");
}

}