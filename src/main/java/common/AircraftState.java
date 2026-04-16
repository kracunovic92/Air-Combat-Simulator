package common;

import squadron.aircraft.AircraftType;

public record AircraftState(
        String id,
        String squadron_id,
        Side side,
        AircraftType type,
        Position position
) {}