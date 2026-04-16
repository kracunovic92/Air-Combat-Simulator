package command.center.CLI;

import command.center.ICommandCenter;
import common.AircraftState;
import common.GridCell;

import java.util.ArrayList;
import java.util.List;


public class CommandCenterConsoleService implements ICommandCenterConsole {

    private static final int GRID_SIZE = 8;
    private static final int CELL_WIDTH = 10;

    private final ICommandCenter commandCenter;

    public CommandCenterConsoleService(ICommandCenter commandCenter) {
        this.commandCenter = commandCenter;
    }

    @Override
    public void printAirPicture() {
        List<String>[][] grid = createEmptyGrid();

        placeBase(grid, commandCenter.getBase());

        for (AircraftState aircraft : commandCenter.getFriendlyAircraft()) {
            placeAircraft(grid, aircraft, true);
        }

        for (AircraftState aircraft : commandCenter.getEnemyAircraft()) {
            placeAircraft(grid, aircraft, false);
        }

        System.out.println();
        System.out.println("=== " + commandCenter.getSide() + " COMMAND CENTER TACTICAL MAP ===");
        System.out.println("Legend: BA=Base, green=friendly, red=enemy");
        System.out.println();

        printColumnHeaders();
        printSeparator();

        for (int y = GRID_SIZE - 1; y >= 0; y--) {
            System.out.print(padRight((y + 1) + " ", 6));
            for (int x = 0; x < GRID_SIZE; x++) {
                System.out.print("|" + formatCell(grid[y][x], CELL_WIDTH));
            }
            System.out.println("|");
            printSeparator();
        }

        System.out.println();
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
        commandCenter.getMissleService().fireAtTarget(targetId);
    }

    @Override
    public void fireAtNearestTargets() {
        commandCenter.getMissleService().fireAtNearestTargets();
    }

    private List<String>[][] createEmptyGrid() {
        List<String>[][] grid = new ArrayList[GRID_SIZE][GRID_SIZE];

        for (int y = 0; y < GRID_SIZE; y++) {
            for (int x = 0; x < GRID_SIZE; x++) {
                grid[y][x] = new ArrayList<>();
            }
        }

        return grid;
    }

    private void placeBase(List<String>[][] grid, GridCell base) {
        int x = clampToGrid(base.column());
        int y = clampToGrid(base.row());
        grid[y][x].add(Colors.color("BA", Colors.CYAN));
    }

    private void placeAircraft(List<String>[][] grid, AircraftState aircraft, boolean friendly) {
        double xPos = aircraft.position().column();
        double yPos = aircraft.position().row();

        int x = clampToGrid((int) Math.floor(xPos));
        int y = clampToGrid((int) Math.floor(yPos));

        String label = Colors.color(aircraft.id(), friendly ? Colors.GREEN : Colors.RED);
        grid[y][x].add(label);
    }

    private int clampToGrid(int value) {
        return Math.max(0, Math.min(value, GRID_SIZE - 1));
    }

    private void printColumnHeaders() {
        System.out.print(padRight("", 6));
        for (int x = 0; x < GRID_SIZE; x++) {
            char colLabel = (char) ('A' + x);
            System.out.print("|" + center(String.valueOf(colLabel), CELL_WIDTH));
        }
        System.out.println("|");
    }

    private void printSeparator() {
        System.out.print("------");
        for (int x = 0; x < GRID_SIZE; x++) {
            System.out.print("+" + "-".repeat(CELL_WIDTH));
        }
        System.out.println("+");
    }

    private String formatCell(List<String> cell, int width) {
        String text = String.join(",", cell);
        return padAnsi(text, width);
    }

    private String padRight(String text, int width) {
        if (text == null) {
            text = "";
        }

        int visible = visibleLength(text);
        if (visible >= width) {
            return truncateAnsi(text, width);
        }

        return text + " ".repeat(width - visible);
    }

    private String center(String text, int width) {
        if (text == null) {
            text = "";
        }

        int visible = visibleLength(text);
        if (visible >= width) {
            return truncateAnsi(text, width);
        }

        int totalPadding = width - visible;
        int left = totalPadding / 2;
        int right = totalPadding - left;

        return " ".repeat(left) + text + " ".repeat(right);
    }

    private String padAnsi(String text, int width) {
        if (text == null) {
            text = "";
        }

        int visible = visibleLength(text);
        if (visible > width) {
            return truncateAnsi(text, width - 1) + "~";
        }

        return text + " ".repeat(width - visible);
    }

    private int visibleLength(String text) {
        return stripAnsi(text).length();
    }

    private String stripAnsi(String text) {
        return text.replaceAll("\\u001B\\[[;\\d]*m", "");
    }

    private String truncateAnsi(String text, int maxVisibleChars) {
        StringBuilder result = new StringBuilder();
        int visibleCount = 0;

        for (int i = 0; i < text.length();) {
            char ch = text.charAt(i);

            if (ch == '\u001B') {
                int end = i + 1;
                while (end < text.length() && text.charAt(end) != 'm') {
                    end++;
                }
                if (end < text.length()) {
                    end++;
                }
                result.append(text, i, end);
                i = end;
            } else {
                if (visibleCount >= maxVisibleChars) {
                    break;
                }
                result.append(ch);
                visibleCount++;
                i++;
            }
        }

        if (!result.toString().endsWith(Colors.RESET)) {
            result.append(Colors.RESET);
        }

        return result.toString();
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
        int x = clampToGrid((int) Math.floor(aircraft.position().column()));
        int y = clampToGrid((int) Math.floor(aircraft.position().row()));
        return new GridCell(x, y);
    }
}