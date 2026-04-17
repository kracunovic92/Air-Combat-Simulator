package squadron.aircraft;

import common.GridCell;
import common.Position;
import common.Side;
import radar.FlyingObjectType;
import radar.RadarContact;
import radar.client.RadarClient;
import squadron.Squadron;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class Aircraft implements Runnable {

    private static final double STEP = 0.1;

    private final String id;
    private final Side side;
    private final AircraftType type;
    private final CountDownLatch startLatch;
    private final CountDownLatch doneLatch;

    /// Lock for handling Landed state
    private final Object stateLock = new Object();

    private final Squadron squadron;

    private final RadarClient radarClient;

    private volatile GridCell patrolCell;
    private volatile AircraftMode mode;

    private volatile  Position position;
    private volatile Thread thread;
    private volatile  boolean active = true;

    public Aircraft(String id, AircraftType type, Position initialPosition, GridCell patrolCell, RadarClient radarClient, Squadron squadron){
        this.id = id;
        this.type = type;
        this.side = squadron.getSide();
        this.position = initialPosition;
        this.patrolCell = patrolCell;
        this.startLatch = squadron.getStartLatch();
        this.doneLatch = squadron.getDoneLatch();
        this.radarClient = radarClient;
        this.squadron = squadron;
        this.mode = AircraftMode.PATROLLING;

    }
    public String getId() {
        return id;
    }
    public Side getSide(){
        return  side;
    }

    public void  handleAssignPatrol(GridCell cell){
        synchronized (stateLock){
            patrolCell = cell;
            position = new Position(cell.column(), cell.row());
            mode = AircraftMode.PATROLLING;
            stateLock.notifyAll();
        }
    }
    /// For now just teleport Aircraft to base
    public void handleReturnToBase(){

        synchronized (stateLock){

            patrolCell = side.base();
            position = new Position(patrolCell.column(), patrolCell.row());
            mode = AircraftMode.LANDED;
            squadron.sendLanded(id);
            stateLock.notifyAll();
        }
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
                synchronized (stateLock){
                    while (active && mode == AircraftMode.LANDED){
                        stateLock.wait();
                    }
                }
                tick();
                pauseAccordingToSpeed();
                notifyCommandCenter();
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

    private void tick(){
        synchronized (stateLock){
            switch (mode){
                case PATROLLING -> movePatrol();
                case LANDED -> {

                }
            }
        }
    }
    private void movePatrol() {

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
    private void notifyCommandCenter(){

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

        double x = clamp(round1(p.column()), minX, maxX);
        double y = clamp(round1(p.row()), minY, maxY);

        return Position.of(x, y);
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
