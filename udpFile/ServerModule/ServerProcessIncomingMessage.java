package udpFile.ServerModule;

import java.io.DataInputStream;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

/**
 * Created by Gavindya Jayawardena on 7/9/2017.
 */
public class ServerProcessIncomingMessage extends Thread {
    private Server server;
    ServerSend serverSend;
    DatagramSocket socket;
    DatagramPacket incomingPacket;
    DataInputStream dataInputStream;
    String msg;

    ServerProcessIncomingMessage(Server _server,DatagramSocket _socket,DatagramPacket _incomingPacket, String _msg,DataInputStream _dataInputStream){
        server=_server;
        socket= _socket;
        incomingPacket = _incomingPacket;
        msg=_msg;
        dataInputStream=_dataInputStream;

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

    public void run(){
      try{
        StringBuilder str = new StringBuilder();

        for(int i=0;i<2;i++){
          str.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
        }
        int dataLength =Integer.parseInt(str.toString(),2);
        str=new StringBuilder();
        for(int i=0;i<4;i++){
          str.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
        }
        int clientSequence =Integer.parseInt(str.toString(),2);
        str=new StringBuilder();
        for(int i=0;i<4;i++){
          str.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
        }
        int serverSequence =Integer.parseInt(str.toString(),2);
        str=new StringBuilder();
        int control = dataInputStream.read();
        for(int i=0;i<2;i++){
          str.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
        }
        int window =Integer.parseInt(str.toString(),2);
        str=new StringBuilder();
        for(int i=0;i<2;i++){
          str.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
        }
        int mss =Integer.parseInt(str.toString(),2);
        str=new StringBuilder();
        for(int i=0;i<2;i++){
          str.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
        }
        int timestamp =Integer.parseInt(str.toString(),2);
        str=new StringBuilder();
        for(int i=0;i<8;i++){
          String x = MakeEight(Integer.toBinaryString(dataInputStream.read()));
          if(x!=null){
            str.append(x);
          }
        }
        long sessionID=0L;
        if(!str.toString().equals("")){
          sessionID =new BigInteger(str.toString(), 2).longValue();
        }
        String dataPortion="";
        try {
          dataPortion = dataInputStream.readUTF();
        }catch (Exception e){
          e.printStackTrace();
        }
        System.out.println("CURRENT DATA==="+dataPortion);

        if(control==12){
            System.out.println("syn ack");
        }else if(control==8){
            System.out.println("syn ");
            serverSend = new ServerSend(server,socket);
            serverSend.sendSYN_ACK(incomingPacket.getAddress(),incomingPacket.getPort(),clientSequence,window,mss,timestamp);
        }
        else if(control==4){
            System.out.println("ack ");
            ServerAccept serverAccept = new ServerAccept(server);
            boolean accepted =  serverAccept.AcceptClient(incomingPacket,sessionID,serverSequence,clientSequence);
            System.out.println( " client accepted ? "+accepted);
        }
        else if(control==6){
            System.out.println("ack fin ");
        }
        else if(control==2){
            System.out.println("fin ");
            for (Map.Entry<Integer, ServerNewClient> entry : server.getConnectedClients().entrySet()) {
              ServerNewClient tempClient = entry.getValue();
//              int currentKey = entry.getKey();
              if(tempClient.getSessionID().equals(String.valueOf(sessionID))){
                serverSend = new ServerSend(server,socket);
                serverSend.sendFINack(incomingPacket.getAddress(),incomingPacket.getPort(),serverSequence,(clientSequence+1),tempClient.getSessionID());
//                server.getConnectedClients().remove(currentKey);
                break;
              }
            }
        }
        else if(control==1){
            System.out.println("reset ");
        }
//        else if((control==0)&&msg.substring(12,18).equals("000000")){
//            System.out.println("keep alive received");
//            // serverSend.sendKeepAlive(incomingPacket.getAddress(),incomingPacket.getPort());
//
//        }
        else{
            System.out.println("---DATA MSG RECEIVED !!!!--------");

            String session = String.valueOf(sessionID);
//            byte[] bytes = dataPortion.getBytes();
//            if(Integer.parseInt(msg.substring(0,6))==(bytes.length+session.length())){
                  for (Map.Entry<Integer, ServerNewClient> entry : server.getConnectedClients().entrySet())
                  {
                      int key = entry.getKey();
                      System.out.println("KEY-->"+key);
                      if(key==serverSequence){
                          ServerNewClient client = entry.getValue();
                          System.out.println("client seq pre saved = "+client.client_seqNumber);
                          if(session.equals(client.getSessionID())){
                            if((client.client_seqNumber+1)==clientSequence){
//                                System.out.println("client seq matched");
                              serverSend = new ServerSend(server,socket);
                              System.out.println("PROCESS server seq : "+serverSequence+" client seq = "+clientSequence+" IN METH 1");
//                              ServerReceivedData.getData(serverSeq,msg.substring(46,msg.length()));
                              if(client.addData(serverSequence,dataPortion)){
                                serverSend.sendDataACK(incomingPacket.getAddress(),incomingPacket.getPort(),serverSequence,(clientSequence+1),client.getSessionID());
                                client.client_seqNumber++;
//                                client.addToReceivedBuffer(serverSeq,msg.substring(46,msg.length()));
                                server.getConnectedClients().remove(key);
                                server.getConnectedClients().put((key+1),client);
                                break;
                              }

                            }
                          }
                      }
                  }
//            }
            for (Map.Entry<Integer, ServerNewClient> entry : server.getConnectedClients().entrySet())
            {
              int key = entry.getKey();
              int value = entry.getValue().client_seqNumber;
              System.out.println("seq num :"+key+" : client seq num = "+value);
              if(key==serverSequence){
                ServerNewClient client = entry.getValue();
                if(session.equals(client.getSessionID())) {
                  if ((client.client_seqNumber + 1) == clientSequence) {
//                    if(Integer.parseInt(msg.substring(0,6))==(bytes.length+session.length())){
                      serverSend = new ServerSend(server, socket);
                      System.out.println("PROCESS servver seq : "+serverSequence+" client seq = "+clientSequence+" IN METH 2");

  //                    ServerReceivedData.getData(serverSeq,msg.substring(46,msg.length()));
                      if(client.addData(serverSequence,dataPortion)) {
                        serverSend.sendDataACK(incomingPacket.getAddress(), incomingPacket.getPort(), serverSequence, (clientSequence + 1),client.getSessionID());
                        client.client_seqNumber++;
  //                  client.addToReceivedBuffer(serverSeq,msg.substring(46,msg.length()));
                        server.getConnectedClients().remove(key);
                        server.getConnectedClients().put((key + 1), client);
                      }
//                    }
                  }
                }
              }
            }
//            }
//            System.out.println(new String(bytes,"UTF-8"));

//            if(server.getConnectedClients().containsKey(Integer.parseInt(msg.substring(12,18)))){
//                System.out.println(Integer.parseInt(msg.substring(12,18)));
//
//              for (Map.Entry<Integer, ServerNewClient> entry : server.getConnectedClients().entrySet())
//              {
//                int key = entry.getKey();
//                if(key==Integer.parseInt(msg.substring(12,18))){
//                  int clientSeq = Integer.parseInt(msg.substring(6,12));
//                  ServerNewClient clientUpdated = entry.getValue();
//                  clientUpdated.client_seqNumber = clientSeq;
//                  server.getConnectedClients().remove(key);
//                  server.getConnectedClients().put(Integer.parseInt(msg.substring(12,18))+1,clientUpdated);
//                  break;
//                }
//              }
//            }
//            for (Map.Entry<Integer, ServerNewClient> entry : server.getConnectedClients().entrySet())
//            {
//              int key = entry.getKey();
//              int value = entry.getValue().client_seqNumber;
//              System.out.println("seq num :"+key+" : client seq num = "+value);
//            }

//            if(server.getConnectedClients().containsKey((Integer.parseInt(msg.substring(6,12))-1))){
//              System.out.println(":/");
//            }


        }
      }catch (Exception ex){
        ex.printStackTrace();
      }
    }
}
