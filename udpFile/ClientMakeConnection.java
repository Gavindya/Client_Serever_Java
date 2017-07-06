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
  private ClientNewServer server;

  ClientMakeConnection(Client _client, ClientNewServer _server){
    client = _client;
    server = _server;
    serverPort = server.server_port;
    hostAddress = server.server_address;
  }

  public void connect(){
    try {
      datagramSocket = new DatagramSocket();
      udpFile.ClientSend clientSend = new udpFile.ClientSend(datagramSocket);
      ClientReceive clientReceive = new ClientReceive(client,server,datagramSocket);

      clientSend.sendSYN(hostAddress,serverPort);
//      clientSend.sendACK(InetAddress.getLocalHost(),7777,987456,663258,client.getWindowSize());
      clientReceive.start();

    }
    catch(Exception e){
      e.printStackTrace();
    }

  }

}
