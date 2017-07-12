package udpFile;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class ServerReceive extends Thread{

  private Server server;
  ServerSend serverSend;
  ServerProcessIncomingMessage processIncomingMessage;

//  ServerSendKeepAlive keepAlive ;

  ServerReceive(Server _server){
    server=_server;

  }

  public void run() {

    try {
      DatagramSocket socket = new DatagramSocket(server.port);
      serverSend = new ServerSend(server,socket);
//      keepAlive = new ServerSendKeepAlive(server,socket);
//      keepAlive.start();

      byte[] incomingBuffer = new byte[server.getServer_windowSize()];
      DatagramPacket incomingPacket = new DatagramPacket(incomingBuffer, incomingBuffer.length);
      System.out.println("UDP.Server is Up");

      while (true) {
        try {
//          socket.setSoTimeout(5000);

          socket.receive(incomingPacket);
          DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(incomingPacket.getData(), incomingPacket.getOffset(), incomingPacket.getLength()));

          System.out.println(dataInputStream);

          String msg = new String(incomingPacket.getData(), incomingPacket.getOffset(), incomingPacket.getLength());
          System.out.println("received-->" + msg);
          processIncomingMessage=  new ServerProcessIncomingMessage(server,socket,incomingPacket,msg,dataInputStream);
          processIncomingMessage.start();
          processIncomingMessage.join();
//          Thread.sleep(500);

        } catch (Exception ex) {
          System.out.println(ex.getMessage());
        }
      }
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
  }
}
