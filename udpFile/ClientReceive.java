package udpFile;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class ClientReceive extends Thread {

  private Client client;
  private DatagramSocket datagramSocket;

  ClientReceive(Client _client, DatagramSocket _datagramSocket) {
    client = _client;
    datagramSocket = _datagramSocket;
  }

  public void run() {
    while (true) {
      try {
        byte[] incomingBuffer = new byte[client.getWindowSize()];
        DatagramPacket replyDatagram = new DatagramPacket(incomingBuffer, incomingBuffer.length);
        datagramSocket.receive(replyDatagram);
        String msg = new String(replyDatagram.getData(), replyDatagram.getOffset(), replyDatagram.getLength());
        System.out.println("reply from server - "+msg);
        System.out.println("reply len- "+msg.length());

        if(msg.substring(18,19).equals("1")&&msg.substring(19,20).equals("1")){
          System.out.println("syn ack");

          client.setServer_sequenceNumber(Integer.parseInt(msg.substring(6,12)));
          client.setServer_mss(Integer.parseInt(msg.substring(28,34)));
          client.setServer_timestamp(Integer.parseInt(msg.substring(40,46)));
          client.setServer_windowSize(Integer.parseInt(msg.substring(22,28)));

          ClientSend clientSend = new udpFile.ClientSend(client,datagramSocket);
          clientSend.sendACK(client.server_address,client.server_port,client.getSequenceNumber()+1,client.server_sequenceNumber+1,client.getWindowSize());

        }else if(msg.substring(18,19).equals("1")){
          System.out.println("syn ");
        }
        else if(msg.substring(19,20).equals("1")){
          System.out.println("ack ");
        }
        else if(msg.substring(19,20).equals("1") && msg.substring(20,21).equals("1")){
          System.out.println("ack fin ");
        }
        else if(msg.substring(20,21).equals("1")){
          System.out.println("fin ");
        }
        else if(msg.substring(21,22).equals("1")){
          System.out.println("reset ");
        }
      } catch (Exception ex) {
        //
      }
    }
  }
}
