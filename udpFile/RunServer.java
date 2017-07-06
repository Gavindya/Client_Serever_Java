package udpFile;

/**
 * Created by AdminPC on 7/6/2017.
 */
public class RunServer {
  public static void main(String[] args) {
    Server server = new Server(7777,65000,5,4500);
    server.serverUp();
  }
}
