package udpFile.ClientModule;

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
  private DatagramSocket datagramSocket;
  private DatagramPacket datagramPacket;
  private Client client;

  ClientSend(DatagramSocket dsocket,Client _client) {
    client = _client;
    datagramSocket = dsocket;
  }

  protected void sendSYN(InetAddress serverAddr, int portNum) throws Exception{
    System.out.println("sending SYN");
    int sequenceNumber = (int) (Math.random() * 1000);
    client.setSequenceNumber(sequenceNumber);
//    private byte[] createMsg(int sequenceNumber,int serverSequence,int control,int windowSize,int mss,int keepAliveTime, long sessionID) {
      byte[] msgByteArray = CreateMessage.createMsg(sequenceNumber,0,8,client.getMaxStreamSize(),client.getMss(),client.getKeepAliveTimeInerval(),0,null);
      if(msgByteArray!=null){
      datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length, serverAddr, portNum);
      datagramSocket.send(datagramPacket);
    }
  }

  protected void sendACK(InetAddress serverAddr, int portNum,int seq,int ack) {
    try{
//    private byte[] createMsg(int sequenceNumber,int serverSequence,int control,int windowSize,int mss,int keepAliveTime, long sessionID) {
      byte[] syn = CreateMessage.createMsg(seq,ack,4,client.getWindow().length,0,0,Long.parseLong(client.getSessionID()),null);
      datagramPacket = new DatagramPacket(syn, syn.length, serverAddr, portNum);
      datagramSocket.send(datagramPacket);
      System.out.println("SENT ACK");
    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }
  }

  protected void sendKeepAlive(){
        try{
          byte[] msgByteArray = CreateMessage.createMsg(client.getSequenceNumber()-2,0,0,0,0,0,0,null);
          if(msgByteArray!=null) {
            datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length, client.getServer().getServer_address(), client.getServer().getServer_port());
            datagramSocket.send(datagramPacket);
          }
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

}

