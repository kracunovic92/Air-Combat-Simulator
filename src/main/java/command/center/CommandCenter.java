package command.center;

import common.AircraftState;
import common.GridCell;
import common.Side;
import missles.MissleService;
import radar.FlyingObjectType;
import radar.RadarContact;
import squadron.SquadronConnection;
import squadron.aircraft.AircraftType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CommandCenter implements ICommandCenter {

    private final Side side;
    private final GridCell base;
    private final MissleService missleService;
    private final Map<String, AircraftState> friendlyAircraft = new ConcurrentHashMap<>();
    private final Map<String, AircraftState> enemyAircraft = new ConcurrentHashMap<>();
    private final Map<String, List<RadarContact>> radarContactsByAircraft = new ConcurrentHashMap<>();
    private final Map<String, SquadronConnection> squadronConnections = new ConcurrentHashMap<>();


    public CommandCenter(Side side, GridCell base){
        this.side = side;
        this.base = base;
        this.missleService = new MissleService();
    }

    public MissleService getMissleService() {
        return missleService;
    }

    @Override
    public AircraftState findAircraftState(String aircraftId) {

        return getFriendlyAircraft()
                .stream().
                filter(a -> a.id().equals(aircraftId))
                .findFirst().orElse(null);

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
        }
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

    public void updateRadarContacts(String aircraftId, List<RadarContact> contacts) {

        updateEnemyWatchByAircraft(aircraftId, contacts);

        radarContactsByAircraft.put(aircraftId, contacts);

        for (RadarContact contact : contacts) {
            if (contact.type() != FlyingObjectType.AIRCRAFT) {
                continue;
            }
            if (friendlyAircraft.containsKey(contact.id())) {
                AircraftState existing = friendlyAircraft.get(contact.id());
                AircraftState updated = new AircraftState(contact.id(), existing.squadron_id(),existing.side(), existing.type(), existing.position());
                friendlyAircraft.put(contact.id(),updated);
                continue;
            }
            if(enemyAircraft.containsKey(contact.id())){
                AircraftState existing = enemyAircraft.get(contact.id());
                AircraftState updated = new AircraftState(contact.id(), existing.squadron_id(),existing.side(), existing.type(), existing.position());
                enemyAircraft.put(contact.id(),updated);

            }else{

                AircraftState enemy = new AircraftState(contact.id(),"enemy",oposideSide(),AircraftType.UNKNOWN,contact.position());
                enemyAircraft.put(contact.id(), enemy);
            }
        }

    }
    private void updateEnemyWatchByAircraft(String aircraftId, List<RadarContact> newContacts ){

        List<RadarContact> lastList = radarContactsByAircraft.getOrDefault(aircraftId, new ArrayList<>());

        if(lastList.isEmpty()){
            return;
        }

        for(RadarContact contact : lastList){

            boolean exist = false;
            String enemyId = contact.id();

            for(RadarContact newContact : newContacts){

                String newId = newContact.id();

                if(Objects.equals(newContact.id(), contact.id()) && enemyAircraft.containsKey(newId)){
                    exist = true;
                }
            }
            if(!exist){
                enemyAircraft.remove(enemyId);
            }
        }
    }
    private Side oposideSide(){
        if(side == Side.BLUE){
            return Side.RED;
        }
        return Side.BLUE;
    }

}
