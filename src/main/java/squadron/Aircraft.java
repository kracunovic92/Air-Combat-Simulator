package squadron;

import common.GridCell;
import common.Position;
import common.Side;
import radar.FlyingObjectType;
import radar.RadarContact;
import radar.client.RadarClient;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class Aircraft implements Runnable {

    private static final double STEP = 0.1;



    private final String id;
    private final Side side;
    private final AircraftType type;
    private final GridCell patrolCell;
    private final CountDownLatch startLatch;
    private final CountDownLatch doneLatch;

    private final Squadron squadron;

    private final RadarClient radarClient;

    private volatile  Position position;
    private volatile Thread thread;
    private volatile  boolean active = true;

    public Aircraft(String id, AircraftType type, Position initialPosition, GridCell patrolCell,
                    RadarClient radarClient,
                    Squadron squadron
    ){
        this.id = id;
        this.type = type;
        this.side = squadron.getSide();
        this.position = initialPosition;
        this.patrolCell = patrolCell;
        this.startLatch = squadron.getStartLatch();
        this.doneLatch = squadron.getDoneLatch();
        this.radarClient = radarClient;
        this.squadron = squadron;

    }
    public String getId() {
        return id;
    }

    public void start() {
        if (thread != null) {
            throw new IllegalStateException("Thread already running");
        }

        active = true;
        thread = new Thread(this, "Aircraft-" + id);
        thread.start();
    }

    @Override
    public void run(){
        try {
            startLatch.await();

            while(active) {
                move();
                pauseAccordingToSpeed();
                notifyCenter();
            }

        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }finally {
            doneLatch.countDown();

        }

    }
    public void stopAircraft() {
        active = false;
        Thread t = thread;
        if (t != null) {
            t.interrupt();
        }
    }

    private void move() {

        double dx = randomDelta();
        double dy = randomDelta();

        Position current = position;
        Position candidate = current.move(dx,dy);

        position = clampToPatrolCell(candidate);

    }

    private void pauseAccordingToSpeed() throws InterruptedException {
        SpeedClass speedClass = type.getSpeedClass();

        int minPause = speedClass.getMinPause();
        int maxPause = speedClass.getMaxPause();

        int sleepMs = ThreadLocalRandom.current().nextInt(minPause, maxPause + 1);

        Thread.sleep(sleepMs);

    }
    private void notifyCenter(){

        try {
            List<RadarContact> contactList = radarClient.reportAndScan(
                    id,
                    FlyingObjectType.AIRCRAFT,
                    position,
                    type.getRadarClass().getRange()
            );
            squadron.sendPosition(id, side, type, position);
            squadron.sendRadarContacts(id, contactList);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private double randomDelta(){

        int value = ThreadLocalRandom.current().nextInt(3);
        return switch (value){
            case 0 -> -STEP;
            case 1 -> 0;
            case 2 -> STEP;
            default -> throw new IllegalStateException("Unexpected value: " + value);
        };
    }


    private Position clampToPatrolCell(Position p) {
        double minX = patrolCell.column();
        double maxX = patrolCell.column() + 0.9;
        double minY = patrolCell.row();
        double maxY = patrolCell.row() + 0.9;

        double x = clamp(round1(p.x()), minX, maxX);
        double y = clamp(round1(p.y()), minY, maxY);

        return Position.of(x, y);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
