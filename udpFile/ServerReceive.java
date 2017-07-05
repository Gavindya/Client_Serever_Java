package udpFile;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class ServerReceive extends Thread{

  private Server server;

  ServerReceive(Server _server){
    server=_server;
  }

  public void run() {
    try {
      DatagramSocket socket = new DatagramSocket(server.port);
      byte[] incomingBuffer = new byte[server.getWindowSize()];
      DatagramPacket incomingPacket = new DatagramPacket(incomingBuffer, incomingBuffer.length);
      System.out.println("UDP.Server is Up");
      while (true) {
        try {
//          socket.setSoTimeout(5000);

          socket.receive(incomingPacket);
          String msg = new String(incomingPacket.getData(), incomingPacket.getOffset(), incomingPacket.getLength());
          System.out.println("received-->" + msg);
          System.out.println(msg.length());

          if(msg.substring(18,19).equals("1")&&msg.substring(19,20).equals("1")){
            System.out.println("syn ack");
          }else if(msg.substring(18,19).equals("1")){
            System.out.println("syn ");
            ServerSend serverSend = new ServerSend(server);
            serverSend.sendSYN_ACK(incomingPacket.getAddress(),incomingPacket.getPort(),socket,msg);
          }
          else if(msg.substring(19,20).equals("1")){
            System.out.println("ack ");
          }
          else if(msg.substring(19,20).equals("1") && msg.substring(20,21).equals("1")){
            System.out.println("ack fin ");
          }
          else if(msg.substring(20,21).equals("1")){
            System.out.println("fin ");
          }
          else if(msg.substring(21,22).equals("1")){
            System.out.println("reset ");
          }


        } catch (Exception ex) {
          System.out.println(ex.getMessage());
        }
      }
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
  }
}
