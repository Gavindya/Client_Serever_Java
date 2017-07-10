package udpFile;

/**
 * Created by AdminPC on 7/6/2017.
 */
public class RunServer {
  public static void main(String[] args) {
    Server server = new Server(9999,65000,100,5000);
    server.serverUp();
  }
}
