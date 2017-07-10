package udpFile;

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
      int sequenceNumber = (int) (Math.random() * 1000000);
      Client.setSequenceNumber(sequenceNumber);
      String syn = createMsgSYN(sequenceNumber);
      System.out.println("syn"+syn);
      byte[] msgByteArray = syn.getBytes();
      datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length, serverAddr, portNum);
//      Client.setSentTime(System.currentTimeMillis());
//      Client.addOutgoingBuffer(msgByteArray);
      datagramSocket.send(datagramPacket);
//    }catch (Exception ex){
//      System.out.println(ex.getMessage());
//    }

  }
//  clientSend.sendACK(client.server_address,client.server_port,client.getSequenceNumber()+1,client.server_sequenceNumber+1,client.getWindowSize());


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

      String syn = createMsgACK(seq,ack,window);
        System.out.println("ACK for syn-ack "+syn);
      byte[] msgByteArray = syn.getBytes();
      datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length, serverAddr, portNum);
      datagramSocket.send(datagramPacket);
    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }
  }

//  public void sendData(){
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
//  private String createMsgKeepAlive(){
//    return (MakeConstantDigits(0) +
//            MakeConstantDigits(Client.getSequenceNumber()-2) +
//            MakeConstantDigits(0) +
//            "0000" +
//            MakeConstantDigits(0) +
//            MakeConstantDigits(0) +
//            MakeConstantDigits(0) +
//            MakeConstantDigits(0));
//  }
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
}

