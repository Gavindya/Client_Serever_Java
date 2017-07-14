package udpFile.Client;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class ClientSend extends Thread {
//  private Client client;
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
//    System.out.println(Byte.parseByte(Integer.toBinaryString(0), 2));
      dos.write(Byte.parseByte(Integer.toBinaryString(0), 2));
//    System.out.println(Byte.parseByte(Integer.toBinaryString(0), 2));
//    //sequence number - 4 bytes
      dos.writeInt(sequenceNumber);
//     //ack # - 4 bytes
     dos.writeInt(0);
//     //control value - 1 byte
     dos.write(Byte.parseByte(Integer.toBinaryString(8), 2));

    //      //window 2 bytes
    String win1;
    String win2;
    if(Client.getWindowSize()<256) {
      win1 = Integer.toBinaryString(0);
      win2 = Integer.toBinaryString(Client.getWindowSize());
    }else {
      win1 = MakeSixteen(Integer.toBinaryString(Client.getWindowSize())).substring(0,8);
      win2 = MakeSixteen(Integer.toBinaryString(Client.getWindowSize())).substring(8,MakeSixteen(Integer.toBinaryString(Client.getWindowSize())).length());
    }
    dos.write((byte) Integer.parseInt(win1, 2));
    dos.write((byte) Integer.parseInt(win2, 2));
//      //mss 2 bytes
    String mss1;
    String mss2;
    if(Client.getMss()<256){
      mss1 = Integer.toBinaryString(0);
      mss2 = Integer.toBinaryString(Client.getMss());
    }else{
      mss1 = MakeSixteen(Integer.toBinaryString(Client.getMss())).substring(0,8);
      mss2 = MakeSixteen(Integer.toBinaryString(Client.getMss())).substring(8,MakeSixteen(Integer.toBinaryString(Client.getMss())).length());

    }
    dos.write((byte) Integer.parseInt(mss1, 2));
    dos.write((byte) Integer.parseInt(mss2, 2));
//      //timestamp 2 bytes
    String time1;
    String time2;
    if(Client.getKeepAliveTimeInerval()<256) {
      time1 = Integer.toBinaryString(0);
      time2 = Integer.toBinaryString(Client.getKeepAliveTimeInerval());
    }else {
      time1 = MakeSixteen(Integer.toBinaryString(Client.getKeepAliveTimeInerval())).substring(0,8);
      time2 = MakeSixteen(Integer.toBinaryString(Client.getKeepAliveTimeInerval())).substring(8,MakeSixteen(Integer.toBinaryString(Client.getKeepAliveTimeInerval())).length());
    }
    dos.write((byte) Integer.parseInt(time1, 2));
    dos.write((byte) Integer.parseInt(time2, 2));

    byte[] msgByteArray = boas.toByteArray();
    datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length, serverAddr, portNum);
    datagramSocket.send(datagramPacket);

//    ByteArrayInputStream bis = new ByteArrayInputStream(msgByteArray);
//    DataInputStream dis = new DataInputStream(bis);
//    for(int y=0;y<20;y++){
//      System.out.println(dis.read());
//    }
  }
//  clientSend.sendACK(client.server_address,client.server_port,client.getSequenceNumber()+1,client.server_sequenceNumber+1,client.getServer_windowSize());


  //  public void sendKeepAlive(){
//    try{
//      String keepAlive = createMsgKeepAlive();
//      byte[] msgByteArray = keepAlive.getBytes();
//      datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length, Client.getServer().server_address, Client.getServer().server_port);
//      datagramSocket.send(datagramPacket);
//      System.out.println("sending KeepAlive time ="+System.currentTimeMillis());
//    }catch(Exception ex){
//      ex.printStackTrace();
//    }
//  }
  public void sendACK(InetAddress serverAddr, int portNum,int seq,int ack,int window) {
    try{
      byte[] syn = createMsgACK(seq,ack,window);
      datagramPacket = new DatagramPacket(syn, syn.length, serverAddr, portNum);
      datagramSocket.send(datagramPacket);
      System.out.println("SENT ACK");
    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }
  }
    public void sendData(char[] cbuf){
    try{
//      String data = createDataMsg(cbuf,seq,ack,window);
//      for(char[] cbuf : Client.getBuffer()){
        String data = createDataMsg(cbuf);
        byte[] msgByteArray = data.getBytes();
//      Client.addOutgoingBuffer(msgByteArray); //add to outgoing buffer
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
  private byte[] createMsgACK(int seq,int ack,int window) throws Exception{
    ByteArrayOutputStream boas = new ByteArrayOutputStream();
    java.io.DataOutputStream dos = new java.io.DataOutputStream(boas);

//    //data length 2 bytes
    dos.write(Byte.parseByte(Integer.toBinaryString(0), 2));
    dos.write(Byte.parseByte(Integer.toBinaryString(0), 2));
//    //sequence number - 4 bytes
    dos.writeInt(seq);
//     //ack # - 4 bytes
    dos.writeInt(ack);
//     //control value - 1 byte
    dos.write(Byte.parseByte(Integer.toBinaryString(4), 2));
    //      //window 2 bytes
    String win1;
    String win2;
    if(Client.getWindowSize()<256) {
      win1 = Integer.toBinaryString(0);
      win2 = Integer.toBinaryString(window);
    }else {
      win1 = MakeSixteen(Integer.toBinaryString(window)).substring(0,8);
      win2 = MakeSixteen(Integer.toBinaryString(window)).substring(8,MakeSixteen(Integer.toBinaryString(window)).length());
    }
    dos.write((byte) Integer.parseInt(win1, 2));
    dos.write((byte) Integer.parseInt(win2, 2));
//      //mss 2 bytes
    String mss1=Integer.toBinaryString(0);
    String mss2=Integer.toBinaryString(0);

    dos.write((byte) Integer.parseInt(mss1, 2));
    dos.write((byte) Integer.parseInt(mss2, 2));
//      //timestamp 2 bytes
    String time1=Integer.toBinaryString(0);
    String time2=Integer.toBinaryString(0);
    dos.write((byte) Integer.parseInt(time1, 2));
    dos.write((byte) Integer.parseInt(time2, 2));

    dos.writeLong(Long.parseLong(Client.getSessionID()));
    return boas.toByteArray();
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

  public void resend(byte[] msg){
    try{

      System.out.println("resending"+new String(msg));
//      Client.setSentTime(System.currentTimeMillis());
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
