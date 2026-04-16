package squadron.aircraft;

public enum RadarClass {
    STANDARD(2.0),
    LONG_RANGE(7.0); //TODO: Revert this after testing

    private final double range;

    RadarClass(double range) {
        this.range = range;
    }

    public double getRange() {
        return range;
    }
}
