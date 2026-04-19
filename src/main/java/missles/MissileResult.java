package missles;

import common.Position;

public record MissileResult(
        String missileId,
        String targetId,
        MissileOutcome hit,
        Position finalPosition
) {}

