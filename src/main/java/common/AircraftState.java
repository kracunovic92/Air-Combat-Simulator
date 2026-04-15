package common;

import squadron.AircraftType;

public record AircraftState(
        String id,
        Side side,
        AircraftType type,
        Position position
) {}