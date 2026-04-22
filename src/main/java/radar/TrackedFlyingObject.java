package radar;


import common.Position;

public class TrackedFlyingObject {
    private final String id;
    private final FlyingObjectType type;
    private volatile Position position;
    private volatile double radarRange;
    private volatile boolean destroyed;

    public TrackedFlyingObject(String id, FlyingObjectType type, Position position, double radarRange) {
        this.id = id;
        this.type = type;
        this.position = position;
        this.radarRange = radarRange;
        this.destroyed = false;
    }

    public String id() { return id; }
    public FlyingObjectType type() { return type; }
    public Position position() { return position; }
    public double radarRange() { return radarRange; }
    public boolean destroyed() { return destroyed; }

    public synchronized void update(Position position, double radarRange) {
        this.position = position;
        this.radarRange = radarRange;
    }
    public synchronized void markDestroyed() {
        this.destroyed = true;
    }
    public synchronized boolean tryDestroyIfAircraftWithinRange(Position attackerPosition, double range) {
        if (destroyed) {
            return false;
        }

        if (type != FlyingObjectType.AIRCRAFT) {
            return false;
        }

        double dx = attackerPosition.column() - position.column();
        double dy = attackerPosition.row() - position.row();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance > range) {
            return false;
        }

        destroyed = true;
        return true;
    }
}