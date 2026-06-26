package maindeveloper.core;

public class PrinterSettings {
    private double nozzleDiameter = 0.4;      // mm
    private double filamentDiameter = 1.75;   // mm
    private double layerHeight = 0.2;         // mm
    private double extrusionMultiplier = 1.0;

    // Getters and setters
    public double getNozzleDiameter() { return nozzleDiameter; }
    public void setNozzleDiameter(double d) { this.nozzleDiameter = d; }

    public double getFilamentDiameter() { return filamentDiameter; }
    public void setFilamentDiameter(double d) { this.filamentDiameter = d; }

    public double getLayerHeight() { return layerHeight; }
    public void setLayerHeight(double h) { this.layerHeight = h; }

    public double getExtrusionMultiplier() { return extrusionMultiplier; }
    public void setExtrusionMultiplier(double m) { this.extrusionMultiplier = m; }

    // Typical extrusion width is 110% of nozzle diameter
    public double getLineWidth() {
        return nozzleDiameter * 1.1;
    }

    // Cross-sectional area of extruded line
    public double getLineCrossSection() {
        return getLineWidth() * layerHeight;
    }

    // Cross-sectional area of filament
    public double getFilamentCrossSection() {
        double r = filamentDiameter / 2.0;
        return Math.PI * r * r;
    }

    // Calculate required filament length for a given move distance
   public double calculateExtrusion(double distance) {
    double lineWidth = getLineWidth();
    double crossSection = lineWidth * layerHeight;
    double filamentArea = getFilamentCrossSection();
    double volume = distance * crossSection;
    double filamentLength = volume / filamentArea;
    return filamentLength * extrusionMultiplier;
}
}