package radar;


import common.Position;

public record TrackedFlyingObject(
        String id,
        FlyingObjectType type,
        Position position,
        double radarRange
) {}
