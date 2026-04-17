package common;

public record Position(double column, double row)  {

    private static final double MIN = 0.0;
    private static final double MAX = 7.9;


    public Position {
        validate(column, row);
    }

    public static Position of(double x, double y) {
        return new Position(x, y);
    }

    private static void validate(double x, double y) {
        if (x < MIN || x > MAX) {
            throw new IllegalArgumentException("Fix X range");
        }
        if (y < MIN || y > MAX) {
            throw new IllegalArgumentException("Fix y range");
        }
    }

    public Position move(double dx, double dy) {
        return Position.clamped(column + dx, row + dy);
    }
    public static Position clamped(double x, double y) {
        return new Position(clamp(x), clamp(y));
    }

    private static double clamp(double value) {
        return Math.max(MIN, Math.min(MAX, value));
    }
}
