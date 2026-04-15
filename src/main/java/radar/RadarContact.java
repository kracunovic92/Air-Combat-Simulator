package radar;

import common.Position;

public record RadarContact(
        String id,
        FlyingObjectType type,
        Position position,
        double distance
) {}
