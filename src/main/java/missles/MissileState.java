package missles;

import common.Position;
import common.Side;

public record MissileState(
        String id,
        Side side,
        Position position
) {}
