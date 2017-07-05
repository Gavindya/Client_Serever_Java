package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by AdminPC on 7/4/2017.
 */
public class ClientReceive extends Thread {
  private int bufferSize;
  private DatagramSocket datagramSocket;

  ClientReceive(DatagramSocket dsock, int buffer){
    datagramSocket=dsock;
    bufferSize=buffer;
  }
  public void run(){
    while (true) {

      try {
        byte[] incomingBuffer = new byte[bufferSize];
        DatagramPacket replyDatagram = new DatagramPacket(incomingBuffer, incomingBuffer.length);
        datagramSocket.receive(replyDatagram);

        String s = new String(replyDatagram.getData(), replyDatagram.getOffset(), replyDatagram.getLength());

        if (s.substring(0, 3).equals("ACK")) {
//            sendNext = true;
          UDPclient.waitingForAckDataBuffer.remove(UDPclient.seqNumber);
          UDPclient.seqNumber++;
          System.out.println(s.substring(3, s.length()));
          UDPclient.ackNumbers.add(Integer.parseInt(s.substring(3, replyDatagram.getLength())));
          System.out.println("server GOT seq# " + Integer.parseInt(s.substring(3, replyDatagram.getLength())));
        } else if(s.substring(0, 3).equals("NAK")) {
          int seqNotReceived =Integer.parseInt(s.substring(3,s.length()));
          ClientSend.SendData(seqNotReceived,UDPclient.waitingForAckDataBuffer.get(seqNotReceived));
        }
        else{

          System.out.println("server MISSED seq# " + s);
//            sendNext = false;
        }
        // String s = new String(data, 0, replyDatagram.getLength());

        //echo the details of incoming data - client ip : client portToBeConnected - client message
        System.out.println(replyDatagram.getAddress().getHostAddress() + " : " + replyDatagram.getPort() + " - " + s);
      } catch (Exception ex) {
        //
      }
    }
  }
}
