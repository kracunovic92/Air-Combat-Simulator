package missles;

import common.Position;

public record MissileResult(
        String missileId,
        String targetId,
        boolean hit,
        Position finalPosition
) {}
