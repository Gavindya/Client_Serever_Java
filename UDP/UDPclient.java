package UDP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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

  UDPclient(int _port,int _bufferSize){
    portToBeConnected = _port;
    bufferSize = _bufferSize;
  }

  public void connect() throws Exception{
    datagramSocket = new DatagramSocket();

    BufferedReader BR = new BufferedReader(new InputStreamReader(System.in));

    InetAddress hostAddr = InetAddress.getLocalHost();
    while (true){
      System.out.println("Enter msg :");
      msg = BR.readLine();
      byte[] msgByteArray = msg.getBytes();

      datagramPacket = new DatagramPacket(msgByteArray,msgByteArray.length,hostAddr, portToBeConnected);
      datagramSocket.send(datagramPacket);
//      buffer to receive incoming data
      byte[] incomingBuffer = new byte[bufferSize];
      DatagramPacket reply = new DatagramPacket(incomingBuffer, incomingBuffer.length);
      datagramSocket.receive(reply);

      byte[] data = reply.getData();
      String s = new String(data, 0, reply.getLength());

      //echo the details of incoming data - client ip : client portToBeConnected - client message
      System.out.println(reply.getAddress().getHostAddress() + " : " + reply.getPort() + " - " + s);

    }
  }
}
