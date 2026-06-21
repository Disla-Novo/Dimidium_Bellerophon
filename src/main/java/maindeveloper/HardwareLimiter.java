package main.java.maindeveloper;

public class HardwareLimiter {
    private double x = 0, y = 0, z = 0;
    
    // These will now be set dynamically
    private double limitX;
    private double limitY;
    private double limitZ;
    
    private double globalX;
    private double globalY;
    private double globalZ;

    // The constructor now REQUIRES limits from the frontend/user!!!!!
    public HardwareLimiter(double maxX, double maxY, double maxZ) {

        System.out.println("=== DEBUG: HardwareLimiter constructor ===");
    System.out.println("Received maxX = " + maxX);
    System.out.println("Received maxY = " + maxY);
    System.out.println("Received maxZ = " + maxZ);
    
        this.globalX = maxX;
        this.globalY = maxY;
        this.globalZ = maxZ;
        resetToGlobal(); // Initializes limitX/Y/Z to the global values
    }

  public void checkAndMove(String axis, double newValue) {
    double limit = getLimitForAxis(axis);
    
    // took this away -----> Used Math.abs(newValue) to treat negative positions as positive magnitude
    if (newValue < 0 || newValue > limit) {
        throw new RuntimeException(axis + " position " + newValue + " out of bounds (0-" + limit + ")");
    
    }

    if (axis.equals("X")) x = newValue;
    else if (axis.equals("Y")) y = newValue;
    else if (axis.equals("Z")) z = newValue;
}

    private double getLimitForAxis(String axis) {
        return axis.equals("X") ? limitX : (axis.equals("Y") ? limitY : limitZ);
    }

    public void setTemporaryLimits(double tx, double ty, double tz) {
        this.limitX = tx;
        this.limitY = ty;
        this.limitZ = tz;
    }

    public void resetToGlobal() {

        this.limitX = globalX;
        this.limitY = globalY;
        this.limitZ = globalZ;
        System.out.println("DEBUG: resetToGlobal -> limits: X=" + limitX + " Y=" + limitY + " Z=" + limitZ);
    }
}