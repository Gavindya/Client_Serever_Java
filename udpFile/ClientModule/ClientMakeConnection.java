package udpFile.ClientModule;

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
  private ClientServerConfiguration server;

  ClientMakeConnection(Client _client, ClientServerConfiguration _server){
    client = _client;
    server = _server;
    serverPort = server.server_port;
    hostAddress = server.server_address;
  }

  public void connect(){
    try {
      datagramSocket = new DatagramSocket();
      ClientSend clientSend = new ClientSend(datagramSocket);
      ClientReceive clientReceive = new ClientReceive(server,datagramSocket);

      clientReceive.start();
      clientSend.sendSYN(hostAddress,serverPort);
//      clientSend.sendACK(InetAddress.getLocalHost(),7777,987456,663258,client.getServer_windowSize());
    }
    catch(Exception e){
      e.printStackTrace();
    }

  }

}
