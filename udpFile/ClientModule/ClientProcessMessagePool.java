package udpFile.ClientModule;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by AdminPC on 7/17/2017.
 */
public class ClientProcessMessagePool {
  private ExecutorService processService;

  protected ClientProcessMessagePool(int threadCount) {
    this.processService = Executors.newFixedThreadPool(threadCount);
  }

  protected ExecutorService getCountservice() {
    return processService;
  }

  protected void addWork(Runnable runnable){
    processService.submit(runnable);
  }

  protected void shutdown(){
    processService.shutdown();
  }

}
