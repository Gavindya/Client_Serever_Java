package udpFile;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class ClientReceive extends Thread {

//  private Client client;
  private DatagramSocket datagramSocket;
  private ClientNewServer server;

  ClientReceive(Client _client, ClientNewServer _server, DatagramSocket _datagramSocket) {
//    client = _client;
    datagramSocket = _datagramSocket;
    server = _server;
  }

  public void run() {
    while (true) {
      try {
        DatagramPacket replyDatagram = new DatagramPacket(Client.getIncomingBuffer(), Client.getIncomingBuffer().length);
        datagramSocket.receive(replyDatagram);
        String msg = new String(replyDatagram.getData(), replyDatagram.getOffset(), replyDatagram.getLength());
        System.out.println("reply from server - "+msg);
        System.out.println("reply len- "+msg.length());

        if(msg.substring(18,19).equals("1")&&msg.substring(19,20).equals("1")){
          System.out.println("syn ack");

          server.setServer_sequenceNumber(Integer.parseInt(msg.substring(6,12))+1);
          server.setServer_mss(Integer.parseInt(msg.substring(28,34)));
          server.setServer_timestamp(Integer.parseInt(msg.substring(40,46)));
          server.setServer_windowSize(Integer.parseInt(msg.substring(22,28)));
          Client.setSequenceNumber(Integer.parseInt(msg.substring(12,18)));

          System.out.println("expecting server seq "+server.getServer_sequenceNumber());
          System.out.println("expecting client seq "+Client.getSequenceNumber());

          ClientSend clientSend = new udpFile.ClientSend(datagramSocket);
          clientSend.sendACK(server.server_address,server.server_port,Client.getSequenceNumber(),server.server_sequenceNumber,Client.getWindowSize());

          Client.setSequenceNumber(Client.getSequenceNumber()+1);

        }else if(msg.substring(18,19).equals("1")){
          System.out.println("syn ");
        }
        else if(msg.substring(19,20).equals("1")){
          System.out.println("ack ");
          if(Integer.parseInt(msg.substring(6,12))==server.getServer_sequenceNumber()){
            server.setServer_sequenceNumber(server.getServer_sequenceNumber()+1);

            ClientSendFile sendFile = new ClientSendFile(datagramSocket);
            sendFile.send(Client.getSequenceNumber(),server.server_sequenceNumber,Client.getWindowSize());
//            sendFile.send(client.getSequenceNumber(),server.server_sequenceNumber,client.getWindowSize());
            Client.setSequenceNumber(Client.getSequenceNumber()+1);

          }else{
            System.out.println("discarded");
          }

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
