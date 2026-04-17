package command.center;
import squadron.SquadronConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class CommandCenterServer {

    private final CommandCenter commandCenter;
    private final CommandCenterMessageHandler messageHandler;


    private volatile boolean running;
    private ServerSocket serverSocket;

    public CommandCenterServer(CommandCenter commandCenter) {
        this.commandCenter = commandCenter;
        this.messageHandler = new CommandCenterMessageHandler(commandCenter);
    }


    public void start(int port) throws IOException {
        if (running) {
            throw new IllegalStateException("Server already running");
        }

        serverSocket = new ServerSocket(port);
        running = true;

        Thread acceptThread = new Thread(() -> {
            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    System.out.println("Squadron connected: " + client.getRemoteSocketAddress());
                    handleClientAsync(client);
                } catch (IOException e) {
                    if (running) {
                        e.printStackTrace();
                    }
                }
            }
        }, "CC-Accept-" + commandCenter.getSide());

        acceptThread.setDaemon(true);
        acceptThread.start();
    }

    private void handleClientAsync(Socket client) {
        Thread t = new Thread(() -> handleClient(client));
        t.setDaemon(true);
        t.start();
    }

    private void handleClient(Socket client) {
        try (
                client;
                BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
                PrintWriter writer = new PrintWriter(client.getOutputStream(), true)
        ) {
            String firstLine = reader.readLine();
            if (firstLine == null) {
                return;
            }

            String[] parts = firstLine.split(";");
            if (parts.length >= 3 && "REGISTER_SQUADRON".equals(parts[0])) {
                String squadronId = parts[2];
                commandCenter.registerSquadron(squadronId, new SquadronConnection(squadronId, client, writer));
                messageHandler.handle(firstLine);
            } else {
                System.out.println("Expected REGISTER_SQUADRON, got: " + firstLine);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                messageHandler.handle(line);
            }

        } catch (IOException e) {
            System.out.println("Squadron disconnected: " + e.getMessage());
        }
    }

    public void stop() throws IOException {
        running = false;
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        while (running) {
            Thread.sleep(1000);
        }
    }

}
