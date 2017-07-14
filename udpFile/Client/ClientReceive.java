package udpFile.Client;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.math.BigInteger;
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
    clientSend = new ClientSend(datagramSocket);
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
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(replyDatagram.getData(), replyDatagram.getOffset(), replyDatagram.getLength()));
        String msg = new String(replyDatagram.getData(), replyDatagram.getOffset(), replyDatagram.getLength());
        System.out.println("reply from server - "+msg+"eom");
//        System.out.println("reply len- "+msg.length());

        StringBuilder strBuilder = new StringBuilder();

        for(int i=0;i<2;i++){
          strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
        }
        int dataLength =Integer.parseInt(strBuilder.toString(),2);
        System.out.println("DATA lENGTH"+dataLength);
        strBuilder=new StringBuilder();
        for(int i=0;i<4;i++){
          strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
        }
        int serverSequence =Integer.parseInt(strBuilder.toString(),2);
        System.out.println("SERVER SEQ "+serverSequence);
        strBuilder=new StringBuilder();
        for(int i=0;i<4;i++){
          strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
        }
        int clientSequence =Integer.parseInt(strBuilder.toString(),2);
        System.out.println("CLIENT SEQ"+clientSequence);
        strBuilder=new StringBuilder();
        int control = dataInputStream.read();
        System.out.println("control="+control);
        for(int i=0;i<2;i++){
          strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
        }
        int window =Integer.parseInt(strBuilder.toString(),2);
        System.out.println("WINDOW="+window);
        strBuilder=new StringBuilder();
        for(int i=0;i<2;i++){
          strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
        }
        int mss =Integer.parseInt(strBuilder.toString(),2);
        System.out.println("MSS="+mss);
        strBuilder=new StringBuilder();
        for(int i=0;i<2;i++){
          strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
        }
        int timestamp =Integer.parseInt(strBuilder.toString(),2);
        System.out.println("TIMESTAMP"+timestamp);
        strBuilder=new StringBuilder();
        for(int i=0;i<8;i++){
          strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
        }
        System.out.println("session binary = "+strBuilder);
        long sessionID =new BigInteger(strBuilder.toString(), 2).longValue();
        System.out.println("SESSION ID---"+sessionID);


        if(control==12){
          System.out.println("syn ack");

          if((Client.getSequenceNumber()+1)==clientSequence){
//            Client.clearOutgoingBuffer();
            server.setServer_sequenceNumber(serverSequence+1);
            server.setServer_mss(mss);
            server.setServer_timestamp(timestamp);
            server.setServer_windowSize(window);
//            Client.setWindow(server.getServer_windowSize());
            Client.setSequenceNumber(clientSequence);
            Client.setSessionID(String.valueOf(sessionID));
            Client.getServer().isAlive= true;
            datagramSocket.setSoTimeout(server.getServer_timestamp());
            maxResendTimes = clientWaitingTime /server.server_timestamp;

            System.out.println("expecting server seq "+server.getServer_sequenceNumber());
            System.out.println("expecting client seq "+Client.getSequenceNumber());
            //here must send client window
            clientSend.sendACK(server.server_address,server.server_port,clientSequence,server.server_sequenceNumber,Client.getWindowSize());

            Client.setSequenceNumber(Client.getSequenceNumber()+1);
            //once syn-ack received, start sending keepalive
            //but if server does not respond after sometime, stop sending keepalive

            sendProcessedData= new ClientSendProcessedData(datagramSocket);
            sendProcessedData.start();
//            sendProcessedData.join();
//            keepAlive.start();
//            sendFile = new ClientSendFile(datagramSocket);
//            sendFile.send();
//            sendFile.send(client.getSequenceNumber(),server.server_sequenceNumber,client.getServer_windowSize());
//            Client.setSequenceNumber(Client.getSequenceNumber()+1);
          }
//          else{
//            clientSend.resend(Client.getOutgoingBuffer());
//          }


        }else if(control==8){
          System.out.println("syn ");
        }
        else if(msg.substring(18,22).equals("0000")&&msg.substring(12,18).equals("000000")){
          System.out.println("keep alive received from server");

        }
        else if(control==6){
          System.out.println("ack fin ");
          String session =msg.substring(msg.length()-20,msg.length());
          if(session.equals(Client.getSessionID())){
            Client.getServer().isAlive=false;
          }
        }
        else if(control==4){
          System.out.println("ack ");

          if(String.valueOf(sessionID).equals(Client.getSessionID())){
            for(int i =0;i<Client.window.length;i++){
              if(Client.window[i]!=null ){
                ByteArrayInputStream bis = new ByteArrayInputStream(Client.window[i]);
                DataInputStream dis = new DataInputStream(bis);
                StringBuilder temp = new StringBuilder();
                dis.skipBytes(6);
                for(int j=0;j<4;j++){
                  temp.append(MakeEight(Integer.toBinaryString(dis.read())));
                }
                int seqNum =Integer.parseInt(temp.toString(),2);
                if(seqNum==serverSequence){
                  Client.window[i]=null;
                }
              }
            }
            System.out.println("AFTER RECEIVING ACK CLIENT WINDO ENTRIES---:===");
            for(byte[] str : Client.window){
              System.out.println(str);
            }
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
        else if(msg.substring(20,21).equals("1")){
          System.out.println("fin ");
        }
        else if(msg.substring(21,22).equals("1")){
          System.out.println("reset ");
        }

     }catch (Exception e) {
//        e.printStackTrace();
        System.out.println(e.getMessage());
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
//      e.printStackTrace();
      System.out.println(e.getMessage());

    }
  }
  private String MakeEight(String str) {
    switch (str.length()) {
      case 1:
        return ("0000000" + str);
      case 2:
        return ("000000" + str);
      case 3:
        return ("00000" + str);
      case 4:
        return ("0000" + str);
      case 5:
        return ("000" + str);
      case 6:
        return ("00" + str);
      case 7:
        return ("0" + str);
      case 8:
        return (str);
      default:
        return (null);
    }
  }
}
