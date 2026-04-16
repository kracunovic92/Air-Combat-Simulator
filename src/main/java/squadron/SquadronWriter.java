package squadron;

import common.GridCell;
import common.Position;
import common.Side;
import radar.RadarContact;
import squadron.aircraft.AircraftType;

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

    public synchronized void sendPosition(String id, Side side, AircraftType type, Position position, String squadron_id) {
        out.println(
                "POSITION;" +
                        id + ";" +
                        side + ";" +
                        type + ";" +
                        position.column() + ";" +
                        position.row()+ ";"+
                        squadron_id
        );
    }

    public synchronized void sendRadarContacts(String aircraftId, List<RadarContact> contacts) {
        String payload = contacts.stream()
                .map(this::serializeContact)
                .collect(Collectors.joining("|"));

        out.println("RADAR;" + aircraftId + ";" + payload);
    }

    public synchronized void sendLanded(String aircraftId) {
        out.println("LANDED;" + aircraftId);
    }

    private String serializeContact(RadarContact contact) {

        int col = (int)Math.floor(contact.position().column());
        int row = (int)Math.floor(contact.position().row());
        String label = GridCell.toLabel(row,col);

        return contact.id()
                + "," + contact.type()
                + "," + label
                + "," + contact.distance();
    }
}