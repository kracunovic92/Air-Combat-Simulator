package squadron;

import common.Position;
import common.Side;
import radar.RadarContact;

import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

public class SquadronWriter {

    private final PrintWriter out;

    public SquadronWriter(PrintWriter out) {
        this.out = out;
    }

    public synchronized void sendRegister(Side side, String squadronId) {
        out.println("REGISTER_SQUADRON;" + side + ";" + squadronId);
    }

    public synchronized void sendPosition(String id, Side side, AircraftType type, Position position) {
        out.println(
                "POSITION;" +
                        id + ";" +
                        side + ";" +
                        type + ";" +
                        position.x() + ";" +
                        position.y()
        );
    }

    public synchronized void sendRadarContacts(String aircraftId, List<RadarContact> contacts) {
        String ids = contacts.stream()
                .map(RadarContact::id)
                .collect(Collectors.joining(","));

        out.println("RADAR;" + aircraftId + ";" + ids);
    }

    public synchronized void sendLanded(String aircraftId) {
        out.println("LANDED;" + aircraftId);
    }
}