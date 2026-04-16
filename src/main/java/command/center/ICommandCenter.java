package command.center;

import common.AircraftState;
import common.GridCell;
import common.Side;
import missles.MissleService;
import squadron.SquadronConnection;

import java.util.Collection;

public interface ICommandCenter {

    Side getSide();

    GridCell getBase();

    Collection<AircraftState> getFriendlyAircraft();

    Collection<AircraftState> getEnemyAircraft();

    void updateAircraft(AircraftState aircraftState);


    void registerSquadron(String squadronId, SquadronConnection connection);

    void unregisterSquadron(String squadronId);

    void sendCommand(String squadronId, String command);

    MissleService getMissleService();

    AircraftState findAircraftState(String aircraftId);


}
