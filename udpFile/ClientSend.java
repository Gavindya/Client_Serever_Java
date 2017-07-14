package udpFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class ClientSend extends Thread {
//  private ClientModule client;
  private DatagramSocket datagramSocket;
  private DatagramPacket datagramPacket;

  ClientSend(DatagramSocket dsocket) {
//    client = _client;
    datagramSocket = dsocket;
  }

  public void sendSYN(InetAddress serverAddr, int portNum) throws Exception{
//    try {
      System.out.println("sending SYN");
      int sequenceNumber = (int) (Math.random() * 1000);
//    int sequenceNumber = (int) (Math.random() * 1000000);
      Client.setSequenceNumber(sequenceNumber);
//      String syn = createMsgSYN(sequenceNumber);
//      System.out.println("syn"+syn);
//    System.out.println("syn bytes ======"+syn.getBytes().length);
//    MakeLength(sequenceNumber);
//      byte[] msgByteArray = syn.getBytes();

      ByteArrayOutputStream boas = new ByteArrayOutputStream();
      java.io.DataOutputStream dos = new java.io.DataOutputStream(boas);

//    //data length 2 bytes
      dos.write(Byte.parseByte(Integer.toBinaryString(0), 2));
    System.out.println(Byte.parseByte(Integer.toBinaryString(0), 2));
      dos.write(Byte.parseByte(Integer.toBinaryString(0), 2));
    System.out.println(Byte.parseByte(Integer.toBinaryString(0), 2));
//    //sequence number - 4 bytes
      dos.writeInt(sequenceNumber);
//     //ack # - 4 bytes
     dos.writeInt(0);
//     //control value - 1 byte
     dos.write(Byte.parseByte(Integer.toBinaryString(8), 2));
    System.out.println(Byte.parseByte(Integer.toBinaryString(8), 2));

    //window length 2 bytes
    String win1 = Integer.toBinaryString(Client.getWindowSize()).substring(0,8);
    String win2 = Integer.toBinaryString(Client.getWindowSize()).substring(8,Integer.toBinaryString(Client.getWindowSize()).length());

    dos.write((byte) Integer.parseInt(win1, 2));
    System.out.println((byte) Integer.parseInt(win1, 2));
    dos.write((byte) Integer.parseInt(win2, 2));
    System.out.println((byte) Integer.parseInt(win2, 2));
//      //mss 2 bytes
    String mss1 = Integer.toBinaryString(Client.getMss()).substring(0,8);
    String mss2 = Integer.toBinaryString(Client.getMss()).substring(8,Integer.toBinaryString(Client.getMss()).length());

    dos.write((byte) Integer.parseInt(mss1, 2));
    System.out.println((byte) Integer.parseInt(mss1, 2));
    dos.write((byte) Integer.parseInt(mss2, 2));
    System.out.println((byte) Integer.parseInt(mss2, 2));
//      //timestamp 2 bytes
    String time1 = Integer.toBinaryString(Client.getKeepAliveTimeInerval()).substring(0,8);
    String time2 = Integer.toBinaryString(Client.getKeepAliveTimeInerval()).substring(8,Integer.toBinaryString(Client.getKeepAliveTimeInerval()).length());

    dos.write((byte) Integer.parseInt(time1, 2));
    System.out.println((byte) Integer.parseInt(time1, 2));
    dos.write((byte) Integer.parseInt(time2, 2));
    System.out.println((byte) Integer.parseInt(time2, 2));

    System.out.println("-----------------");
    byte[] msgByteArray = boas.toByteArray();

      datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length, serverAddr, portNum);
      datagramSocket.send(datagramPacket);

    ByteArrayInputStream bis = new ByteArrayInputStream(msgByteArray);
    DataInputStream dis = new DataInputStream(bis);
    for(int i=0;i<20;i++){
      System.out.println(dis.read());
    }
    System.out.println("*********");
  }
//  clientSend.sendACK(client.server_address,client.server_port,client.getSequenceNumber()+1,client.server_sequenceNumber+1,client.getServer_windowSize());


  //  public void sendKeepAlive(){
//    try{
//      String keepAlive = createMsgKeepAlive();
//      byte[] msgByteArray = keepAlive.getBytes();
//      datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length, ClientModule.getServer().server_address, ClientModule.getServer().server_port);
//      datagramSocket.send(datagramPacket);
//      System.out.println("sending KeepAlive time ="+System.currentTimeMillis());
//    }catch(Exception ex){
//      ex.printStackTrace();
//    }
//  }
  public void sendACK(InetAddress serverAddr, int portNum,int seq,int ack,int window) {
    try{

      String syn = createMsgACK(seq,ack,window);
        System.out.println("ACK for syn-ack "+syn);
      byte[] msgByteArray = syn.getBytes();
      datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length, serverAddr, portNum);
      datagramSocket.send(datagramPacket);
    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }
  }
    public void sendData(char[] cbuf){
    try{
//      String data = createDataMsg(cbuf,seq,ack,window);
//      for(char[] cbuf : ClientModule.getBuffer()){
        String data = createDataMsg(cbuf);
        byte[] msgByteArray = data.getBytes();
//      ClientModule.addOutgoingBuffer(msgByteArray); //add to outgoing buffer
        Client.currentOutgoingMsg(msgByteArray); //add to current buffer
        datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length, Client.getServer().getServer_address(), Client.getServer().getServer_port());
        datagramSocket.send(datagramPacket);
//      }

    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }
  }
    public void sendKeepAlive(){
        try{
            String data = createMsgKeepAlive();
            byte[] msgByteArray = data.getBytes();
            datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length, Client.getServer().getServer_address(), Client.getServer().getServer_port());
            datagramSocket.send(datagramPacket);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }
  private String createMsgKeepAlive(){
    return (MakeConstantDigits(0) +
            MakeConstantDigits(Client.getSequenceNumber()-2) +
            MakeConstantDigits(0) +
            "0000" +
            MakeConstantDigits(0) +
            MakeConstantDigits(0) +
            MakeConstantDigits(0) +
            MakeConstantDigits(0) );
  }
  private String createDataMsg(char[] cbuf) {
//    private String createDataMsg(char[] cbuf,int seq, int ack, int window) {
      String text = String.valueOf(cbuf);
      int dataLen =cbuf.length+Client.getSessionID().length();
      return (MakeConstantDigits(dataLen) +
      MakeConstantDigits(Client.getSequenceNumber()) +
      MakeConstantDigits(Client.getServer().getServer_sequenceNumber()) +
      "0000" +
      MakeConstantDigits(Client.getWindowSize()) +
      MakeConstantDigits(0) +
      MakeConstantDigits(0) +
      MakeConstantDigits(0)+text+Client.getSessionID());
  }
  private String createMsgSYN(int seqNum) {
    return (MakeConstantDigits(0) +
          MakeConstantDigits(seqNum) +
          MakeConstantDigits(0) +
          "1000" +
          MakeConstantDigits(Client.getWindowSize()) +
          MakeConstantDigits(Client.getMss()) +
          MakeConstantDigits(Client.getWindowSize()) +
          MakeConstantDigits(Client.getKeepAliveTimeInerval()));
  }
  private String createMsgACK(int seq,int ack,int window) {
    return (MakeConstantDigits(0) +
      MakeConstantDigits(seq) +
      MakeConstantDigits(ack) +
      "0100" +
      MakeConstantDigits(window) +
      MakeConstantDigits(0) +
      MakeConstantDigits(0) +
      MakeConstantDigits(0)+Client.getSessionID());
  }

  public void resend(byte[] msg){
    try{

      System.out.println("resending"+new String(msg));
//      ClientModule.setSentTime(System.currentTimeMillis());
      datagramPacket = new DatagramPacket(msg, msg.length, Client.getServer().getServer_address(), Client.getServer().getServer_port());
      datagramSocket.send(datagramPacket);
    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }
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
  private void MakeLength(int seqNum) throws Exception{
    ByteArrayOutputStream boas = new ByteArrayOutputStream();
    java.io.DataOutputStream dos = new java.io.DataOutputStream(boas);
    dos.writeInt(0);
    System.out.println("SEQ++"+seqNum);
    dos.writeInt(seqNum);
    dos.writeInt(0);
    dos.writeInt(8);
    dos.writeInt(Client.getWindowSize());
    dos.writeInt(Client.getMss());
    dos.writeInt(Client.getWindowSize());
    dos.writeInt(Client.getKeepAliveTimeInerval());
//    dos.writeUTF("HELLO :D");
//    dos.writeUTF("SYN");
    byte[] bytes = boas.toByteArray();

    System.out.println("bytes length========="+bytes.length);
    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
    DataInputStream dis = new DataInputStream(bis);
//    System.out.println(dis.toString());
//    System.out.println(dis.readByte());
    dis.skipBytes(4);
    StringBuilder x =new StringBuilder();
    for(int i=0;i<4;i++){
      int num = dis.read();
      System.out.println(num);
      System.out.println("binary-"+MakeEight(Integer.toBinaryString(num)));
      x.append(MakeEight(Integer.toBinaryString(num)));
    }
    System.out.println(x);
    System.out.println("SEQ NUM==="+Integer.parseInt(x.toString(),2));
//    dis.skipBytes(6*4);
//    System.out.println("msg==="+dis.readUTF());
  }
  private String MakeEight(String str) {
    switch (str.length()) {
      case 1:
        return ("0000000" + str);
      case 2:
        return ("000000" + str);
      case 3:
        return ("00000" + str);
      case 4:
        return ("0000" + str);
      case 5:
        return ("000" + str);
      case 6:
        return ("00" + str);
      case 7:
        return ("0" + str);
      case 8:
        return (str);
      default:
        return (null);
    }
  }
}

