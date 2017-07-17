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

//    ClientProcessData clientProcessData = new ClientProcessData("src/test1.txt");
//    if(Client.getServer().isAlive){
//      clientProcessData.start();
//    }
    byte[] data = ("The poems here collected are in the main reprints of pieces \n" +
      "that originally appeared in various newspapers and periodicals, \n" +
      "beginning with the Louisville Journal in the late ’50s. This \n" +
      "newspaper was at that time edited by the brilliant George D. \n" +
      "Prentice, my personal friend, who a few years after I had left \n" +
      "college offered me the assistant editorship of his paper.").getBytes();
    client.send(data);
  }
}
