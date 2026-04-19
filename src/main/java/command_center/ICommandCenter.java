package command_center;

import common.AircraftState;
import common.GridCell;
import common.Side;
import missles.MissileResult;
import missles.MissleService;
import squadron.SquadronConnection;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

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

    AircraftState findFriendlyState(String aircraftId);

    AircraftState findEnemyState(String aircraftId);

     Future<MissileResult> fireAtTarget(String targetId);
     List<Future<MissileResult>> fireAtNearestTargets();
}
