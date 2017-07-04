package UDP;

import org.apache.commons.lang.ArrayUtils;
import java.net.*;
import java.util.*;

/**
 * Created by AdminPC on 6/27/2017.
 */
public class UDPserver {
  private static int port;
  private static int bufferSize;
//  private static ArrayList<Integer> sessions;
  private static Map<Integer,String> seqNumbers;
  private static int activeSession;
  private long time;

  UDPserver(int _port,int _bufferSize){
    port=_port;
    bufferSize = _bufferSize;
    time = System.currentTimeMillis();
  }

  public static void main(String[] args) throws Exception{
    UDPserver server = new UDPserver(7777,65507);
    server.up();
  }

  public void up() throws Exception{
      DatagramSocket socket;
      //1. creating a server socket, parameter is local port number
      socket = new DatagramSocket(port);
      //buffer to receive incoming data
      byte[] incomingBuffer = new byte[bufferSize];
       DatagramPacket incomingPacket = new DatagramPacket(incomingBuffer,incomingBuffer.length);

    // buffer without specifying size
//      List<Byte> incomingBuffer = new ArrayList<Byte>();
//      DatagramPacket incomingPacket = new DatagramPacket(ArrayUtils.toPrimitive(incomingBuffer.toArray(new Byte[incomingBuffer.size()])), incomingBuffer.size());
      //2. Wait for an incoming data
      System.out.println("UDP.Server is Up");
      //communication loop
      while(true) {
        try {
          socket.setSoTimeout(5000);

          socket.receive(incomingPacket);

//        System.out.println("Incoming from:"+incomingPacket.getAddress()+":"+incomingPacket.getPort());
//        System.out.println("size = "+incomingPacket.getLength());

//        byte[] data = incomingPacket.getData();
          String msg = new String(incomingPacket.getData(), incomingPacket.getOffset(), incomingPacket.getLength());
          System.out.println("received-->" + msg);
//        System.out.println("Session ID = "+Integer.parseInt(msg.substring(0,6)));
          if (activeSession != Integer.parseInt(msg.substring(0, 6))) {
            activeSession = Integer.parseInt(msg.substring(0, 6));
            seqNumbers = new HashMap<Integer, String>();
          }
          int seqNum = Integer.parseInt(msg.substring(11, 17));
          int msgLen = Integer.parseInt(msg.substring(22, 28));
          int originalLength = 33 + msgLen;
//        System.out.println("original msg length = "+originalLength);
          if (originalLength == msg.length()) {
            String data = msg.substring(33, (33 + msgLen));
            seqNumbers.put(seqNum, data);
            System.out.println(data);
            System.gc();

//        msg = "Received : " + msg;
            byte[] reply = ("ACK" + msg.substring(11, 17)).getBytes();
            InetAddress outgoingAddress = incomingPacket.getAddress();
            int outgoingPort = incomingPacket.getPort();

            DatagramPacket outgoingDatagram = new DatagramPacket(reply, reply.length, outgoingAddress, outgoingPort);
            socket.send(outgoingDatagram);
          } else {
            byte[] reply = ("NAK" + msg.substring(11, 17)).getBytes();
            InetAddress outgoingAddress = incomingPacket.getAddress();
            int outgoingPort = incomingPacket.getPort();

            DatagramPacket outgoingDatagram = new DatagramPacket(reply, reply.length, outgoingAddress, outgoingPort);
            socket.send(outgoingDatagram);

          }

        } catch (SocketTimeoutException ex) {
          byte[] reply = "Timeout".getBytes();
          InetAddress outgoingAddress = incomingPacket.getAddress();
          int outgoingPort = incomingPacket.getPort();

          DatagramPacket outgoingDatagram = new DatagramPacket(reply, reply.length, outgoingAddress,outgoingPort);
          socket.send(outgoingDatagram);
        }
      }
  }
}
