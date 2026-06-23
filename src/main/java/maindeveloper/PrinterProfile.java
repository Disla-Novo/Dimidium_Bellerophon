package maindeveloper;

public class PrinterProfile {
    private String name = "Default";
    private double maxX = 220;
    private double maxY = 220;
    private double maxZ = 250;
    private double nozzleDiameter = 0.4;
    private double filamentDiameter = 1.75;
    private double layerHeight = 0.2;
    private double extrusionMultiplier = 1.0;

    // Default constructor
    public PrinterProfile() {}

    // Constructor with all fields
    public PrinterProfile(String name, double maxX, double maxY, double maxZ,
                          double nozzleDiameter, double filamentDiameter,
                          double layerHeight, double extrusionMultiplier) {
        this.name = name;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.nozzleDiameter = nozzleDiameter;
        this.filamentDiameter = filamentDiameter;
        this.layerHeight = layerHeight;
        this.extrusionMultiplier = extrusionMultiplier;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getMaxX() { return maxX; }
    public void setMaxX(double maxX) { this.maxX = maxX; }

    public double getMaxY() { return maxY; }
    public void setMaxY(double maxY) { this.maxY = maxY; }

    public double getMaxZ() { return maxZ; }
    public void setMaxZ(double maxZ) { this.maxZ = maxZ; }

    public double getNozzleDiameter() { return nozzleDiameter; }
    public void setNozzleDiameter(double nozzleDiameter) { this.nozzleDiameter = nozzleDiameter; }

    public double getFilamentDiameter() { return filamentDiameter; }
    public void setFilamentDiameter(double filamentDiameter) { this.filamentDiameter = filamentDiameter; }

    public double getLayerHeight() { return layerHeight; }
    public void setLayerHeight(double layerHeight) { this.layerHeight = layerHeight; }

    public double getExtrusionMultiplier() { return extrusionMultiplier; }
    public void setExtrusionMultiplier(double extrusionMultiplier) { this.extrusionMultiplier = extrusionMultiplier; }
}