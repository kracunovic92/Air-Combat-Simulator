package squadron.aircraft;

public enum RadarClass {
    STANDARD(2.0),
    LONG_RANGE(4.0),
    UNKNOWN(0);

    private final double range;

    RadarClass(double range) {
        this.range = range;
    }

    public double getRange() {
        return range;
    }
}
