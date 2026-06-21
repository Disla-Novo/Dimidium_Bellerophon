package main.java.maindeveloper;
import jupitore.gen.JupitoreParser;
public class LayerHandler {
    private final GCodeVisitor visitor;
    private final PrinterSettings settings;

    public LayerHandler(GCodeVisitor visitor, PrinterSettings settings) {
        this.visitor = visitor;
        this.settings = settings;
    }

    public String handleLayer(int layers, JupitoreParser.Statement_blockContext block) {
        StringBuilder sb = new StringBuilder();
        double startZ = visitor.currentZ;
        for (int i = 0; i < layers; i++) {
            // Visit inner statements (they use the visitor's current position)
            sb.append(visitor.visit(block));
            // Raise Z for next layer (unless it's the last layer)
            if (i < layers - 1) {
                double nextZ = startZ + (i + 1) * settings.getLayerHeight();
                sb.append("G91\n");
                sb.append("G1 Z").append(settings.getLayerHeight()).append(" F300\n");
                sb.append("G90\n");
                visitor.currentZ = nextZ;
            }
        }
        return sb.toString();
    }
}