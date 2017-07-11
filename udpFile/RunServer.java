package udpFile;

/**
 * Created by AdminPC on 7/6/2017.
 */
public class RunServer {
  public static void main(String[] args) {
//    Server(int _portNum, int _winSize, int _mss, int _timeStamp,int _keepAliveInterval,int _receivingWindowSize){
      Server server = new Server(9999,65000,100,5000,6000,3);
    server.serverUp();
  }
}
