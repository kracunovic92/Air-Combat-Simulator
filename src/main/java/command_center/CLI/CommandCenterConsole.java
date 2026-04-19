package command_center.CLI;

import command_center.ICommandCenter;

import java.util.Scanner;

public class CommandCenterConsole implements Runnable {

    private  volatile boolean running = false;
    private final ICommandCenter commandCenter;
    private final ICommandCenterConsole commandCenterConsole;
    private Thread thread;

    public CommandCenterConsole(ICommandCenter commandCenter, ICommandCenterConsole commandCenterConsole){
        this.commandCenter = commandCenter;
        this.commandCenterConsole = commandCenterConsole;
    }

    public void start(){

        if(running){
            throw new IllegalStateException("Already running console thread");
        }

        running = true;
        thread = new Thread(this);
        thread.start();
    }

    public void stop(){
        running = false;
        if(thread != null){
            thread.interrupt();
        }
    }

    @Override
    public void run(){

        Scanner scanner = new Scanner(System.in);

        printHelp();

        while(running){

            try{
                System.out.print(commandCenter.getSide() + "> ");
                if (!scanner.hasNextLine()) {
                    break;
                }

                String line = scanner.nextLine();

                if (line == null || line.isBlank()) {
                    continue;
                }

                handleCommand(line.trim());

            }catch (Exception e) {
                if (running) {
                    System.out.println("Console error: " + e.getMessage());
                }
            }
        }

        System.out.println("Console stopped.");

    }


    private void handleCommand(String line) {

        String[] parts = line.split("\\s+", 3);
        String cmd = parts[0].toLowerCase();

        switch (cmd) {

            case "help" -> printHelp();
            case "print" -> commandCenterConsole.printAirPicture();
            case "return" -> {
                if (parts.length < 2) {
                    System.out.println("Usage: return <aircraftId>");
                    return;
                }
                commandCenterConsole.returnAircraftToBase(parts[1]);
            }
            case "patrol" -> {
                if (parts.length < 3) {
                    System.out.println("Usage: patrol <aircraftId> <cells>");
                    return;
                }
                commandCenterConsole.assignPatrol(parts[1], parts[2]);
            }
            case "fire" -> {
                if (parts.length < 2) {
                    System.out.println("Usage: fire <targetId>");
                    return;
                }
                commandCenterConsole.fireAtTarget(parts[1]);
            }
            case "fire-nearest" -> commandCenterConsole.fireAtNearestTargets();
            case "exit" -> stop();
            default -> System.out.println("Unknown command. Type 'help'.");
        }
    }
    private void printHelp() {
        System.out.println("""
                Commands:
                  help
                  print
                  return <aircraftId>
                  patrol <aircraftId> <cells>
                  fire <targetId>
                  fire-nearest
                  exit
                """);
    }
}
