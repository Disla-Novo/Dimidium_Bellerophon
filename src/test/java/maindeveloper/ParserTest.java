package maindeveloper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

public class ParserTest {

    @Test
    void parsesSimpleMacro() {
        String source = """
            M.title "test"
            Home
            M.end
            """;

        assertDoesNotThrow(() -> TestUtils.parse(source));
    }

    @Test
    void parsesLayerStatement() {
        String source = """
            M.title "layer"
            Layer 3
                MoveTo x=100 y=50
            end
            M.end
            """;

        assertDoesNotThrow(() -> TestUtils.parse(source));
    }

    @Test
    void parsesRepeatStatement() {
        String source = """
            M.title "repeat"
            repeat 4
                MoveTo x=20
            end
            M.end
            """;

        assertDoesNotThrow(() -> TestUtils.parse(source));
    }

    @Test
    void parsesVariablesAndExpressions() {
        String source = """
            M.title "vars"
            temp = 200
            Heat extruder = temp + 10
            M.end
            """;

        assertDoesNotThrow(() -> TestUtils.parse(source));
    }
}