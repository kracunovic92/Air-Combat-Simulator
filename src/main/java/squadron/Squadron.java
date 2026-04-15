package squadron;

import common.Position;
import common.Side;
import radar.RadarContact;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class Squadron {

    private static final int MAX_AIRCRAFT = 5;

    private final Side side;
    private final String squadronId;
    private final String host;
    private final int port;


    private final List<Aircraft> aircraftList = new ArrayList<>();
    private final CountDownLatch startLatch = new CountDownLatch(1);
    private final CountDownLatch doneLatch = new CountDownLatch(MAX_AIRCRAFT);

    private final SquadronMessageHandler messageHandler;
    private SquadronWriter messageWriter;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread thread;


    public Squadron(Side side, String squadronId, String host, int port) {
        this.side = side;
        this.squadronId = squadronId;
        this.host = host;
        this.port = port;
        this.messageHandler = new SquadronMessageHandler(this);
    }

    public Side getSide() {
        return side;
    }

    public String getSquadronId() {
        return squadronId;
    }

    public void addAircraft(Aircraft plane) {
        if (aircraftList.size() >= 5) {
            throw new IllegalArgumentException("At most 5 planes");
        }
        aircraftList.add(plane);
    }

    public void startAll() {
        if (aircraftList.size() != 5) {
            throw new IllegalStateException("Squadron must contain exactly 5 aircraft");
        }

        for (Aircraft plane : aircraftList) {
            plane.start();
        }
        startLatch.countDown();
    }

    public void stopAll() {
        for (Aircraft plane : aircraftList) {
            plane.stopAircraft();
        }
    }

    public void awaitTermination() throws InterruptedException {
        doneLatch.await();
    }

    public CountDownLatch getStartLatch() {
        return startLatch;
    }

    public CountDownLatch getDoneLatch() {
        return doneLatch;

    }

    public Aircraft findAircraftById(String aircraftId) {
        for (Aircraft aircraft : aircraftList) {
            if (aircraft.getId().equals(aircraftId)) {
                return aircraft;
            }
        }
        return null;
    }

    public void connect() throws IOException {
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        messageWriter = new SquadronWriter(out);
        messageWriter.sendRegister(side, squadronId);

        startCommandListener();
    }

    private void startCommandListener() {
        thread = new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    messageHandler.handle(line);
                }
            } catch (IOException e) {
                if (socket != null && !socket.isClosed()) {
                    System.out.println("Squadron listener stopped: " + e.getMessage());
                }
            }
        }, "Squadron-Listener-" + squadronId);

        thread.start();
    }
    public void sendPosition(String id, Side side, AircraftType type, Position position) {
        if (messageWriter == null) {
            System.out.println("Not good");
            return;
        }
        messageWriter.sendPosition(id, side, type, position);
    }


    public void sendRadarContacts(String aircraftId, List<RadarContact> contacts) {
        if (messageWriter == null) {
            System.out.println("Not good");
            return;
        }
        messageWriter.sendRadarContacts(aircraftId, contacts);
    }

    public void sendLanded(String aircraftId) {
        if (messageWriter == null) {
            System.out.println("Not good");
            return;
        }
        messageWriter.sendLanded(aircraftId);
    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ignored) {
        }

        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException ignored) {
        }

        if (out != null) {
            out.close();
        }

        if (thread != null) {
            try {
                thread.join(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
