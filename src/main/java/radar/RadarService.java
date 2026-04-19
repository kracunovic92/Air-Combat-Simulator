package radar;

import common.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RadarService implements IRadarService {

    private final Map<String, TrackedFlyingObject> trackedObjects = new ConcurrentHashMap<>();

    @Override
    public RadarScanResult reportAndScan(String id, FlyingObjectType type, Position position, double radarRange, String targetId) {

        TrackedFlyingObject currentObject = updateObject(id, type, position, radarRange);

        if (currentObject.destroyed()) {
            return new RadarScanResult(List.of(), true, false, null);
        }

        List<RadarContact> contacts = scanContacts(id, position, radarRange);
        HitResult hitResult = HitResult.noHit();

        if(type == FlyingObjectType.MISSILE){
            hitResult =  tryConfirmMissileHit(position, radarRange, targetId);

        }
        return new RadarScanResult(contacts, false, hitResult.hitConfirmed(), hitResult.hitTargetId());
    }

    private TrackedFlyingObject updateObject(String id, FlyingObjectType type, Position position, double radarRange ){
        return trackedObjects.compute(id, (key, existing) -> {
            if (existing == null) {
                return new TrackedFlyingObject(id, type, position, radarRange);
            }

            existing.update(position, radarRange);
            return existing;
        });
    }

    private List<RadarContact> scanContacts(String selfId, Position selfPosition, double radarRange) {
        List<RadarContact> contacts = new ArrayList<>();

        for (TrackedFlyingObject other : trackedObjects.values()) {
            if (shouldSkipFromScan(selfId, other)) {
                continue;
            }

            double distance = euclideanDistance(selfPosition, other.position());
            if (distance <= radarRange) {
                contacts.add(new RadarContact(
                        other.id(),
                        other.type(),
                        other.position(),
                        distance
                ));
            }
        }

        return contacts;
    }

    private boolean shouldSkipFromScan(String selfId, TrackedFlyingObject other) {
        return other.id().equals(selfId) || other.destroyed();
    }

    private HitResult tryConfirmMissileHit( Position missilePosition, double radarRange, String targetId) {
        System.out.println("Try to confirm " + missilePosition + "   " + targetId);

        TrackedFlyingObject target = trackedObjects.get(targetId);

        if (target == null) {
            return HitResult.noHit();
        }

        if (target.destroyed()) {
            return HitResult.noHit();
        }

        if (target.type() != FlyingObjectType.AIRCRAFT) {
            return HitResult.noHit();
        }

        double distance = euclideanDistance(missilePosition, target.position());

        if (distance <= radarRange) {
            System.out.println("Marking it  destroyed");
            target.markDestroyed();
            return HitResult.hit(target.id());
        }
        return HitResult.noHit();
    }

    private double euclideanDistance(Position a, Position b) {
        double dx = a.column() - b.column();
        double dy = a.row() - b.row();
        return Math.sqrt(dx * dx + dy * dy);
    }


}