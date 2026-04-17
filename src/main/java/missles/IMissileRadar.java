package missles;

import common.Position;
import radar.RadarContact;

import java.util.List;

public interface IMissileRadar {

    List<RadarContact> scan(Position position, double range);
    void reportHit(String targetId, String missileId);
    void reportMiss(String missileId, String targetId);
}
