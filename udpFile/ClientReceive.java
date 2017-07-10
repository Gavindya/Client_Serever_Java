package udpFile;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class ClientReceive extends Thread {

//  private Client client;
  private DatagramSocket datagramSocket;
  private ClientServerConfiguration server;
  ClientSendFile sendFile;
  ClientSend clientSend;
//  ClientSendKeepAlive keepAlive;
  int resendCount =0;//resend calc
  int clientWaitingTime; //maximum waiting time of client
  int maxResendTimes; //number of times to b resend
  ClientSendProcessedData sendProcessedData;
  ClientReceive(ClientServerConfiguration _server, DatagramSocket _datagramSocket) {
//    client = _client;
    datagramSocket = _datagramSocket;
    server = _server;
    clientSend = new udpFile.ClientSend(datagramSocket);
    clientWaitingTime =Client.getWaitingTime();
//    keepAlive=new ClientSendKeepAlive(datagramSocket);
  }

  public void run() {
    System.out.println("running receiving thread");
    try {
      if(server.getServer_timestamp()!=0){
        datagramSocket.setSoTimeout(server.getServer_timestamp());
        maxResendTimes = clientWaitingTime /server.server_timestamp; //maxResendTimes = clientWaitingTime waiting of client / keep alive time interval of server
        System.out.println("server is set");
      }else{
        datagramSocket.setSoTimeout(5000); //only waits for a reply for 1sec when server timestamp is not set
        maxResendTimes = clientWaitingTime /5000;
        System.out.println("server is not set");
      }
      System.out.println("resend count "+resendCount);
      System.out.println("clientWaitingTime "+clientWaitingTime);
      System.out.println("maxResendTimes "+maxResendTimes);
//      System.out.println("set timeout to "+server.getServer_timestamp());

    while (true) {

        System.out.println("in while loop");
        DatagramPacket replyDatagram = new DatagramPacket(Client.getIncomingBuffer(), Client.getIncomingBuffer().length);
      try{
        datagramSocket.receive(replyDatagram);


        String msg = new String(replyDatagram.getData(), replyDatagram.getOffset(), replyDatagram.getLength());
        System.out.println("reply from server - "+msg+"eom");
//        System.out.println("reply len- "+msg.length());

        if(msg.substring(18,19).equals("1")&&msg.substring(19,20).equals("1")){
          System.out.println("----------------------------");
          System.out.println("server seq "+msg.substring(6,12));
          System.out.println("client seq"+msg.substring(12,18));
          System.out.println("syn ack");

          if((Client.getSequenceNumber()+1)==Integer.parseInt(msg.substring(12,18))){
//            Client.clearOutgoingBuffer();
            server.setServer_sequenceNumber(Integer.parseInt(msg.substring(6,12))+1);
            server.setServer_mss(Integer.parseInt(msg.substring(28,34)));
            server.setServer_timestamp(Integer.parseInt(msg.substring(40,46)));
            server.setServer_windowSize(Integer.parseInt(msg.substring(22,28)));
//            Client.setWindow(server.getServer_windowSize());
            Client.setSequenceNumber(Integer.parseInt(msg.substring(12,18)));
            Client.setSessionID(msg.substring(46,msg.length()));

            Client.getServer().isAlive= true;

            datagramSocket.setSoTimeout(server.getServer_timestamp());
            maxResendTimes = clientWaitingTime /server.server_timestamp;

            System.out.println("expecting server seq "+server.getServer_sequenceNumber());
            System.out.println("expecting client seq "+Client.getSequenceNumber());

            clientSend.sendACK(server.server_address,server.server_port,Client.getSequenceNumber(),server.server_sequenceNumber,Client.getWindowSize());

            Client.setSequenceNumber(Client.getSequenceNumber()+1);
            //once syn-ack received, start sending keepalive
            //but if server does not respond after sometime, stop sending keepalive

            sendProcessedData= new ClientSendProcessedData(datagramSocket);
            sendProcessedData.start();
//            sendProcessedData.join();
//            keepAlive.start();
//            sendFile = new ClientSendFile(datagramSocket);
//            sendFile.send();
//            sendFile.send(client.getSequenceNumber(),server.server_sequenceNumber,client.getWindowSize());
//            Client.setSequenceNumber(Client.getSequenceNumber()+1);
          }else{
            clientSend.resend(Client.getOutgoingBuffer());
          }


        }else if(msg.substring(18,19).equals("1")){
          System.out.println("----------------------------");
          System.out.println("server seq "+msg.substring(6,12));
          System.out.println("client seq"+msg.substring(12,18));
          System.out.println("syn ");
        }
        else if(msg.substring(18,22).equals("0000")&&msg.substring(12,18).equals("000000")){
          System.out.println("keep alive received from server");

        }
        else if(msg.substring(19,20).equals("1")){
          System.out.println("----------------------------");
          System.out.println("server seq "+msg.substring(6,12));
          System.out.println("client seq"+msg.substring(12,18));
          System.out.println("ack ");

          int receivedServerSeq = Integer.parseInt(msg.substring(6,12));

          for(int i =0;i<Client.window.length;i++){
            if(Client.window[i]!=null ){
              if(Integer.parseInt(Client.window[i].substring(12,18))==receivedServerSeq){
                Client.window[i]=null;
              }
            }
          }
          System.out.println("AFTER RECEIVING ACK CLIENT WINDO ENTRIES---:===");
          for(String str : Client.window){
            System.out.println(str);
          }
//          if(server.server_sequenceNumber==Integer.parseInt(msg.substring(6,12))){
//
//            Client.clearOutgoingBuffer(server.server_sequenceNumber);//clear buffer
//
//            sendFile.setOffset();
//            server.server_sequenceNumber++;
//            System.out.println("saved client"+Client.getSequenceNumber()+"----- server expected client seq "+msg.substring(12,18));
//
//            sendFile.send();
////              sendFile.send(Client.getSequenceNumber(),server.server_sequenceNumber,Integer.parseInt(msg.substring(22,28)));//win size at end
//
//            Client.setSequenceNumber(Client.getSequenceNumber()+1);
//            System.out.println("client seq now="+Client.getSequenceNumber());
//          }

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

     }catch (Exception e) {
        e.printStackTrace();
//        if(Client.getOutgoingBuffer()!=null){
//          if(resendCount < maxResendTimes) {
//            clientSend.resend(Client.getOutgoingBuffer());
//            resendCount++;
//          }else{
//            Client.getServer().isAlive= false;
//            break;
//          }
//        }
      }
    }
    } catch (SocketException e) {
      e.printStackTrace();

    }
  }
}
