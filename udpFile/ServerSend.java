package udpFile;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class ServerSend {

  private Server server;
  private DatagramSocket datagramSocket;

  ServerSend(Server _server,DatagramSocket _socket){
    server=_server;
    datagramSocket = _socket;
  }

  public void sendSYN_ACK(InetAddress clientAddr, int clientPort, String synMsg){
    try {
      System.out.println("sending syn ack");
//      System.out.println(ackMsg);
      int sequenceNumber = (int) (Math.random() * 1000000);
      int ack = Integer.parseInt(synMsg.substring(6,12))+1;
      Server.setPendingClients(sequenceNumber,synMsg);
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
  public void sendACK(InetAddress clientAddr, int clientPort, String msg){
    try {
      int serverSeq = Integer.parseInt(msg.substring(12,18)); //sending the seq which client expected
      int receivedClientSeq = Integer.parseInt(msg.substring(6,12));
      int ack =  receivedClientSeq +1; //excpected client seq = curent client seq+1
      byte[] reply = createMsgACK(serverSeq,ack).getBytes();
      DatagramPacket outgoingDatagram = new DatagramPacket(reply, reply.length, clientAddr, clientPort);
      datagramSocket.send(outgoingDatagram);

      if(server.getConnectedClients().containsKey(serverSeq)){
        for (Map.Entry<Integer, ServerNewClient> entry : server.getConnectedClients().entrySet())
        {
          int key = entry.getKey();
          if(key==serverSeq){
            ServerNewClient client = entry.getValue();
            client.client_seqNumber=receivedClientSeq;
            server.getConnectedClients().remove(key);
            server.getConnectedClients().put(key+1,client);
            break;
          }
        }
      }

    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }
  }

  private String createMsgACK(int seqNum,int ack) {
    return (MakeConstantDigits(0) +
      MakeConstantDigits(seqNum) +
      MakeConstantDigits(ack) +
      "0100" +
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
