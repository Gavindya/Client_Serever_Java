package udpFile.ServerModule;

import java.io.ByteArrayOutputStream;
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
      byte[] reply = createFINack(serverSeq,clientSeq,sessionID);
      if(reply!=null){
        DatagramPacket outgoingDatagram = new DatagramPacket(reply, reply.length, clientAddr, clientPort);
        datagramSocket.send(outgoingDatagram);
      }
    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }
  }
  private byte[] createFINack(int serverSeq,int clientSeq,String sessionID){
    try {
      ByteArrayOutputStream boas = new ByteArrayOutputStream();
      java.io.DataOutputStream dos = new java.io.DataOutputStream(boas);
//    //data length 2 bytes
      dos.write(Byte.parseByte(Integer.toBinaryString(0), 2));
      dos.write(Byte.parseByte(Integer.toBinaryString(0), 2));
//    //sequence number - 4 bytes
      dos.writeInt(serverSeq);
//     //ack # - 4 bytes
      dos.writeInt(clientSeq);
//     //control value - 1 byte
      dos.write(Byte.parseByte(Integer.toBinaryString(6), 2));
      //window length 2 bytes
      dos.write(Byte.parseByte(Integer.toBinaryString(0), 2));
      dos.write(Byte.parseByte(Integer.toBinaryString(0), 2));
//      //mss 2 bytes
      dos.write(Byte.parseByte(Integer.toBinaryString(0), 2));
      dos.write(Byte.parseByte(Integer.toBinaryString(0), 2));
//      //timestamp 2 bytes
      dos.write(Byte.parseByte(Integer.toBinaryString(0), 2));
      dos.write(Byte.parseByte(Integer.toBinaryString(0), 2));

      dos.writeLong(Long.parseLong(sessionID));// 8 bytes
      return boas.toByteArray();
    }catch (Exception ex){
      ex.printStackTrace();
      return null;
    }


  }
  public void sendSYN_ACK(InetAddress clientAddr, int clientPort,int clientSequence,int window,int mss,int timestamp){
    try {
      System.out.println("sending syn ack");
      int sequenceNumber = (int) (Math.random() * 1000);
      long session = UUID.randomUUID().getLeastSignificantBits();
      while (Long.toBinaryString(session).length()!=64){
        session=UUID.randomUUID().getLeastSignificantBits();
      }
      System.out.println("session ID="+session);
      int ack = clientSequence+1;
//      ServerNewClient(int server_seqNum,int clientSeq, String filePath, InetAddress clientAddress, String _sessionID,int window,int mss,int timestamp) throws Exception{
      server.setPendingClients(sequenceNumber,String.valueOf(session),window,mss,timestamp,clientAddr,clientSequence);
      byte[] reply=  createMsgSYNACK(sequenceNumber,ack,session);
      if(reply!=null){
        DatagramPacket outgoingDatagram = new DatagramPacket(reply, reply.length, clientAddr, clientPort);
        datagramSocket.send(outgoingDatagram);
        System.out.println("datagram sent");
      }
    }catch (Exception ex){
      ex.printStackTrace();
    }
  }

  private byte[] createMsgSYNACK(int seqNum,int ack,long sessionID){
      //syn-ack is sent with sessionID as Data
    try {
      ByteArrayOutputStream boas = new ByteArrayOutputStream();
      java.io.DataOutputStream dos = new java.io.DataOutputStream(boas);
//    //data length 2 bytes
      dos.write(Byte.parseByte(Integer.toBinaryString(0), 2));
      dos.write(Byte.parseByte(Integer.toBinaryString(0), 2));
//    //sequence number - 4 bytes
      dos.writeInt(seqNum);
//     //ack # - 4 bytes
      dos.writeInt(ack);
//     //control value - 1 byte
      dos.write(Byte.parseByte(Integer.toBinaryString(12), 2));
      //window length 2 bytes
      String win1;
      String win2;
      if (Server.getServer_windowSize() < 256) {
        win1 = Integer.toBinaryString(0);
        win2 = Integer.toBinaryString(Server.getServer_windowSize());
      } else {
        win1 = MakeSixteen(Integer.toBinaryString(Server.getServer_windowSize())).substring(0, 8);
        win2 = MakeSixteen(Integer.toBinaryString(Server.getServer_windowSize())).substring(8, MakeSixteen(Integer.toBinaryString(Server.getServer_windowSize())).length());
      }
      dos.write((byte) Integer.parseInt(win1, 2));
      dos.write((byte) Integer.parseInt(win2, 2));
//      //mss 2 bytes
      String mss1;
      String mss2;
      if (server.getMss() < 256) {
        mss1 = Integer.toBinaryString(0);
        mss2 = Integer.toBinaryString(server.getMss());
      } else {
        mss1 = MakeSixteen(Integer.toBinaryString(server.getMss())).substring(0, 8);
        mss2 = MakeSixteen(Integer.toBinaryString(server.getMss())).substring(8, MakeSixteen(Integer.toBinaryString(server.getMss())).length());
      }
      dos.write((byte) Integer.parseInt(mss1, 2));
      dos.write((byte) Integer.parseInt(mss2, 2));
//      //timestamp 2 bytes
      String time1;
      String time2;
      if (server.getTimestamp() < 256) {
        time1 = Integer.toBinaryString(0);
        time2 = Integer.toBinaryString(server.getTimestamp());
      } else {
        time1 = MakeSixteen(Integer.toBinaryString(server.getTimestamp())).substring(0, 8);
        time2 = MakeSixteen(Integer.toBinaryString(server.getTimestamp())).substring(8, MakeSixteen(Integer.toBinaryString(server.getTimestamp())).length());
      }
      dos.write((byte) Integer.parseInt(time1, 2));
      dos.write((byte) Integer.parseInt(time2, 2));

      dos.writeLong(sessionID);// 8 bytes
      return boas.toByteArray();
    }catch (Exception ex){
      ex.printStackTrace();
      return null;
    }
//    return boas.toByteArray();

//    ByteArrayInputStream bis = new ByteArrayInputStream(boas.toByteArray());
//    DataInputStream dataInputStream = new DataInputStream(bis);
//
//    StringBuilder strBuilder = new StringBuilder();
//    for(int i=0;i<2;i++){
//      strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
//    }
//    int dataLength =Integer.parseInt(strBuilder.toString(),2);
//    System.out.println("DATA lENGTH"+dataLength);
//    strBuilder=new StringBuilder();
//    for(int i=0;i<4;i++){
//      strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
//    }
//    int serverSequence =Integer.parseInt(strBuilder.toString(),2);
//    System.out.println("SERVER SEQ "+serverSequence);
//    strBuilder=new StringBuilder();
//    for(int i=0;i<4;i++){
//      strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
//    }
//    int clientSequence =Integer.parseInt(strBuilder.toString(),2);
//    System.out.println("CLIENT SEQ"+clientSequence);
//    strBuilder=new StringBuilder();
//    int control = dataInputStream.read();
//    System.out.println("control="+control);
//    for(int i=0;i<2;i++){
//      strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
//    }
//    int window =Integer.parseInt(strBuilder.toString(),2);
//    System.out.println("WINDOW="+window);
//    strBuilder=new StringBuilder();
//    for(int i=0;i<2;i++){
//      strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
//    }
//    int mss =Integer.parseInt(strBuilder.toString(),2);
//    System.out.println("MSS="+mss);
//    strBuilder=new StringBuilder();
//    for(int i=0;i<2;i++){
//      strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
//      System.out.println("T "+strBuilder);
//    }
//    int timestamp =Integer.parseInt(strBuilder.toString(),2);
//    System.out.println("TIMESTAMP"+timestamp);
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
      byte[] reply = createDataACK(serverSeq,clientSeq,sessionID);
      if(reply!=null){
        DatagramPacket outgoingDatagram = new DatagramPacket(reply, reply.length, clientAddr, clientPort);
        datagramSocket.send(outgoingDatagram);
        System.out.println("SENT DATA ACK");
      }
    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }
  }
  private String MakeSixteen(String str) {
    switch (str.length()) {
      case 1:
        return ("000000000000000" + str);
      case 2:
        return ("00000000000000" + str);
      case 3:
        return ("0000000000000" + str);
      case 4:
        return ("000000000000" + str);
      case 5:
        return ("00000000000" + str);
      case 6:
        return ("0000000000" + str);
      case 7:
        return ("000000000" + str);
      case 8:
        return ("00000000" + str);
      case 9:
        return ("0000000" + str);
      case 10:
        return ("000000" + str);
      case 11:
        return ("00000" + str);
      case 12:
        return ("0000" + str);
      case 13:
        return ("000" + str);
      case 14:
        return ("00" + str);
      case 15:
        return ("0" + str);
      case 16:
        return (str);
      default:
        return (null);
    }
  }
  private byte[] createDataACK(int serverSeq,int clientSeq,String sessionID) {
    try {
      ByteArrayOutputStream boas = new ByteArrayOutputStream();
      java.io.DataOutputStream dos = new java.io.DataOutputStream(boas);
//    //data length 2 bytes
      dos.write(Byte.parseByte(Integer.toBinaryString(0), 2));
      dos.write(Byte.parseByte(Integer.toBinaryString(0), 2));
//    //sequence number - 4 bytes
      dos.writeInt(serverSeq);
//     //ack # - 4 bytes
      dos.writeInt(clientSeq);
      //control bit
      dos.write(Byte.parseByte(Integer.toBinaryString(4), 2));
      //window length 2 bytes
      String win1;
      String win2;
      if (Server.getServer_windowSize() < 256) {
        win1 = Integer.toBinaryString(0);
        win2 = Integer.toBinaryString(Server.getServer_windowSize());
      } else {
        win1 = MakeSixteen(Integer.toBinaryString(Server.getServer_windowSize())).substring(0, 8);
        win2 = MakeSixteen(Integer.toBinaryString(Server.getServer_windowSize())).substring(8, MakeSixteen(Integer.toBinaryString(Server.getServer_windowSize())).length());
      }
      dos.write((byte) Integer.parseInt(win1, 2));
      dos.write((byte) Integer.parseInt(win2, 2));
//      //mss 2 bytes
      dos.write((byte) Integer.parseInt(Integer.toBinaryString(0), 2));
      dos.write((byte) Integer.parseInt(Integer.toBinaryString(0), 2));
//      //timestamp 2 bytes
      dos.write((byte) Integer.parseInt(Integer.toBinaryString(0), 2));
      dos.write((byte) Integer.parseInt(Integer.toBinaryString(0), 2));

      dos.writeLong(Long.parseLong(sessionID));// 8 bytes
      return boas.toByteArray();
    }catch (Exception e){
      e.printStackTrace();
      return null;
    }
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
