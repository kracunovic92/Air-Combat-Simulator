package missles;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class MissleService {

    private final ExecutorService launcer = Executors.newFixedThreadPool(5);
    private final AtomicInteger availableMissiles = new AtomicInteger(20);

    public boolean tryConsume(){

        while(true){
            int current = availableMissiles.get();
            if(current <= 0){
                return false;
            }
            if(availableMissiles.compareAndSet(current,current-1)){
                return  true;
            }
        }
    }

    public Future<MissileResult> fire(MissileTask task){

        if(!tryConsume()){
            throw  new IllegalStateException("No missles avail");
        }
        return launcer.submit(task);
    }

    public void shutdown(){
        launcer.shutdown();
    }
}
