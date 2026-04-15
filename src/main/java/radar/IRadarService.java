package radar;

import common.Position;

import java.util.List;

public interface IRadarService {

    List<RadarContact> reportAndScan(String id, FlyingObjectType type, Position position, double radarRange );
}
