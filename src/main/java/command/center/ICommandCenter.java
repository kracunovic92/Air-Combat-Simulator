package command.center;

import common.AircraftState;
import common.GridCell;
import common.Side;
import squadron.SquadronConnection;

import java.util.Collection;

public interface ICommandCenter {

    Side getSide();

    GridCell getBase();

    Collection<AircraftState> getFriendlyAircraft();

    Collection<AircraftState> getEnemyAircraft();

    void updateAircraft(AircraftState aircraftState);

    void removeAircraft(String aircraftId);

    void registerSquadron(String squadronId, SquadronConnection connection);

    void unregisterSquadron(String squadronId);

    void sendCommand(String squadronId, String command);

    void printAirPicture();

    void returnAircraftToBase(String aircraftId);

    void assignPatrol(String aircraftId, String patrolCells);

    void fireAtTarget(String targetId);

    void fireAtNearestTargets();


}
