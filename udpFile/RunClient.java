package udpFile;

import java.net.InetAddress;

/**
 * Created by AdminPC on 7/6/2017.
 */
public class RunClient {
  public static void main(String[] args) throws Exception {
    //Client(int _mss, int _timestamp,int _window, int _server_port, InetAddress _server_address){
    Client client = new Client(2048,5000,65400,7777, InetAddress.getLocalHost(),25000);
    client.makeConnection();
  }
}
