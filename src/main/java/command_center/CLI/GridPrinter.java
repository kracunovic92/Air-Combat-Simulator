package command_center.CLI;

import common.AircraftState;
import common.GridCell;
import common.Side;
import missles.MissileState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GridPrinter {

    private static final int GRID_SIZE = 8;
    private static final int CELL_WIDTH = 10;

    public void print(Side side, GridCell base, Collection<AircraftState> friendlyAircraft, Collection<AircraftState> enemyAircraft, Collection<MissileState> activeMissiles) {

        List<String>[][] grid = createEmptyGrid();

        placeBase(grid, base);

        for (AircraftState aircraft : friendlyAircraft) {
            placeAircraft(grid, aircraft, true);
        }

        for (AircraftState aircraft : enemyAircraft) {
            placeAircraft(grid, aircraft, false);
        }

        for (MissileState missile : activeMissiles) {
            placeMissile(grid, missile);
        }
        System.out.println();
        System.out.println("=== " + side + " COMMAND CENTER TACTICAL MAP ===");
        System.out.println("Legend: BA=Base, green=friendly, red=enemy");
        System.out.println();

        printColumnHeaders();
        printSeparator();

        for (int y = GRID_SIZE - 1; y >= 0; y--) {
            System.out.print(ConsoleHelper.padRight((y + 1) + " ", 6));
            for (int x = 0; x < GRID_SIZE; x++) {
                System.out.print("|" + formatCell(grid[y][x], CELL_WIDTH));
            }
            System.out.println("|");
            printSeparator();
        }

        System.out.println();

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
        int x = ConsoleHelper.clampToGrid(base.column(),GRID_SIZE);
        int y = ConsoleHelper.clampToGrid(base.row(), GRID_SIZE);
        grid[y][x].add(Colors.color("BA", Colors.CYAN));
    }

    private void placeAircraft(List<String>[][] grid, AircraftState aircraft, boolean friendly) {
        double xPos = aircraft.position().column();
        double yPos = aircraft.position().row();

        int x = ConsoleHelper.clampToGrid((int) Math.floor(xPos), GRID_SIZE);
        int y = ConsoleHelper.clampToGrid((int) Math.floor(yPos), GRID_SIZE);

        String label = Colors.color(aircraft.id(), friendly ? Colors.GREEN : Colors.RED);
        grid[y][x].add(label);
    }

    private void printColumnHeaders() {
        System.out.print(ConsoleHelper.padRight("", 6));
        for (int x = 0; x < GRID_SIZE; x++) {
            char colLabel = (char) ('A' + x);
            System.out.print("|" + ConsoleHelper.center(String.valueOf(colLabel), CELL_WIDTH));
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
        return ConsoleHelper.padText(text, width);
    }

    private void placeMissile(List<String>[][] grid, MissileState missile) {
        double xPos = missile.position().column();
        double yPos = missile.position().row();

        int x = ConsoleHelper.clampToGrid((int) Math.floor(xPos), GRID_SIZE);
        int y = ConsoleHelper.clampToGrid((int) Math.floor(yPos), GRID_SIZE);

        String shortId = "M:" + shortenId(missile.id());
        String color =  Colors.YELLOW;

        grid[y][x].add(Colors.color(shortId, color));
    }
    private String shortenId(String id) {
        if (id == null || id.isBlank()) {
            return "?";
        }
        return id.length() <= 4 ? id : id.substring(0, 4);
    }
}
