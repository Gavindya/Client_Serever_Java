import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by AdminPC on 6/26/2017.
 */
public class ServerWorkerThreadPool {

  private static ExecutorService executor;

  ServerWorkerThreadPool(int threadCount){
    executor =  Executors.newFixedThreadPool(threadCount);
  }

  public ExecutorService getCountservice() {

    return executor;
  }

  public void addWork(Runnable runnable){

    executor.submit(runnable);
  }

  public void shutdown(){

    executor.shutdown();
  }

}
