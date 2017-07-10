package udpFile;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class ServerReceive extends Thread{

  private Server server;
  ServerSend serverSend;

  ServerReceive(Server _server){
    server=_server;

  }

  public void run() {

    try {
      DatagramSocket socket = new DatagramSocket(server.port);
      serverSend = new ServerSend(server,socket);
      byte[] incomingBuffer = new byte[server.getWindowSize()];
      DatagramPacket incomingPacket = new DatagramPacket(incomingBuffer, incomingBuffer.length);
      System.out.println("UDP.Server is Up");
      ServerProcessIncomingMessage processIncomingMessage = new ServerProcessIncomingMessage(server,socket,incomingPacket);

      while (true) {
        try {
//          socket.setSoTimeout(5000);

          socket.receive(incomingPacket);
          String msg = new String(incomingPacket.getData(), incomingPacket.getOffset(), incomingPacket.getLength());
          System.out.println("received-->" + msg);
          System.out.println(msg.length());
          processIncomingMessage.processMsg(msg);

        } catch (Exception ex) {
          System.out.println(ex.getMessage());
        }
      }
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
  }
}
