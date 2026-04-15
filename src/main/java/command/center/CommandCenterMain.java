package command.center;

import common.GridCell;
import common.Side;

public class CommandCenterMain {

    public static void main(String[] args)  throws  Exception{

        if (args.length != 1) {
            System.out.println("Usage: <BLUE|RED>");
            return;
        }

        Side side = Side.valueOf(args[0].toUpperCase());

        switch (side) {
            case BLUE -> startBlue();
            case RED -> startRed();
        }

    }

    private static void startBlue() throws Exception {
        CommandCenter center = new CommandCenter(Side.BLUE, new GridCell(0, 0));
        CommandCenterConsole console = new CommandCenterConsole(center);
        CommandCenterServer server = new CommandCenterServer(center);

        server.start(6001);
        console.start();
        System.out.println("BLUE Command Center started on port 6001");
        server.blockUntilShutdown();
    }

    private static void startRed() throws Exception {
        CommandCenter center = new CommandCenter(Side.RED, new GridCell(7, 7));
        CommandCenterServer server = new CommandCenterServer(center);
        CommandCenterConsole console = new CommandCenterConsole(center);

        server.start(6002);
        console.start();
        System.out.println("RED Command Center started on port 6002");
        server.blockUntilShutdown();
    }
}
