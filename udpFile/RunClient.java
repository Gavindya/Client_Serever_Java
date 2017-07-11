package udpFile;

import java.net.InetAddress;

/**
 * Created by AdminPC on 7/6/2017.
 */
public class RunClient {
  public static void main(String[] args) throws Exception {
    //Client(int _mss, int _keepAliveTimeInterval,int _window, int _server_port, InetAddress _server_address,waitingtime){
    Client client = new Client(2048,1000,65400,9999, InetAddress.getLocalHost(),25000,15,2);
    client.makeConnection();
//    client.setServer();
    ClientProcessData clientProcessData = new ClientProcessData("src/test.txt");
    clientProcessData.start();

  }
}
