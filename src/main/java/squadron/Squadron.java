package squadron;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Squadron {

    private final String host;
    private final int port;

    private final List<Aircraft> aircraftList = new ArrayList<>();

    private final CountDownLatch startLatch = new CountDownLatch(1);
    private final CountDownLatch doneLatch = new CountDownLatch(5);


    private Socket socket;

    public Squadron(String host, int port){
        this.host = host;
        this.port = port;
    }

    public void addAircraft(Aircraft plane){
        if (aircraftList.size() >= 5){
            throw new IllegalArgumentException("At most 5 Planes ");

        }
        aircraftList.add(plane);
    }
    public void startAll() {

        for(Aircraft plane : aircraftList){
            plane.start();
        }
        startLatch.countDown();
    }
    public void stopAll(){
        for(Aircraft plane : aircraftList){
            plane.stop();
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
}
