package udpFile;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;
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

//  public void sendKeepAlive(InetAddress clientAddr, int clientPort){
//
//  }
  public void sendSYN_ACK(InetAddress clientAddr, int clientPort, String synMsg){
    try {
      System.out.println("sending syn ack");
//      System.out.println(ackMsg);
      int sequenceNumber = (int) (Math.random() * 1000000);
      UUID sessionID =UUID.randomUUID();
      System.out.println(sessionID.toString());
      int ack = Integer.parseInt(synMsg.substring(6,12))+1;
      Server.setPendingClients(sequenceNumber,synMsg,sessionID.toString());
      byte[] reply = createMsgSYNACK(sequenceNumber,ack,sessionID).getBytes();
      System.out.println("Syn ack ="+createMsgSYNACK(sequenceNumber,ack,sessionID));
      DatagramPacket outgoingDatagram = new DatagramPacket(reply, reply.length, clientAddr, clientPort);
      datagramSocket.send(outgoingDatagram);
      System.out.println("datagram sent");
    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }
  }

  private String createMsgSYNACK(int seqNum,int ack,UUID sessionID) {
    //syn-ack is sent with sessionID as Data
    return (MakeConstantDigits(0) +
      MakeConstantDigits(seqNum) +
      MakeConstantDigits(ack) +
      "1100" +
      MakeConstantDigits(server.getWindowSize()) +
      MakeConstantDigits(server.getMss()) +
      MakeConstantDigits(server.getWindowSize()) +
      MakeConstantDigits(server.getTimestamp())+
      (sessionID.toString()));

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
//      MakeConstantDigits(server.getWindowSize()) +
//      MakeConstantDigits(server.getMss()) +
//      MakeConstantDigits(server.getWindowSize()) +
//      MakeConstantDigits(server.getTimestamp()));
//
//  }
  public void sendDataACK(InetAddress clientAddr, int clientPort, int serverSeq, int clientSeq){
    try {
      System.out.println("in send ack method");
      byte[] reply = createDataACK(serverSeq,clientSeq).getBytes();
      System.out.println("ACK==>"+createDataACK(serverSeq,clientSeq));
      DatagramPacket outgoingDatagram = new DatagramPacket(reply, reply.length, clientAddr, clientPort);
      datagramSocket.send(outgoingDatagram);
    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }
  }
  private String createDataACK(int serverSeq,int clientSeq) {
    return (MakeConstantDigits(0) +
      MakeConstantDigits(serverSeq) +
      MakeConstantDigits(clientSeq) +
      "0100" +
      MakeConstantDigits(0) +
      MakeConstantDigits(0) +
      MakeConstantDigits(0) +
      MakeConstantDigits(0));

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
