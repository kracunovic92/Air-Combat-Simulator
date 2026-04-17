package command.center.CLI;

import command.center.ICommandCenter;
import common.AircraftState;
import common.GridCell;


public class CommandCenterConsoleService implements ICommandCenterConsole {

    private static final int GRID_SIZE = 8;

    private final ICommandCenter commandCenter;
    private final GridPrinter gridPrinter = new GridPrinter();

    public CommandCenterConsoleService(ICommandCenter commandCenter) {
        this.commandCenter = commandCenter;
    }

    @Override
    public void printAirPicture() {

        gridPrinter.print(commandCenter.getSide(), commandCenter.getBase(), commandCenter.getFriendlyAircraft(), commandCenter.getEnemyAircraft());

        printDetailedLists();
    }

    @Override
    public void returnAircraftToBase(String aircraftId) {
        AircraftState state = commandCenter.findAircraftState(aircraftId);
        if (state == null) {
            System.out.println("Aircraft not found: " + aircraftId);
            return;
        }

        commandCenter.sendCommand(state.squadron_id(), "RETURN_TO_BASE;" + aircraftId);
    }

    @Override
    public void assignPatrol(String aircraftId, String patrolCells) {
        AircraftState state = commandCenter.findAircraftState(aircraftId);
        if (state == null) {
            System.out.println("Aircraft not found: " + aircraftId);
            return;
        }

        commandCenter.sendCommand(state.squadron_id(), "PATROL;" + aircraftId + ";" + patrolCells);
    }

    @Override
    public void fireAtTarget(String targetId) {
        commandCenter.fireAtTarget(targetId);
    }

    @Override
    public void fireAtNearestTargets() {

        commandCenter.fireAtNearestTargets();
    }


    private void printDetailedLists() {
        System.out.println("Friendly aircraft:");
        for (AircraftState aircraft : commandCenter.getFriendlyAircraft()) {
            GridCell cell = toGridCell(aircraft);
            System.out.println("  "
                    + Colors.color(aircraft.id(), Colors.GREEN)
                    + " " + aircraft.type()
                    + " @ " + aircraft.position()
                    + " [" + cell.label() + "]");
        }

        System.out.println("Enemy aircraft:");
        for (AircraftState aircraft : commandCenter.getEnemyAircraft()) {
            GridCell cell = toGridCell(aircraft);
            System.out.println("  "
                    + Colors.color(aircraft.id(), Colors.RED)
                    + " " + aircraft.type()
                    + " @ " + aircraft.position()
                    + " [" + cell.label() + "]");
        }
    }

    private GridCell toGridCell(AircraftState aircraft) {
        int x = ConsoleHelper.clampToGrid((int) Math.floor(aircraft.position().column()), GRID_SIZE);
        int y = ConsoleHelper.clampToGrid((int) Math.floor(aircraft.position().row()), GRID_SIZE);

        return new GridCell(x, y);
    }
}