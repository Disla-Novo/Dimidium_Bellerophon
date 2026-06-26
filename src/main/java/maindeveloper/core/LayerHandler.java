package maindeveloper.core;

import jupitore.gen.JupitoreParser;

public class LayerHandler {

    private final GCodeVisitor visitor;
    private final PrinterSettings settings;

    public LayerHandler(GCodeVisitor visitor, PrinterSettings settings) {
        this.visitor = visitor;
        this.settings = settings;
    }

    public String handleLayer(int layers,
                              JupitoreParser.Statement_blockContext block) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < layers; i++) {


       boolean oldInsideLayer = visitor.insideLayer;
        
        try {
            visitor.insideLayer = true;
            sb.append(visitor.visit(block));
        } finally {
            visitor.insideLayer = oldInsideLayer;
        }

            // Raise Z unless this is the last layer
            if (i < layers - 1) {

                boolean wasRelative = visitor.relativeMode;

                sb.append("G91\n");
                sb.append("G1 Z")
                  .append(settings.getLayerHeight())
                  .append(" F300\n");

                // Restore user's positioning mode
                sb.append(wasRelative ? "G91\n" : "G90\n");

                // Keep visitor state synchronized
                visitor.currentZ += settings.getLayerHeight();
            }
        }

        return sb.toString();
    }
}