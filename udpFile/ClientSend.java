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

  public void sendSYN(InetAddress serverAddr, int portNum) {
    try {
      int sequenceNumber = (int) (Math.random() * 1000000);
      Client.setSequenceNumber(sequenceNumber);
      String syn = createMsgSYN(sequenceNumber);
      byte[] msgByteArray = syn.getBytes();
      datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length, serverAddr, portNum);
      datagramSocket.send(datagramPacket);
    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }

  }

//  clientSend.sendACK(client.server_address,client.server_port,client.getSequenceNumber()+1,client.server_sequenceNumber+1,client.getWindowSize());
  public void sendACK(InetAddress serverAddr, int portNum,int seq,int ack,int window) {
    try{

      String syn = createMsgACK(seq,ack,window);
      byte[] msgByteArray = syn.getBytes();
      datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length, serverAddr, portNum);
      datagramSocket.send(datagramPacket);
    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }
  }

  public void sendData(char[] cbuf,int seq,int ack,int window){
    try{
      System.out.println(cbuf);
      String data = createDataMsg(cbuf,seq,ack,window);
      byte[] msgByteArray = data.getBytes();
      datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length, Client.getServer().getServer_address(), Client.getServer().getServer_port());
      datagramSocket.send(datagramPacket);
    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }
  }
  private String createDataMsg(char[] cbuf,int seq, int ack, int window) {
    return (MakeConstantDigits(cbuf.length) +
      MakeConstantDigits(Client.getSequenceNumber()) +
      MakeConstantDigits(Client.getServer().getServer_sequenceNumber()) +
      "0000" +
      MakeConstantDigits(Client.getWindowSize()) +
      MakeConstantDigits(0) +
      MakeConstantDigits(0) +
      MakeConstantDigits(0)+cbuf);
  }
  private String createMsgSYN(int seqNum) {
    return (MakeConstantDigits(0) +
          MakeConstantDigits(seqNum) +
          MakeConstantDigits(0) +
          "1000" +
          MakeConstantDigits(Client.getWindowSize()) +
          MakeConstantDigits(Client.getMss()) +
          MakeConstantDigits(Client.getWindowSize()) +
          MakeConstantDigits(Client.getTimestamp()));
  }
  private String createMsgACK(int seq,int ack,int window) {
    return (MakeConstantDigits(0) +
      MakeConstantDigits(seq) +
      MakeConstantDigits(ack) +
      "0100" +
      MakeConstantDigits(window) +
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

