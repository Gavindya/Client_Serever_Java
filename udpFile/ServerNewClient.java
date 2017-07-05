package udpFile;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class ServerNewClient extends Thread{

  public int client_windowSize;
  public int client_mss;
  public int client_timestamp;
  private static int seqNumber;
  public int client_seqNumber;

  ServerNewClient(int windowSize, int mss, int timestamp ,int seqNum){
    client_mss=mss;
    client_seqNumber=seqNum;
    client_timestamp=timestamp;
    client_windowSize = windowSize;
    seqNumber = (int) (Math.random() * 1000000);
  }

  public void run(){
    System.out.println(seqNumber);
  }
}
