package missles;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MissleService {

    private final ExecutorService launcer = Executors.newFixedThreadPool(5);
    private int availableMissiles = 20;

    public boolean hasMissiles(){
        return availableMissiles > 0;
    }

    public boolean consume(){
        if(hasMissiles()){
            availableMissiles--;
            return true;
        }
        return false;
    }
    public void fireAtTarget(String targetId){
        //TODO: implement later
        return;
    }
    public void fireAtNearestTargets(){
        //TODO: implement later
    }

    public void shutdown(){
        launcer.shutdown();
    }
}
