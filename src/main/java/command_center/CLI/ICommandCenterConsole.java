package command_center.CLI;

public interface ICommandCenterConsole {

    void printAirPicture();

    void returnAircraftToBase(String aircraftId);

    void assignPatrol(String aircraftId, String patrolCells);

    void fireAtTarget(String targetId);

    void fireAtNearestTargets();

}
