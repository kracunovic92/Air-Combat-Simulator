package command.center;

import common.AircraftState;
import common.Side;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandCenter {

    private final Side side;
    private final Map<String, AircraftState> friendlyAircraft = new ConcurrentHashMap<>();
    private final Map<String, AircraftState> enemyAircraft = new ConcurrentHashMap<>();

    public CommandCenter(Side side){
        this.side = side;
    }

    public Collection<AircraftState> getFriendlyAircraft() {
        return friendlyAircraft.values();
    }

    public Collection<AircraftState> getEnemyAircraft() {
        return enemyAircraft.values();
    }
}
