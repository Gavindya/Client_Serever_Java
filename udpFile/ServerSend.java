package udpFile;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class ServerSend {

  private Server server;

  ServerSend(Server _server){
    server=_server;
  }

  public void sendSYN_ACK(InetAddress clientAddr, int clientPort, DatagramSocket datagramSocket,String ackMsg){
    try {
      System.out.println("sending syn ack");
      int sequenceNumber = (int) (Math.random() * 1000000);
      int ack = Integer.parseInt(ackMsg.substring(6,12))+1;
      server.setPendingClients(sequenceNumber,ackMsg);
      byte[] reply = createMsgSYNACK(sequenceNumber,ack).getBytes();
      DatagramPacket outgoingDatagram = new DatagramPacket(reply, reply.length, clientAddr, clientPort);
      datagramSocket.send(outgoingDatagram);
    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }
  }

  private String createMsgSYNACK(int seqNum,int ack) {
    return (MakeConstantDigits(0) +
      MakeConstantDigits(seqNum) +
      MakeConstantDigits(ack) +
      "1100" +
      MakeConstantDigits(server.getWindowSize()) +
      MakeConstantDigits(server.getMss()) +
      MakeConstantDigits(server.getWindowSize()) +
      MakeConstantDigits(server.getTimestamp()));

  }

  private static String MakeConstantDigits(int num) {
    String str = Integer.toString(num);

    switch (str.length()) {
      case 1:
        return ("00000" + str);
      case 2:
        return ("0000" + str);
      case 3:
        return ("000" + str);
      case 4:
        return ("00" + str);
      case 5:
        return ("0" + str);
      case 6:
        return (str);
      default:
        return (null);
    }
  }



}
