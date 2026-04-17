package missles;

import command.center.CommandCenter;
import common.Position;
import radar.FlyingObjectType;
import radar.RadarContact;
import radar.client.RadarClient;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

public class MissileTask implements Callable<MissileResult> {

    private final String missileId;
    private final String targetId;
    private final Position launchPosition;
    private final Position targetCoordinates;
    private final CommandCenter commandCenter;

    private final RadarClient radarClient;

    private Position currentPosition;

    private static final double MISSILE_SPEED = 0.4;
    private static final double DRIFT = 0.05;
    private static final double RADAR_RANGE = 0.3;

    public MissileTask(String missileId, String targetId, Position launchPosition, Position targetCoordinates, CommandCenter commandCenter) {
        this.missileId = missileId;
        this.targetId = targetId;
        this.launchPosition = launchPosition;
        this.targetCoordinates = targetCoordinates;
        this.radarClient = new RadarClient("localhost", 9090);
        this.commandCenter = commandCenter;
        this.currentPosition = launchPosition;
    }

    @Override
    public MissileResult call() throws Exception {
        while (true) {
            moveTowardTargetWithDrift();

            List<RadarContact> contacts = radarClient.reportAndScan(missileId, FlyingObjectType.MISSILE, currentPosition, RADAR_RANGE);

            boolean targetSeen = contacts.stream()
                    .anyMatch(c -> c.type() == FlyingObjectType.AIRCRAFT && c.id().equals(targetId));

            if (targetSeen) {

                //TODO:: Report hit
            }

            if (reachedTargetCoordinates()) {
                //TODO:: Report miss
                return new MissileResult(missileId, targetId, false, currentPosition);
            }

            Thread.sleep(200);
        }
    }

    private void moveTowardTargetWithDrift() {
        double dx = targetCoordinates.column() - currentPosition.column();
        double dy = targetCoordinates.row() - currentPosition.row();

        double len = Math.sqrt(dx * dx + dy * dy);
        if (len == 0.0) {
            return;
        }

        double stepX = (dx / len) * MISSILE_SPEED;
        double stepY = (dy / len) * MISSILE_SPEED;

        double driftX = ThreadLocalRandom.current().nextDouble(-DRIFT, DRIFT);
        double driftY = ThreadLocalRandom.current().nextDouble(-DRIFT, DRIFT);

        currentPosition = Position.clamped(currentPosition.column() + stepX + driftX, currentPosition.row() + stepY + driftY);
    }

    private boolean reachedTargetCoordinates() {
        double dx = targetCoordinates.column() - currentPosition.column();
        double dy = targetCoordinates.row() - currentPosition.row();
        return Math.sqrt(dx * dx + dy * dy) < 0.25;
    }
}