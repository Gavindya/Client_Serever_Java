package udpFile.ServerModule;

import java.net.DatagramSocket;
import java.util.Map;

/**
 * Created by Gavindya Jayawardena on 7/8/2017.
 */
public class ServerSendKeepAlive extends Thread {
  private long time=0;
  private DatagramSocket datagramSocket;
  private ServerSend serversend;
  Server server;
  ServerSendKeepAlive ( Server _server,DatagramSocket _datagramSocket){
    datagramSocket=_datagramSocket;
    server = _server;
    serversend = new ServerSend(server,datagramSocket);
  }
  public void run(){
    while (true){
      if((System.currentTimeMillis()-time)>=server.getKeepAliveInterval()){
        for (Map.Entry<Integer, ServerNewClient> entry : server.getConnectedClients().entrySet())
        {
          serversend.sendKeepAlive(entry.getKey());
        }
        time = System.currentTimeMillis();
      }
    }
  }
}
