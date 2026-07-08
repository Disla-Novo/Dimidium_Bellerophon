package maindeveloper;

import org.antlr.v4.runtime.*;
import jupitore.gen.*;

public class TestUtils {

    public static JupitoreParser.ProgramContext parse(String source) {

        CharStream input = CharStreams.fromString(source);

        JupitoreLexer lexer = new JupitoreLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        JupitoreParser parser = new JupitoreParser(tokens);

        return parser.program();
    }

    // like parse(), but fails on any syntax error instead of letting ANTLR's
    // default error recovery paper over it - use this when the point of the
    // test is that the input is valid syntax, not just that visiting the
    // (possibly mangled) tree doesn't throw
    public static JupitoreParser.ProgramContext parseStrict(String source) {

        CharStream input = CharStreams.fromString(source);

        JupitoreLexer lexer = new JupitoreLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        JupitoreParser parser = new JupitoreParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                    int line, int charPositionInLine, String msg, RecognitionException e) {
                throw new IllegalStateException(
                        "Syntax error at line " + line + ":" + charPositionInLine + " - " + msg);
            }
        });

        return parser.program();
    }

}