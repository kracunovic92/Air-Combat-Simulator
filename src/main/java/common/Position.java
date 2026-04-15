package common;

public class Position  {

    private double x;
    private double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public static Position of(double x, double y) {
        return new Position(x, y);
    }


    public double x() {
        return x;
    }

    public double y() {
        return y;
    }

    public Position move(double dx, double dy) {
        return new Position(x + dx, y + dy);
    }
}
