// this program is the heart of the compiler. 
// it traverses the parse tree and generates G-code based on the rules defined in the visitor methods.
// vs code comment generator used
package main.java.maindeveloper;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import jupitore.gen.*;

import java.nio.file.Files;
import java.nio.file.Paths;
 /* 
public class TestGCode {
   
    public static void main(String[] args) throws Exception {
        //String inputText = Files.readString(Paths.get("test.jup"));
      // System.out.println("Input text:\n" + inputText);
        
        CharStream input = CharStreams.fromString(inputText);
        JupitoreLexer lexer = new JupitoreLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // Print tokens for debugging
        tokens.fill();
        
       // System.out.println("\nTokens found:");
        for (Token token : tokens.getTokens()) {
            if (token.getType() != Token.EOF) {
            //    System.out.println(JupitoreLexer.VOCABULARY.getSymbolicName(token.getType()) + 
                       //         ": " + token.getText());
            }
        }
        
        JupitoreParser parser = new JupitoreParser(tokens);
        ParseTree tree = parser.program();
        
      // System.out.println("\nParse tree: " + tree.toStringTree(parser));
        
        GCodeVisitor visitor = new GCodeVisitor(0, 0, 0);
        String gcode = visitor.visit(tree);


         
      // System.out.println("\n--- Generated G-code ---");
        System.out.println(gcode);
    }
}
    */