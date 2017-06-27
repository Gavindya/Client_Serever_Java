package UDP;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by AdminPC on 6/27/2017.
 */
public class UDPclient {
  private static int portToBeConnected;
  private static int bufferSize;
  private static DatagramSocket datagramSocket;
  private String msg;
  private DatagramPacket datagramPacket;
  private String filepath = "src/test1.txt";

  UDPclient(int _port,int _bufferSize){
    portToBeConnected = _port;
    bufferSize = _bufferSize;
  }

  public void connect() throws Exception{
    datagramSocket = new DatagramSocket();

//    BufferedReader BR = new BufferedReader(new InputStreamReader(System.in));

//    InetAddress hostAddr = InetAddress.getLocalHost();
//    while (true){
//      System.out.println("Enter msg :");
//      msg = BR.readLine();
//      byte[] msgByteArray = msg.getBytes();
//
//
//
//      datagramPacket = new DatagramPacket(msgByteArray,msgByteArray.length,hostAddr, portToBeConnected);
//      datagramSocket.send(datagramPacket);
      SendFile();
//      buffer to receive incoming data
      byte[] incomingBuffer = new byte[bufferSize];
      DatagramPacket replyDatagram = new DatagramPacket(incomingBuffer, incomingBuffer.length);
      datagramSocket.receive(replyDatagram);

      byte[] data = replyDatagram.getData();
      String s = new String(data, 0, replyDatagram.getLength());

      //echo the details of incoming data - client ip : client portToBeConnected - client message
      System.out.println(replyDatagram.getAddress().getHostAddress() + " : " + replyDatagram.getPort() + " - " + s);

//    }
  }

  public void SendFile() {
    try{
      System.out.println(filepath);
      BufferedReader reader = new BufferedReader(new FileReader(filepath));
      System.out.println("file found");
      int count = 0;
      int currentChar;
      StringWriter writer = new StringWriter();

      // terminate when eof reached
      while((currentChar = reader.read()) != -1) {
        if(count<bufferSize){
          System.out.println((char)currentChar);

          writer.append((char)currentChar);
          count++;
        }
        if(count==bufferSize){
          System.out.println(writer.toString());
          SendData(writer);
          count=0;
          writer=new StringWriter();
        }
      }
    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }

  }

  public void SendData( StringWriter words ) throws Exception{
    System.out.println("In Send Data meth");
    InetAddress hostAddr = InetAddress.getLocalHost();
    byte[] msgByteArray = words.toString().getBytes();

    datagramPacket = new DatagramPacket(msgByteArray,msgByteArray.length,hostAddr, portToBeConnected);
    datagramSocket.send(datagramPacket);
  }


}
