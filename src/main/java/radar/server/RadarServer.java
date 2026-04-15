package radar.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import radar.RadarService;

import java.io.IOException;

public class RadarServer {

    private final int port;
    private Server server;

    public RadarServer(int port){

        this.port = port;
    }

    public void start() throws IOException {
        server = ServerBuilder
                .forPort(port)
                .addService(new RadarGrpcServiceImpl(new RadarService()))
                .build()
                .start();

        System.out.println("Radar server started on port " + port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down radar server...");
            stop();
        }));
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }


    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws Exception{
        RadarServer server = new RadarServer(9090);
        server.start();
        server.blockUntilShutdown();
    }
}
