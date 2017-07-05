package udpFile;

import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class ClientMakeConnection {

  private int serverPort;
  private DatagramSocket datagramSocket;
  private InetAddress hostAddress;
  private Client client;

  ClientMakeConnection(Client _client){
    client = _client;
    serverPort = client.server_port;
    hostAddress = client.server_address;
  }

  public void connect(){
    try {
      datagramSocket = new DatagramSocket();
      udpFile.ClientSend clientSend = new udpFile.ClientSend(client,datagramSocket);
      ClientReceive clientReceive = new ClientReceive(client,datagramSocket);

      clientSend.sendSYN(hostAddress,serverPort);
      clientReceive.start();
    }
    catch(Exception e){
      e.printStackTrace();
    }

  }

}
