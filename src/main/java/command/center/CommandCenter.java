package command.center;

import common.AircraftState;
import common.GridCell;
import common.Side;
import missles.MissleService;
import squadron.SquadronConnection;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandCenter implements ICommandCenter {

    private final Side side;
    private final GridCell base;
    private final MissleService missleService;
    private final Map<String, AircraftState> friendlyAircraft = new ConcurrentHashMap<>();
    private final Map<String, AircraftState> enemyAircraft = new ConcurrentHashMap<>();
    private final Map<String, List<String>> radarContactsByAircraft = new ConcurrentHashMap<>();
    private final Map<String, SquadronConnection> squadronConnections = new ConcurrentHashMap<>();


    public CommandCenter(Side side, GridCell base){

        this.side = side;
        this.base = base;
        this.missleService = new MissleService();
    }

    @Override
    public Side getSide() {
        return  side;
    }

    @Override
    public GridCell getBase() {
        return base;
    }

    @Override
    public Collection<AircraftState> getFriendlyAircraft() {
        return friendlyAircraft.values();
    }

    @Override
    public Collection<AircraftState> getEnemyAircraft() {
        return enemyAircraft.values();
    }

    @Override
    public void updateAircraft(AircraftState aircraftState) {
        if (aircraftState.side() == side) {
            friendlyAircraft.put(aircraftState.id(), aircraftState);
        } else {
            enemyAircraft.put(aircraftState.id(), aircraftState);
        }

    }

    @Override
    public void removeAircraft(String aircraftId) {

        if (aircraftId == null || aircraftId.isBlank()) {
            throw new IllegalArgumentException("Id is not null");
        }

        friendlyAircraft.remove(aircraftId);
        enemyAircraft.remove(aircraftId);

    }
    @Override
    public void registerSquadron(String squadronId, SquadronConnection connection) {
        if (squadronConnections.size() >= 2 && !squadronConnections.containsKey(squadronId)) {
            throw new IllegalStateException("Command center can handle at most 2 squadrons");
        }

        squadronConnections.put(squadronId, connection);
    }

    @Override
    public void unregisterSquadron(String squadronId) {
        squadronConnections.remove(squadronId);
    }

    @Override
    public void sendCommand(String squadronId, String command) {
        SquadronConnection connection = squadronConnections.get(squadronId);
        if (connection == null) {
            System.out.println("No connected squadron: " + squadronId);
            return;
        }

        connection.send(command);
    }

    @Override
    public void printAirPicture() {
        final String RESET = "\u001B[0m";
        final String GREEN = "\u001B[32m";
        final String RED = "\u001B[31m";
        final String CYAN = "\u001B[36m";
        final String YELLOW = "\u001B[33m";

        System.out.println(CYAN + "=== " + side + " COMMAND CENTER ===" + RESET);
        System.out.println(YELLOW + "Base: " + base + RESET);

        System.out.println(GREEN + "--- Friendly aircraft ---" + RESET);
        for (AircraftState a : friendlyAircraft.values()) {
            System.out.println(GREEN + a.id() + " " + a.type() + " @ " + a.position() + RESET);
        }

        System.out.println(RED + "--- Enemy aircraft ---" + RESET);
        for (AircraftState a : enemyAircraft.values()) {
            System.out.println(RED + a.id() + " " + a.type() + " @ " + a.position() + RESET);
        }
    }
    @Override
    public void returnAircraftToBase(String aircraftId) {
        String squadronId = inferSquadronId(aircraftId);
        if (squadronId == null) {
            System.out.println("Cannot determine squadron for aircraft: " + aircraftId);
            return;
        }

        sendCommand(squadronId, "RETURN_TO_BASE;" + aircraftId);
    }
    @Override
    public void assignPatrol(String aircraftId, String patrolCells) {
        String squadronId = inferSquadronId(aircraftId);
        if (squadronId == null) {
            System.out.println("Cannot determine squadron for aircraft: " + aircraftId);
            return;
        }

        sendCommand(squadronId, "PATROL;" + aircraftId + ";" + patrolCells);
    }

    @Override
    public void fireAtTarget(String targetId) {
        missleService.fireAtTarget(targetId);
    }

    @Override
    public void fireAtNearestTargets() {
        System.out.println("TODO: fire available missiles at nearest enemy targets");
    }

    private String inferSquadronId(String aircraftId) {
        if (aircraftId == null || aircraftId.length() < 2) {
            return null;
        }
        return aircraftId.substring(0, 2);

    }

    public void updateRadarContacts(String aircraftId, List<String> contacts) {
        radarContactsByAircraft.put(aircraftId, contacts);
    }

    public Map<String, List<String>> getRadarContactsByAircraft() {
        return radarContactsByAircraft;
    }
}
