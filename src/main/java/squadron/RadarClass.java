package squadron;

public enum RadarClass {
    STANDARD(2.0),
    LONG_RANGE(3.5);

    private final double range;

    RadarClass(double range) {
        this.range = range;
    }

    public double getRange() {
        return range;
    }
}
