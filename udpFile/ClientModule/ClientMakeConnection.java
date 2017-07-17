package udpFile.ClientModule;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientMakeConnection {

  private int serverPort;
  private DatagramSocket datagramSocket;
  private InetAddress hostAddress;
  private Client client;
  private ClientServerConfiguration server;

  ClientMakeConnection(Client _client, ClientServerConfiguration _server){
    client = _client;
    server = _server;
    serverPort = server.getServerPort();
    hostAddress = server.getServerAddress();
  }

  protected void connect(){
    try {
      datagramSocket = new DatagramSocket();
      ClientSend clientSend = new ClientSend(datagramSocket,client);
      ClientReceive clientReceive = new ClientReceive(server,datagramSocket,client);

      clientReceive.start();
      clientSend.sendSYN(hostAddress,serverPort);
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

}
