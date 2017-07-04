package ThreadsEx;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalculationThreadPool {
    private ExecutorService countservice;

    public CalculationThreadPool(int threadCount) {
        this.countservice = Executors.newFixedThreadPool(threadCount);
    }

    public ExecutorService getCountservice() {
        return countservice;
    }

    public void addWork(Runnable runnable){
        countservice.submit(runnable);
    }

    public void shutdown(){
        countservice.shutdown();
    }
}
