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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;

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
            "reprap", RepRapVisitor::new // added reprap to factory map
    );
    private static final String CONFIG_FILE = "config.properties";
    private static final int DEFAULT_PORT = 4567;
    private static final int STATE_SCHEMA_VERSION = 1;
    private static final int OUTPUT_PAGE_THRESHOLD_BYTES = 1_000_000;

    // Tracks the most recently paged output file so we can delete it the
    // next time we page a new one. Without this, temp files would pile up
    // in %TEMP% until the OS cleaned them out. Not volatile: pageToFile is
    // the only writer and it's synchronized on the class.
    private static File lastPagedFile = null;

    // Resolves the persisted state.json to a fixed OS user-data directory,
    // NOT relative to the jar/install folder. Each release is unzipped to a
    // fresh folder, so anything stored next to the jar gets silently
    // orphaned on every upgrade. Resolving from user.home instead means
    // state survives across releases. See issue: localStorage -> JSON
    // persistence layer.
    //
    // Windows: %APPDATA%\Dimidium\state.json
    // macOS: ~/Library/Application Support/Dimidium/state.json
    // Linux: $XDG_DATA_HOME/dimidium/state.json
    // (or ~/.local/share/dimidium/state.json if XDG_DATA_HOME is unset)
    private static Path getStateFilePath() {
        String userHome = System.getProperty("user.home");
        String os = System.getProperty("os.name", "").toLowerCase();

        Path dataDir;
        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            dataDir = Paths.get(appData != null ? appData : userHome, "Dimidium");
        } else if (os.contains("mac")) {
            dataDir = Paths.get(userHome, "Library", "Application Support", "Dimidium");
        } else {
            String xdgData = System.getenv("XDG_DATA_HOME");
            if (xdgData != null && !xdgData.isBlank()) {
                dataDir = Paths.get(xdgData, "dimidium");
            } else {
                dataDir = Paths.get(userHome, ".local", "share", "dimidium");
            }
        }

        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            System.out.println("WARNING: Failed to create state directory " + dataDir + ": " + e.getMessage());
        }

        return dataDir.resolve("state.json");
    }

    // Default, empty state shape matching the schema in the persistence
    // issue. Returned whenever state.json is missing, unreadable, or
    // malformed, so the frontend always has something safe to work with
    // instead of crashing on startup.
    private static JsonObject defaultState() {
        JsonObject root = new JsonObject();
        root.addProperty("version", STATE_SCHEMA_VERSION);
        root.addProperty("updatedAt", Instant.now().toString());

        JsonObject data = new JsonObject();
        data.add("editor", new JsonObject());
        data.add("profile", new JsonObject());
        data.add("references", new JsonObject());
        data.add("gcode", new JsonObject());
        data.add("gravity", new JsonObject());
        data.add("ui", new JsonObject());
        data.add("theme", new JsonObject());
        root.add("data", data);

        return root;
    }

    // Reads state.json, falling back to a fresh default state on any
    // failure (missing file, malformed JSON, wrong schema version for now -
    // migration logic slots in here later without changing the endpoint
    // shape).
    private static JsonObject readState(Gson gson) {
        Path path = getStateFilePath();

        if (!Files.exists(path)) {
            return defaultState();
        }

        try {
            String content = Files.readString(path);
            if (content.isBlank()) {
                return defaultState();
            }
            JsonObject state = gson.fromJson(content, JsonObject.class);
            if (state == null || !state.has("data")) {
                System.out.println("WARNING: state.json missing 'data' section, using default state");
                return defaultState();
            }
            return state;
        } catch (IOException | JsonSyntaxException e) {
            System.out.println("WARNING: Failed to read state.json (" + e.getMessage() + "), using default state");
            return defaultState();
        }
    }

    // Atomic write: write to a temp file in the same directory, then rename
    // over the real file. Rename is atomic on the same filesystem, so a
    // crash or power loss mid-write can never leave a half-written
    // state.json behind.
    //
    // ATOMIC_MOVE is not universally supported (network-redirected %APPDATA%
    // on domain-joined Windows machines, some antivirus filesystem filters).
    // On those systems we fall back to a plain REPLACE_EXISTING move, which
    // is still safe for our purpose because the temp file is on the same
    // directory - the worst case is a very brief window where the file is
    // mid-rename, not a half-written file.
    private static void writeState(Gson gson, JsonObject state) throws IOException {
        Path path = getStateFilePath();
        Path tempPath = path.resolveSibling(path.getFileName() + ".tmp");

        state.addProperty("updatedAt", Instant.now().toString());
        if (!state.has("version")) {
            state.addProperty("version", STATE_SCHEMA_VERSION);
        }

        Files.writeString(tempPath, gson.toJson(state));

        try {
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException e) {
            System.out.println("NOTE: Atomic move not supported on this filesystem, falling back to standard move");
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
        }
    }

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
            System.out
                    .println("server.port=" + portValue + " is not a valid number, using default port " + DEFAULT_PORT);
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

    static class CompileError {
        public int line;
        public String message;
        public String type;
    }

    static class CompileResponse {
        public boolean success;
        public String output;
        public String error;
        public List<CompileError> errors;
    }

    private static int inferLineNumberFromMessage(String message, String requestBody) {
        if (message == null || requestBody == null || requestBody.isBlank()) {
            return 1;
        }

        try {
            CompileRequest request = new Gson().fromJson(requestBody, CompileRequest.class);
            if (request != null && request.code != null && !request.code.isBlank()) {
                String[] lines = request.code.split("\\r?\\n");
                for (int i = 0; i < lines.length; i++) {
                    String line = lines[i].trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    if (message.toLowerCase().contains(line.toLowerCase())) {
                        return i + 1;
                    }
                }
            }
        } catch (Exception ignored) {
            // fall back to line 1
        }

        return 1;
    }

    static class ScanRequest {
        public String folderPath;
    }

    static class ScanResponse {
        public boolean success;
        public List<String> files;
        public String error;
    }

    private static synchronized String pageToFile(String gcode) throws IOException {
        if (lastPagedFile != null && lastPagedFile.exists()) {
            lastPagedFile.delete();
        }
        File tempFile = File.createTempFile("bph_scratch_", ".gcode");
        lastPagedFile = tempFile;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write(gcode);
        }
        return "SUCCESS_PAGED:" + tempFile.getAbsolutePath() + ":" + gcode.length();
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
                out.errors = new ArrayList<>();
                out.errors.add(new CompileError());
                out.errors.get(0).line = inferLineNumberFromMessage(message, req.body());
                out.errors.get(0).message = out.error;
                out.errors.get(0).type = "error";
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

        // /state endpoints: single versioned JSON persistence file, stored
        // in the OS user-data directory so it survives release upgrades.
        // State is treated as an opaque blob here - the backend doesn't
        // need to understand its contents, only read/write/reset it safely.
        get("/state", (req, res) -> {
            res.type("application/json");
            return gson.toJson(readState(gson));
        });

        post("/state", (req, res) -> {
            try {
                JsonObject incoming = gson.fromJson(req.body(), JsonObject.class);
                if (incoming == null || !incoming.has("data")) {
                    res.status(400);
                    JsonObject error = new JsonObject();
                    error.addProperty("success", false);
                    error.addProperty("error", "Request body must contain a 'data' section.");
                    return gson.toJson(error);
                }

                writeState(gson, incoming);

                res.type("application/json");
                JsonObject ok = new JsonObject();
                ok.addProperty("success", true);
                return gson.toJson(ok);
            } catch (JsonSyntaxException e) {
                res.status(400);
                JsonObject error = new JsonObject();
                error.addProperty("success", false);
                error.addProperty("error", "Malformed JSON: " + e.getMessage());
                return gson.toJson(error);
            } catch (IOException e) {
                res.status(500);
                JsonObject error = new JsonObject();
                error.addProperty("success", false);
                error.addProperty("error", "Failed to write state: " + e.getMessage());
                return gson.toJson(error);
            }
        });

        // Reset writes a fresh default state to disk, then returns a simple
        // {success: true} ack - matching the /state POST shape. The frontend
        // does its own empty-state reconstruction, so there's no reason to
        // serialize the whole default object back over the wire.
        post("/state/reset", (req, res) -> {
            try {
                JsonObject fresh = defaultState();
                writeState(gson, fresh);
                res.type("application/json");
                JsonObject ok = new JsonObject();
                ok.addProperty("success", true);
                return gson.toJson(ok);
            } catch (IOException e) {
                res.status(500);
                JsonObject error = new JsonObject();
                error.addProperty("success", false);
                error.addProperty("error", "Failed to reset state: " + e.getMessage());
                return gson.toJson(error);
            }
        });

        // /paged endpoint: returns the contents of a paged G-code temp file
        // so the download button can save the real output
        // . Restricted to the system temp directory
        // so a caller can't read arbitrary files off disk.
        get("/paged", (req, res) -> {
            String path = req.queryParams("path");
            if (path == null || path.isBlank()) {
                res.status(400);
                return "Missing path parameter";
            }

            File file = new File(path);
            if (!file.exists() || !file.isFile()) {
                res.status(404);
                return "Not found";
            }

            try {
                String canonicalFile = file.getCanonicalPath();
                String canonicalTemp = new File(System.getProperty("java.io.tmpdir")).getCanonicalPath();
                if (!canonicalFile.startsWith(canonicalTemp)) {
                    res.status(403);
                    return "Forbidden";
                }
            } catch (IOException e) {
                res.status(500);
                return "Cannot resolve path";
            }

            res.type("text/plain");
            return Files.readString(file.toPath());
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
     * @param input the compilation request containing code, mode, and printer
     *              profile
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

        // Standard ANTLR pipeline initialization: feeds raw string input into the
        // target lexer token stream.
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

        String result = visitor.visit(tree);

        if (!pagingUse && result.length() > OUTPUT_PAGE_THRESHOLD_BYTES) {
            System.out.println("Output is " + (result.length() / 1024) + " KB - paging to disk");
            return pageToFile(result);
        }

        return result;
    }
}