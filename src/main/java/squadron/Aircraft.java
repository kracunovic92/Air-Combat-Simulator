package squadron;

import common.GridCell;
import common.Position;
import common.Side;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class Aircraft implements Runnable {

    private static final double STEP = 0.1;


    private Thread thread;
    private final String id;
    private final Side side;
    private final AircraftType type;
    private final GridCell patrolCell;
    private final CountDownLatch startLatch;
    private final CountDownLatch doneLatch;

    private volatile  Position position;
    private volatile  boolean active = true;

    public Aircraft(String id, AircraftType type, Side side, Position initialPosition, GridCell patrolCell,CountDownLatch startLatch, CountDownLatch doneLatch){
        this.id = id;
        this.type = type;
        this.side = side;
        this.position = initialPosition;
        this.patrolCell = patrolCell;
        this.startLatch = startLatch;
        this.doneLatch = doneLatch;
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
    public void stop(){
        Thread stopThread = thread;
        thread = null;
        try{
            stopThread.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            active = false;
        }
    }

    private void move() {

        double dx = randomDelta();
        double dy = randomDelta();

        Position current = position;
        Position candidate = current.move(dx,dy);

        position = clampToPatrolCell(candidate);

    }

    private void pauseAccordingToSpeed() {

    }
    private void notifyCenter(){

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
        return Math.clamp(value, min, max);
    }

    private double round1(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
