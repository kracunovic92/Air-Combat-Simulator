package squadron;

import common.GridCell;
import common.Position;
import common.Side;
import radar.client.RadarClient;
import squadron.aircraft.Aircraft;
import squadron.aircraft.AircraftType;


public class SquadronMain {

    //TODO: Fix handle starting of processes
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Missing arguments");
            return;
        }

        Side side = Side.valueOf(args[0].toUpperCase());
        String squadronName = args[1].toUpperCase();
        int squadronNumber = Integer.parseInt(args[2]);

        switch (side) {
            case BLUE -> startBlue(side,squadronName,squadronNumber);
            case RED -> startRed(side,squadronName,squadronNumber);
        }
    }

    private static void startBlue(Side side, String name, Integer squadronNumber ) throws Exception {

        Squadron squadron = new Squadron(side, name, "localhost", 6001);
        RadarClient radarClient = new RadarClient("localhost", 9090);

        addBlueAircraft(squadron,radarClient, squadronNumber);

        try{
            squadron.startAll();
            squadron.connect();

            System.out.println("BLUE Squadron started");
            squadron.awaitTermination();
        }finally {
            squadron.stopAll();
            squadron.close();
        }

    }

    private static void startRed(Side side, String name, Integer squadronNumber) throws Exception {

        Squadron squadron = new Squadron(side, name, "localhost", 6002);
        RadarClient radarClient = new RadarClient("localhost", 9090);

        addRedAircraft(squadron,radarClient, squadronNumber);

        try{
            squadron.startAll();
            squadron.connect();

            System.out.println("RED Squadron started");
            squadron.awaitTermination();

        }finally {
            squadron.stopAll();
            squadron.close();
        }
    }

    private static void addBlueAircraft(Squadron squadron, RadarClient radarClient, Integer squadronNumber) {

        squadron.addAircraft(new Aircraft("P1" + squadronNumber + "1", AircraftType.F_22, Position.of(0.0, 1.0), new GridCell(0, 1), radarClient, squadron));
        squadron.addAircraft(new Aircraft("P1" + squadronNumber + "2", AircraftType.F_35, Position.of(0.0, 2.0), new GridCell(0, 2), radarClient, squadron));
        squadron.addAircraft(new Aircraft("P1" + squadronNumber + "3", AircraftType.F_15, Position.of(1.0, 1.0), new GridCell(1, 1), radarClient, squadron));
        squadron.addAircraft(new Aircraft("P1" + squadronNumber + "4", AircraftType.F_22, Position.of(1.0, 2.0), new GridCell(1, 2), radarClient, squadron));
        squadron.addAircraft(new Aircraft("P1" + squadronNumber + "5", AircraftType.F_15, Position.of(2.0, 1.0), new GridCell(2, 1), radarClient, squadron));

    }

    private static void addRedAircraft(Squadron squadron, RadarClient radarClient,  Integer squadronNumber ) {

        squadron.addAircraft(new Aircraft("C1" + squadronNumber + "1", AircraftType.MIG_31, Position.of(7.0, 6.0), new GridCell(7, 6), radarClient, squadron));
        squadron.addAircraft(new Aircraft("C1" + squadronNumber + "2", AircraftType.SU_35, Position.of(7.0, 5.0), new GridCell(7, 5), radarClient, squadron));
        squadron.addAircraft(new Aircraft("C1" + squadronNumber + "3", AircraftType.SU_30, Position.of(6.0, 6.0), new GridCell(6, 6), radarClient, squadron));
        squadron.addAircraft(new Aircraft("C1" + squadronNumber + "4", AircraftType.SU_57, Position.of(6.0, 5.0), new GridCell(6, 5), radarClient, squadron));
        squadron.addAircraft(new Aircraft("C1" + squadronNumber + "5", AircraftType.SU_35, Position.of(5.0, 6.0), new GridCell(5, 6), radarClient, squadron));

    }

}
