// this program sets up a web server using the Spark framework to handle requests for compiling Jupitore code and highlighting syntax.
// it defines endpoints for compiling code and returning syntax highlights
// and uses the GCodeVisitor to generate G-code from Jupitore input. 
package maindeveloper;
import static spark.Spark.*;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import jupitore.gen.*;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class WebServer {

    // tested 
    static class TokenHighlight {
        public String text;
        public int start;
        public int end;
        public int type;
        public String name;
    }

    // Request & Response Classes 
    static class CompileRequest {
        public String code;
        // adding klipper or marlin mode!
        public String mode;
        // added the limiters
        public double limitX;
        public double limitY;
        public double limitZ;
        public PrinterProfile profile; // added printer profile for the visitor to use when setting up the hardware limiter and printer settings    
    }

    static class CompileResponse {
        public boolean success;
        public String output;
        public String error;
    }

    /** 
     * @param args
     */
    public static void main(String[] args) {

        ipAddress("127.0.0.1");   // added this to force it to use localhost. 
        port(4567); // Spark server port
        staticFiles.externalLocation("webpage"); // serve frontend

        Gson gson = new Gson();

        //  endpoint compiling yo
        post("/compile", (req, res) -> {
            CompileRequest input = gson.fromJson(req.body(), CompileRequest.class);
            CompileResponse out = new CompileResponse();

            try {
               // out.output = compileJupitore(input.code);
               out.output = compileJupitore(input);
                out.success = true;
            } catch (Exception e) {
                out.success = false;
                out.error = e.getMessage();
            }

            res.type("application/json");
            return gson.toJson(out);
        });

        // highlight endpoint
        post("/highlight", (req, res) -> {
            CompileRequest input = gson.fromJson(req.body(), CompileRequest.class);
            List<TokenHighlight> highlights = new ArrayList<>();

            CharStream charStream = CharStreams.fromString(input.code);
            JupitoreLexer lexer = new JupitoreLexer(charStream);
            lexer.removeErrorListeners(); // prevent console spam

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            tokens.fill();
            

            for (Token t : tokens.getTokens()) {
                if (t.getType() == Token.EOF) continue;

                TokenHighlight th = new TokenHighlight();
                th.text = t.getText();
                th.start = t.getStartIndex();
                th.end = t.getStopIndex();
                th.type = t.getType();
                th.name = JupitoreLexer.VOCABULARY.getSymbolicName(t.getType()); // exact symbolic name
                highlights.add(th);
            }

            res.type("application/json");
            return gson.toJson(highlights);
        });

    }

    /** 
     * @param code
     * @return String
     * @throws Exception
     */
    // ---- Compile Jupitore to G-code ---- YES
    // using Pages method to circumvent the data issue where it crashes if we have too many lines of output
    // i was recommended to do paging. we will use a hybrid approach between memory and .bin temp storage. this may help with the issue while giving us better performance. 
  private static String compileJupitore(CompileRequest input) throws Exception {

System.out.println("=== DEBUG: Compile Request ===");
    System.out.println("Mode: " + input.mode);
    System.out.println("Code length: " + (input.code != null ? input.code.length() : 0));
    System.out.println("Profile received: " + (input.profile != null ? "YES" : "NULL"));
    if (input.profile != null) {
        System.out.println("  maxX = " + input.profile.getMaxX());
        System.out.println("  maxY = " + input.profile.getMaxY());
        System.out.println("  maxZ = " + input.profile.getMaxZ());
        System.out.println("  nozzle = " + input.profile.getNozzleDiameter());
        System.out.println("  filament = " + input.profile.getFilamentDiameter());
    } else {
        System.out.println("  Profile is NULL - will use default");
    }



    System.gc(); // suggest garbage collection before we check memory, to get a more accurate reading

    // do not touch this
    CharStream charStream = CharStreams.fromString(input.code);
    JupitoreLexer lexer = new JupitoreLexer(charStream);
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    JupitoreParser parser = new JupitoreParser(tokens);
  


    // im doing it here!
    // i want to see how much space we have. Runtime()
    Runtime runtime = Runtime.getRuntime();
    long EmpRam = runtime.maxMemory() - runtime.totalMemory() - runtime.freeMemory();
      // added if its greater than 500kb or if we have less than 250mb of free memory. 
    boolean pagingUse = (input.code.length() > 500000) || (EmpRam < 250 * 1024 * 1024);
    // now here 4/10/2026, im going to tell the visitor to please use .bin if needed
    // Now we use the EXACT numbers from the user's sidebar

System.out.println("--- BELLEROPHON DEBUG ---");
System.out.println("Available RAM: " + (EmpRam / (1024 * 1024)) + "MB");
System.out.println("Paging Triggered: " + pagingUse);
System.out.println("-------------------------");
              // moved parse here
      ParseTree tree = parser.program();

    // Use provided profile or create a default one
    PrinterProfile profile = input.profile;
    if (profile == null) {
        profile = new PrinterProfile(); // default values
    }

    if ("marlin".equals(input.mode)) {
        MarlinVisitor visitor = new MarlinVisitor(profile);
        visitor.setEnablePaging(pagingUse);
        return visitor.visit(tree);
    } else {
        KlipperVisitor visitor = new KlipperVisitor(profile);
        visitor.setEnablePaging(pagingUse);
        return visitor.visit(tree);
    }
}
}
