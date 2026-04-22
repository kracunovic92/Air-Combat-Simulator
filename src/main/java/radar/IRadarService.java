package radar;

import common.Position;

import java.util.List;

public interface IRadarService {

    RadarScanResult reportAndScan(String id, FlyingObjectType type, Position position, double radarRange, String targetId );
    boolean removeTrackedObject(String id);
}
