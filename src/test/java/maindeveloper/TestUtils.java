package maindeveloper;

import org.antlr.v4.runtime.*;
import jupitore.gen.*;

public class TestUtils {

    public static JupitoreParser.ProgramContext parse(String source) {

        CharStream input = CharStreams.fromString(source);

        JupitoreLexer lexer =
            new JupitoreLexer(input);

        CommonTokenStream tokens =
            new CommonTokenStream(lexer);

        JupitoreParser parser =
            new JupitoreParser(tokens);

        return parser.program();
    }

}