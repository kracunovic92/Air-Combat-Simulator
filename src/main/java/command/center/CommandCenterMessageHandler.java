package command.center;

import common.AircraftState;
import common.GridCell;
import common.Position;
import common.Side;
import radar.FlyingObjectType;
import radar.RadarContact;
import squadron.aircraft.AircraftType;

import java.util.Arrays;
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
        if (parts.length < 4) {
            System.out.println("Invalid REGISTER_SQUADRON message");
            return;
        }

        String side = parts[1];
        String squadronId = parts[2];
        String aircraftPayload = parts[3];

        if (aircraftPayload.isBlank()) {
            System.out.println("Squadron registered without aircraft: " + squadronId + " (" + side + ")");
            return;
        }

        String[] aircraftEntries = aircraftPayload.split("\\|");

        for (String entry : aircraftEntries) {
            AircraftState state = parseAircraftState(entry);

            if (state == null) {
                System.out.println("Skipping invalid aircraft entry: " + entry);
                continue;
            }

            commandCenter.updateAircraft(state);
        }

        System.out.println("Squadron registered: " + squadronId + " (" + side + ")");
    }

    private void handlePosition(String[] parts) {
        if (parts.length != 7) {
            System.out.println("Invalid POSITION message");
            return;
        }

        String id = parts[1];
        String squadron_id = parts[6];
        Side aircraftSide = Side.valueOf(parts[2]);
        AircraftType aircraftType = AircraftType.valueOf(parts[3]);
        double x = Double.parseDouble(parts[4]);
        double y = Double.parseDouble(parts[5]);

        AircraftState state = new AircraftState(id, squadron_id, aircraftSide, aircraftType, Position.of(x, y));

        commandCenter.updateAircraft(state);
    }

    private void handleRadar(String[] parts) {
        if (parts.length == 2) {
            commandCenter.updateRadarContacts(parts[1], List.of());
            return;
        }
        if (parts.length != 3) {
            System.out.println("Invalid RADAR message");
            return;
        }
        String aircraftId = parts[1];
        String rawContacts = parts[2];

        List<RadarContact> contacts;
        contacts = Arrays.stream(rawContacts.split("\\|"))
                .map(this::parseRadarContact)
                .toList();
        commandCenter.updateRadarContacts(aircraftId, contacts);
    }

    private void handleLanded(String[] parts) {
        if (parts.length != 2) {
            System.out.println("Invalid LANDED message");
            return;
        }

    }

    private RadarContact parseRadarContact(String raw) {
        String[] fields = raw.split(",", -1);
        if (fields.length != 5) {
            throw new IllegalArgumentException("Invalid radar contact: " + raw);
        }

        String id = fields[0];
        FlyingObjectType type = FlyingObjectType.valueOf(fields[1]);
        double col = Double.parseDouble(fields[2]);
        double row = Double.parseDouble(fields[3]);
        double distance = Double.parseDouble(fields[4]);

        Position position = new Position(col, row);

        return new RadarContact(id, type, position, distance);
    }

    private AircraftState parseAircraftState(String data) {
        String[] fields = data.split(",");

        if (fields.length != 6) {
            return null;
        }

        try {
            String id = fields[0];
            String squadronId = fields[1];
            Side aircraftSide = Side.valueOf(fields[2]);
            AircraftType aircraftType = AircraftType.valueOf(fields[3]);
            double x = Double.parseDouble(fields[4]);
            double y = Double.parseDouble(fields[5]);

            return new AircraftState(
                    id,
                    squadronId,
                    aircraftSide,
                    aircraftType,
                    Position.clamped(x, y)
            );
        } catch (Exception e) {
            return null;
        }
    }

}
