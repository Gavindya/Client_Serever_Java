package udpFile.ClientModule;

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

  private DatagramSocket datagramSocket;
  private ClientServerConfiguration server;
  ClientSend clientSend;
//  ClientSendKeepAlive keepAlive;
  int clientWaitingTime; //maximum waiting time of client
  int maxResendTimes; //number of times to b resend
  ClientSendProcessedData sendProcessedData;
  private Client client;
  private ClientProcessMessagePool threadPool;

  ClientReceive(ClientServerConfiguration _server, DatagramSocket _datagramSocket,Client _client) {
    client = _client;
    datagramSocket = _datagramSocket;
    server = _server;
    clientSend = new ClientSend(datagramSocket,client);
    clientWaitingTime =client.getWaitingTime();
    threadPool = new ClientProcessMessagePool(20);
//    keepAlive=new ClientSendKeepAlive(datagramSocket);
  }

  public void run() {
    try {
      if(server.getServer_timestamp()!=0){
        datagramSocket.setSoTimeout(server.getServer_timestamp());
        maxResendTimes = clientWaitingTime /server.getServer_timestamp(); //maxResendTimes = clientWaitingTime waiting of client / keep alive time interval of server
//        System.out.println("server is set");
      }else{
        datagramSocket.setSoTimeout(5000); //only waits for a reply for 1sec when server timestamp is not set
        maxResendTimes = clientWaitingTime /5000;
//        System.out.println("server is not set");
      }

    while (true) {
        System.out.println("in receiving loop");
        byte[] incomingBuffer = new byte[client.getMaxStreamSize()];
        DatagramPacket replyDatagram = new DatagramPacket(incomingBuffer, incomingBuffer.length);
      try{
        datagramSocket.receive(replyDatagram);
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(replyDatagram.getData(), replyDatagram.getOffset(), replyDatagram.getLength()));
        String msg = new String(replyDatagram.getData(), replyDatagram.getOffset(), replyDatagram.getLength());
        System.out.println("reply from server - "+msg+"eom");

        threadPool.addWork(new ClientProcessIncomingMessage(dataInputStream,client,server,datagramSocket,maxResendTimes,clientSend));

//        StringBuilder strBuilder = new StringBuilder();
//
//        for(int i=0;i<2;i++){
//          strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
//        }
//        int dataLength =Integer.parseInt(strBuilder.toString(),2);
////        System.out.println("DATA lENGTH"+dataLength);
//        strBuilder=new StringBuilder();
//        for(int i=0;i<4;i++){
//          strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
//        }
//        int serverSequence =Integer.parseInt(strBuilder.toString(),2);
////        System.out.println("SERVER SEQ "+serverSequence);
//        strBuilder=new StringBuilder();
//        for(int i=0;i<4;i++){
//          strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
//        }
//        int clientSequence =Integer.parseInt(strBuilder.toString(),2);
////        System.out.println("CLIENT SEQ"+clientSequence);
//        strBuilder=new StringBuilder();
//        int control = dataInputStream.read();
////        System.out.println("control="+control);
//        for(int i=0;i<2;i++){
//          strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
//        }
//        int window =Integer.parseInt(strBuilder.toString(),2);
////        System.out.println("WINDOW="+window);
//        strBuilder=new StringBuilder();
//        for(int i=0;i<2;i++){
//          strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
//        }
//        int mss =Integer.parseInt(strBuilder.toString(),2);
////        System.out.println("MSS="+mss);
//        strBuilder=new StringBuilder();
//        for(int i=0;i<2;i++){
//          strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
//        }
//        int timestamp =Integer.parseInt(strBuilder.toString(),2);
////        System.out.println("TIMESTAMP"+timestamp);
//        strBuilder=new StringBuilder();
//        for(int i=0;i<8;i++){
//          strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
//        }
//        long sessionID =new BigInteger(strBuilder.toString(), 2).longValue();
////        System.out.println("SESSION ID---"+sessionID);
//
//
//        if(control==12){
//          System.out.println("syn ack");
//
//          if((client.getSequenceNumber()+1)==clientSequence){
//            server.setServer_sequenceNumber(serverSequence+1);
//            server.setServer_mss(mss);
//            server.setServer_timestamp(timestamp);
//            server.setServer_windowSize(window);
//
//            client.setSequenceNumber(clientSequence);
//            client.setSessionID(String.valueOf(sessionID));
//            client.getServer().isAlive= true;
//            datagramSocket.setSoTimeout(server.getServer_timestamp());
//            maxResendTimes = clientWaitingTime /server.server_timestamp;
//
//            System.out.println("expecting server seq "+server.getServer_sequenceNumber());
//            System.out.println("expecting client seq "+client.getSequenceNumber());
//            //here must send client window
//            clientSend.sendACK(server.server_address,server.server_port,clientSequence,server.server_sequenceNumber);
//
//            client.setSequenceNumber(client.getSequenceNumber()+1);
//            //once syn-ack received, start sending keepalive
//            //but if server does not respond after sometime, stop sending keepalive
//
//            sendProcessedData= new ClientSendProcessedData(datagramSocket,client);
//            sendProcessedData.start();
////            sendProcessedData.join();
////            keepAlive.start();
////            sendFile = new ClientSendFile(datagramSocket);
////            sendFile.send();
////            sendFile.send(client.getSequenceNumber(),server.server_sequenceNumber,client.getServer_windowSize());
////            ClientModule.setSequenceNumber(ClientModule.getSequenceNumber()+1);
//          }
//        }else if(control==8){
//          System.out.println("syn ");
//        }
//        else if(control==8&&msg.substring(12,18).equals("000000")){
//          System.out.println("keep alive received from server");
//        }
//        else if(control==6){
//          System.out.println("ack fin ");
//          String session =msg.substring(msg.length()-20,msg.length());
//          if(session.equals(client.getSessionID())){
//            client.getServer().isAlive=false;
//          }
//        }
//        else if(control==4){
//          System.out.println("ack ");
//
//          if(String.valueOf(sessionID).equals(client.getSessionID())){
//            for(int i =0;i<client.getWindow().length;i++){
//              if(client.getWindow()[i]!=null ){
//                ByteArrayInputStream bis = new ByteArrayInputStream(client.getWindow()[i]);
//                DataInputStream dis = new DataInputStream(bis);
//                StringBuilder temp = new StringBuilder();
//                dis.skipBytes(6);
//                for(int j=0;j<4;j++){
//                  temp.append(MakeEight(Integer.toBinaryString(dis.read())));
//                }
//                int seqNum =Integer.parseInt(temp.toString(),2);
//                if(seqNum==serverSequence){
//                  client.setWindow(i,null);
//                }
//              }
//            }
////            System.out.println("AFTER RECEIVING ACK CLIENT WINDO ENTRIES---:===");
////            for(byte[] str : client.getWindow()){
////              System.out.println(str);
////            }
//          }
//        }
//        else if(msg.substring(20,21).equals("1")){
//          System.out.println("fin ");
//        }
//        else if(msg.substring(21,22).equals("1")){
//          System.out.println("reset ");
//        }

     }catch (Exception e) {
        e.printStackTrace();
//        System.out.println(e.getMessage());
      }
    }

    } catch (SocketException e) {
      e.printStackTrace();
//      System.out.println(e.getMessage());
    }
    threadPool.shutdown();
  }

}
