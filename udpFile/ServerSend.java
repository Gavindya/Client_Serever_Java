package udpFile;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.UUID;

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

//  public void closeDatagram(){
//      datagramSocket.close();
//  }
  public void sendFINack(InetAddress clientAddr, int clientPort, int serverSeq, int clientSeq,String sessionID){
    try {
      System.out.println("in send fin ack ");
      byte[] reply = createFINack(serverSeq,clientSeq,sessionID).getBytes();
      System.out.println("FIN ACK==>"+createFINack(serverSeq,clientSeq,sessionID));
      DatagramPacket outgoingDatagram = new DatagramPacket(reply, reply.length, clientAddr, clientPort);
      datagramSocket.send(outgoingDatagram);
    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }
  }
  private String createFINack(int serverSeq,int clientSeq,String sessionID){
    return (MakeConstantDigits(0) +
      MakeConstantDigits(serverSeq) +
      MakeConstantDigits(clientSeq) +
      "0110" +
      MakeConstantDigits(0) +
      MakeConstantDigits(0) +
      MakeConstantDigits(0) +
      MakeConstantDigits(0)+
      sessionID);
  }
  public void sendSYN_ACK(InetAddress clientAddr, int clientPort, String synMsg){
    try {
      System.out.println("sending syn ack");
//      System.out.println(ackMsg);
      int sequenceNumber = (int) (Math.random() * 1000000);
      long session = UUID.randomUUID().getLeastSignificantBits();

      while (Long.toBinaryString(session).length()!=64){
        session=UUID.randomUUID().getLeastSignificantBits();
      }
      System.out.println(session);
      int ack = Integer.parseInt(synMsg.substring(6,12))+1;
      Server.setPendingClients(sequenceNumber,synMsg,String.valueOf(session));
      String synAck =  createMsgSYNACK(sequenceNumber,ack,session);
      byte[] reply =synAck.getBytes();
//      System.out.println(synAck);
//      System.out.println("Syn ack ="+synAck.substring(synAck.length()-20,synAck.length()));
      DatagramPacket outgoingDatagram = new DatagramPacket(reply, reply.length, clientAddr, clientPort);
      datagramSocket.send(outgoingDatagram);
      System.out.println("datagram sent");
    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }
  }

  private String createMsgSYNACK(int seqNum,int ack,long sessionID) {
    //syn-ack is sent with sessionID as Data
    return (MakeConstantDigits(0) +
      MakeConstantDigits(seqNum) +
      MakeConstantDigits(ack) +
      "1100" +
      MakeConstantDigits(server.getServer_windowSize()) +
      MakeConstantDigits(server.getMss()) +
      MakeConstantDigits(server.getServer_windowSize()) +
      MakeConstantDigits(server.getTimestamp())+
      (sessionID));

  }
//  public void sendACK(InetAddress clientAddr, int clientPort, int serverSeq, int clientSeq){
//    try {
//      System.out.println("in send ack method");
//      byte[] reply = createMsgACK(serverSeq,clientSeq).getBytes();
//      System.out.println("ACK in handshake ACK==>"+createMsgACK(serverSeq,clientSeq));
//      DatagramPacket outgoingDatagram = new DatagramPacket(reply, reply.length, clientAddr, clientPort);
//      datagramSocket.send(outgoingDatagram);
//    }catch (Exception ex){
//      System.out.println(ex.getMessage());
//    }
//  }
//
//  private String createMsgACK(int serverSeq,int clientSeq) {
//    return (MakeConstantDigits(0) +
//      MakeConstantDigits(serverSeq) +
//      MakeConstantDigits(clientSeq) +
//      "0100" +
//      MakeConstantDigits(server.getServer_windowSize()) +
//      MakeConstantDigits(server.getMss()) +
//      MakeConstantDigits(server.getServer_windowSize()) +
//      MakeConstantDigits(server.getTimestamp()));
//
//  }
  public void sendDataACK(InetAddress clientAddr, int clientPort, int serverSeq, int clientSeq,String sessionID){
    try {
      System.out.println("in send ack method");
      byte[] reply = createDataACK(serverSeq,clientSeq,sessionID).getBytes();
      System.out.println("ACK==>"+createDataACK(serverSeq,clientSeq,sessionID));
      DatagramPacket outgoingDatagram = new DatagramPacket(reply, reply.length, clientAddr, clientPort);
      datagramSocket.send(outgoingDatagram);
    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }
  }
  private String createDataACK(int serverSeq,int clientSeq,String sessionID) {
    return (MakeConstantDigits(0) +
      MakeConstantDigits(serverSeq) +
      MakeConstantDigits(clientSeq) +
      "0100" +
      MakeConstantDigits(0) +
      MakeConstantDigits(0) +
      MakeConstantDigits(0) +
      MakeConstantDigits(0)+
      sessionID);

  }
  public void sendKeepAlive(int seqNum){
    try{
      String keepAlive = createMsgKeepAlive(seqNum);
      byte[] msgByteArray = keepAlive.getBytes();
      DatagramPacket datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length,InetAddress.getLocalHost() , server.port);
      datagramSocket.send(datagramPacket);
      System.out.println("sending KeepAlive time ="+System.currentTimeMillis());
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }


  private String createMsgKeepAlive(int seqNum){
    return (MakeConstantDigits(0) +
      MakeConstantDigits(seqNum) +
      MakeConstantDigits(0) +
      "0000" +
      MakeConstantDigits(0) +
      MakeConstantDigits(0) +
      MakeConstantDigits(0) +
      MakeConstantDigits(0));
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
