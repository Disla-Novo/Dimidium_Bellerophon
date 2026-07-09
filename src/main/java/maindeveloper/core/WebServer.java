// this program sets up a web server using the Spark framework to handle requests for compiling Jupitore code and highlighting syntax.
// it defines endpoints for compiling code and returning syntax highlights
// and uses the GCodeVisitor to generate G-code from Jupitore input. 
package maindeveloper.core;

import static spark.Spark.*;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.Properties;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.google.gson.Gson;

import jupitore.gen.*;
import maindeveloper.dialects.KlipperVisitor;
import maindeveloper.dialects.MarlinVisitor;
import maindeveloper.dialects.RepRapVisitor;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class WebServer {

    // maps a firmware mode string to a visitor constructor - add new firmware
    // targets here instead of growing an if-else chain
    private static final Map<String, Function<PrinterProfile, GCodeVisitor>> VISITOR_FACTORIES = Map.of(
            "klipper", KlipperVisitor::new,
            "marlin", MarlinVisitor::new,
            "reprap", RepRapVisitor::new  // added reprap to factory map
    );
    private static final String CONFIG_FILE = "config.properties";
    private static final int DEFAULT_PORT = 4567;

    // reads server.port from config.properties next to the jar; falls back to
    // DEFAULT_PORT if the file is missing, unreadable, or the value isn't a
    // usable port number
    private static int loadServerPort() {
        Properties config = new Properties();

        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            config.load(in);
        } catch (IOException e) {
            System.out.println(CONFIG_FILE + " not found or unreadable, using default port " + DEFAULT_PORT);
            return DEFAULT_PORT;
        }

        String portValue = config.getProperty("server.port");
        if (portValue == null || portValue.isBlank()) {
            return DEFAULT_PORT;
        }

        try {
            int port = Integer.parseInt(portValue.trim());
            if (port < 1 || port > 65535) {
                System.out.println("server.port=" + portValue + " is out of range, using default port " + DEFAULT_PORT);
                return DEFAULT_PORT;
            }
            return port;
        } catch (NumberFormatException e) {
            System.out.println("server.port=" + portValue + " is not a valid number, using default port " + DEFAULT_PORT);
            return DEFAULT_PORT;
        }
    }

    // tested
    static class TokenHighlight {
        public String text;
        public int start;
        public int end;
        public int type;
        public String name;
    }

    // Request/Response DTOs for compilation and syntax highlighting
    static class CompileRequest {
        public String code;
        // adding klipper or marlin mode!
        public String mode;
        // added the limiters
        public double limitX;
        public double limitY;
        public double limitZ;
        public PrinterProfile profile; // added printer profile for the visitor to use when setting up the hardware
                                       // limiter and printer settings
        public String gcodeFolder; // insertGcode folder - the G-code library folder path from frontend
    }

    static class CompileResponse {
        public boolean success;
        public String output;
        public String error;
    }

    static class ScanRequest {
        public String folderPath;
    }

    static class ScanResponse {
        public boolean success;
        public List<String> files;
        public String error;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {

        ipAddress("127.0.0.1"); // Bind to localhost only
        port(loadServerPort()); // Spark server port
        staticFiles.externalLocation("webpage"); // serve frontend

        // make sure Jetty actually stops (releases the port/socket) instead of
        // relying on the JVM dying on its own - on Windows a closed console
        // window can leave the process running with the port/files still locked
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stop();
            awaitStop();
        }));

        Gson gson = new Gson();

        // /compile endpoint: parses and compiles Bellerophon code
        post("/compile", (req, res) -> {
            CompileRequest input = gson.fromJson(req.body(), CompileRequest.class);
            CompileResponse out = new CompileResponse();

            try {
                // out.output = compileJupitore(input.code);
                out.output = compileJupitore(input);
                out.success = true;
            } catch (Exception e) {
                out.success = false;
                String message = e.getMessage();
                String hint = "";

                // helpful hints for common errors
                if (message != null) {
                    if (message.contains("out of bounds")) {
                        hint = " → Check your PrinterProfile boundary values.";
                    } else if (message.contains("iterator") && message.contains("only")) {
                        hint = " → The 'i' variable only works inside Brepeat loops.";
                    } else if (message.contains("Unknown function")) {
                        hint = " → Check function name (case-insensitive). Supported: sqrt, sin, cos, tan, and basic arithmetic.";
                    } else if (message.contains("Memory Paging Failed")) {
                        hint = " → Free up disk space or reduce repeat counts/macro complexity.";
                    }
                }

                out.error = message + hint;
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
                if (t.getType() == Token.EOF)
                    continue;

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

        // scan folder endpoint
        post("/scan-folder", (req, res) -> {
            ScanRequest request = gson.fromJson(req.body(), ScanRequest.class);
            ScanResponse response = new ScanResponse();
            response.files = new ArrayList<>();

            if (request.folderPath != null && !request.folderPath.isEmpty()) {
                File folder = new File(request.folderPath);
                if (folder.exists() && folder.isDirectory()) {
                    File[] gcodeFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".gcode") ||
                            name.toLowerCase().endsWith(".g") ||
                            name.toLowerCase().endsWith(".gc"));
                    if (gcodeFiles != null) {
                        for (File f : gcodeFiles) {
                            response.files.add(f.getName());
                        }
                        response.success = true;
                    } else {
                        response.success = true;
                    }
                } else {
                    response.success = false;
                    response.error = "Folder does not exist: " + request.folderPath;
                }
            } else {
                response.success = false;
                response.error = "No folder path provided";
            }

            res.type("application/json");
            return gson.toJson(response);
        });

    }

   /**
 * @param input the compilation request containing code, mode, and printer profile
 * @return String G-code output
 * @throws Exception if compilation fails
 */
    // ---- Compile Jupitore to G-code ---- YES
    // using Pages method to circumvent the data issue where it crashes if we have
    // too many lines of output
    // i was recommended to do paging. we will use a hybrid approach between memory
    // and .bin temp storage. this may help with the issue while giving us better
    // performance.
    private static String compileJupitore(CompileRequest input) throws Exception {

        // added null check to prevent NPE when input.code is missing
        if (input == null || input.code == null) {
            throw new IllegalArgumentException("Compilation request must contain code.");
        }

        System.out.println("=== DEBUG: Compile Request ===");
        System.out.println("Mode: " + input.mode);
        System.out.println("Code length: " + input.code.length());
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

        System.gc(); // suggest garbage collection before we check memory, to get a more accurate
                     // reading

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

        String mode = input.mode == null ? "" : input.mode.toLowerCase();
        Function<PrinterProfile, GCodeVisitor> factory = VISITOR_FACTORIES.get(mode);
        if (factory == null) {
            String requested = (input.mode == null || input.mode.isBlank())
                    ? "(none specified)"
                    : "'" + input.mode + "'";
            String supported = String.join(", ", VISITOR_FACTORIES.keySet());
            throw new IllegalArgumentException(
                    "Unsupported firmware mode: " + requested + ". Supported modes are: " + supported);
        }
        GCodeVisitor visitor = factory.apply(profile);

        visitor.setEnablePaging(pagingUse);
        if (input.gcodeFolder != null && !input.gcodeFolder.isEmpty()) {
            visitor.setSourceFilePath(input.gcodeFolder);
            System.out.println("G-code folder set to: " + input.gcodeFolder);
        }
        return visitor.visit(tree);
    }
}