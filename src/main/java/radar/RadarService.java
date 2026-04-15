package radar;

import common.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RadarService  implements IRadarService{
    private final Map<String, TrackedFlyingObject> trackedObjects = new ConcurrentHashMap<>();


    @Override
    public List<RadarContact> reportAndScan(String id, FlyingObjectType type, Position position, double radarRange) {

        TrackedFlyingObject self = new TrackedFlyingObject(id, type, position, radarRange);
        trackedObjects.put(id, self);

        List<RadarContact> contacts = new ArrayList<>();

        for (TrackedFlyingObject other : trackedObjects.values()) {
            if (other.id().equals(id)) {
                continue;
            }

            double distance = euclideanDistance(position, other.position());

            if (distance <= radarRange) {
                contacts.add(new RadarContact(other.id(), other.type(), other.position(), distance));
            }
        }
        return contacts;
    }

    private double euclideanDistance(Position a, Position b) {
        double dx = a.x() - b.x();
        double dy = a.y() - b.y();
        return Math.sqrt(dx * dx + dy * dy);
    }



}
