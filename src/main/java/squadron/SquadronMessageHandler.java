package squadron;

public class SquadronMessageHandler {


    private final Squadron squadron;

    public SquadronMessageHandler(Squadron squadron){
        this.squadron = squadron;
    }

    public void handle(String line){

        if(line == null){
            return;
        }

        String[] parts = line.split(";");
        String command = parts[0];

        switch (command){
            case "RETURN_TO_BASE" -> handleReturnToBase(parts);
            case "PATROL" -> handlePatrol(parts);
            default -> System.out.println("Unknown command");
        }


    }
    private void handleReturnToBase(String[] parts) {

        if (parts.length != 2) {
            System.out.println("Invalid RETURN_TO_BASE command");
            return;
        }

        String aircraftId = parts[1];

        squadron.handleReturnToBase(aircraftId);
    }
    private void handlePatrol(String[] parts) {
        if (parts.length < 3) {
            System.out.println("Invalid PATROL command");
            return;
        }

        String aircraftId = parts[1];
        String patrolCells = parts[2];

        squadron.handleAssignPatrol(aircraftId,patrolCells);
    }
}
