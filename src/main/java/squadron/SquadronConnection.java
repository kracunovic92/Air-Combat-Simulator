package squadron;

import java.io.PrintWriter;
import java.net.Socket;

public class SquadronConnection {

    private final String squadronId;
    private final Socket socket;
    private final PrintWriter out;

    public SquadronConnection(String squadronId, Socket socket, PrintWriter out) {
        this.squadronId = squadronId;
        this.socket = socket;
        this.out = out;
    }



    public String getSquadronId() {
        return squadronId;
    }

    public Socket getSocket() {
        return socket;
    }

    public synchronized void send(String message) {
        out.println(message);
    }
}
