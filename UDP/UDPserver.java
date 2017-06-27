package UDP;
import java.io.*;
import java.net.*;
/**
 * Created by AdminPC on 6/27/2017.
 */
public class UDPserver {
  private static int port;
  private static int bufferSize;

  UDPserver(int _port,int _bufferSize){
    port=_port;
    bufferSize = _bufferSize;
  }

  public static void main(String[] args) throws Exception{
    UDPserver server = new UDPserver(7777,1024);
    server.up();
  }

  public void up() throws Exception{
    DatagramSocket socket;
      //1. creating a server socket, parameter is local port number
      socket = new DatagramSocket(port);
      //buffer to receive incoming data
      byte[] incomingBuffer = new byte[bufferSize];
      DatagramPacket incomingPacket = new DatagramPacket(incomingBuffer, incomingBuffer.length);
      //2. Wait for an incoming data
      System.out.println("Server is Up");

      //communication loop
      while(true)
      {
        socket.receive(incomingPacket);
        System.out.println("Incoming Address :"+incomingPacket.getAddress());
        System.out.println("Incoming Port :"+incomingPacket.getPort());
        byte[] data = incomingPacket.getData();
        String msg = new String(data,0,data.length);
        System.out.println(msg);

        msg = "Received : " + msg;
        byte[] reply = msg.getBytes();
        InetAddress outgoingAddress = incomingPacket.getAddress();
        int outgoingPort = incomingPacket.getPort();

        DatagramPacket outgoingDatagram = new DatagramPacket(reply , reply.length , outgoingAddress,outgoingPort );
        socket.send(outgoingDatagram);
      }

  }
}
