package UDP;

/**
 * Created by AdminPC on 6/27/2017.
 */
public class Main {
  public static void main(String[] args) throws Exception {

    UDPclient udPclient1 = new UDPclient(7777,1024);
    udPclient1.connect();

  }
}
