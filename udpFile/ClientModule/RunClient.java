package udpFile.ClientModule;

import java.net.InetAddress;

/**
 * Created by AdminPC on 7/6/2017.
 */
public class RunClient {
  public static void main(String[] args) throws Exception {
//    ClientModule(int _mss, int _keepAliveInterval,int _window, int _server_port, InetAddress _server_address,int _waitingTime,int bufferSize,int numOfElementInWindow){
    Client client = new Client(2048,1000,65400,9999, InetAddress.getLocalHost(),25000,5,2);
    client.makeConnection();
//    client.setServer();
    Thread.sleep(1000);

    ClientProcessData clientProcessData = new ClientProcessData("src/test1.txt");
    if(Client.getServer().isAlive){
      clientProcessData.start();
    }
  }
}
