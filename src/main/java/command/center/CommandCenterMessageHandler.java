package command.center;

import common.AircraftState;
import common.Position;
import common.Side;
import squadron.AircraftType;

import java.util.List;

public class CommandCenterMessageHandler {

    private final CommandCenter commandCenter;

    public CommandCenterMessageHandler(CommandCenter commandCenter) {
        this.commandCenter = commandCenter;
    }

    public void handle(String line) {
        if (line == null || line.isBlank()) {
            return;
        }

        String[] parts = line.split(";");
        switch (parts[0]) {
            case "REGISTER_SQUADRON" -> handleRegister(parts);
            case "POSITION" -> handlePosition(parts);
            case "RADAR" -> handleRadar(parts);
            case "LANDED" -> handleLanded(parts);
            default -> System.out.println("Unknown message: " + line);
        }
    }

    private void handleRegister(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Invalid REGISTER_SQUADRON message");
            return;
        }

        String side = parts[1];
        String squadronId = parts[2];

        System.out.println("Squadron registered: " + squadronId + " (" + side + ")");
    }

    private void handlePosition(String[] parts) {
        if (parts.length != 6) {
            System.out.println("Invalid POSITION message");
            return;
        }

        String id = parts[1];
        Side aircraftSide = Side.valueOf(parts[2]);
        AircraftType aircraftType = AircraftType.valueOf(parts[3]);
        double x = Double.parseDouble(parts[4]);
        double y = Double.parseDouble(parts[5]);

        AircraftState state = new AircraftState(
                id,
                aircraftSide,
                aircraftType,
                Position.of(x, y)
        );

        commandCenter.updateAircraft(state);
    }

    private void handleRadar(String[] parts) {
        if (parts.length != 3) {
            System.out.println("Invalid RADAR message");
            return;
        }
        String aircraftId = parts[1];
        String rawContacts = parts[2];

        List<String> contacts;
        if (rawContacts == null || rawContacts.isBlank()) {
            contacts = List.of();
        } else {
            contacts = List.of(rawContacts.split(","));
        }

        commandCenter.updateRadarContacts(aircraftId, contacts);
    }

    private void handleLanded(String[] parts) {
        if (parts.length != 2) {
            System.out.println("Invalid LANDED message");
            return;
        }

        String aircraftId = parts[1];
        commandCenter.removeAircraft(aircraftId);
    }

}
