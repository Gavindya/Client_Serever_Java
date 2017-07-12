package udpFile;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
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
//
//        StringBuilder xttt = new StringBuilder();
//        for(int i=0;i<2;i++){
//          int num = dis.read();
//          System.out.println(num);
//          System.out.println("binary-"+MakeEight(Integer.toBinaryString(num)));
//          xttt.append(MakeEight(Integer.toBinaryString(num)));
//        }
//        System.out.println(xttt);
//        System.out.println(Integer.parseInt(xttt.toString(),2));

//        System.out.println("bytes length===="+b.length);
//        ByteArrayInputStream bis = new ByteArrayInputStream(b);
//        DataInputStream dis = new DataInputStream(bis);
//        dis.skipBytes(4);
//        StringBuilder x =new StringBuilder();
//        for(int i=0;i<4;i++){
//          int num = dis.read();
//          System.out.println(num);
//          System.out.println("binary-"+MakeEight(Integer.toBinaryString(num)));
//          x.append(MakeEight(Integer.toBinaryString(num)));
//        }
//        System.out.println(x);
//        System.out.println("SEQ NUM==="+Integer.parseInt(x.toString(),2));

        if(control==12){
            System.out.println("----------------------------");
            System.out.println("client seq "+msg.substring(6,12));
            System.out.println("server seq"+msg.substring(12,18));
            System.out.println("syn ack");
        }else if(control==8){
            System.out.println("----------------------------");
            System.out.println("syn ");
            serverSend = new ServerSend(server,socket);
            serverSend.sendSYN_ACK(incomingPacket.getAddress(),incomingPacket.getPort(),msg,clientSequence,serverSequence,dataInputStream);
        }
        else if(control==4){
            System.out.println("----------------------------");
            System.out.println("client seq "+msg.substring(6,12));
            System.out.println("server seq"+msg.substring(12,18));
            System.out.println("ack ");
            ServerAccept serverAccept = new ServerAccept(server);
            boolean accepted =  serverAccept.AcceptClient(msg,incomingPacket);
            System.out.println( " client accepted ? "+accepted);
            for (Map.Entry<Integer, ServerNewClient> entry : server.getConnectedClients().entrySet())
            {
                System.out.println("SERVER SEQ = "+entry.getKey());
                ServerNewClient client = entry.getValue();
                System.out.println("CLIENT SEQ = "+client.client_seqNumber);

            }
//            if(accepted){
//              ServerSend serverSend = new ServerSend(server,socket);
//              serverSend.sendACK(incomingPacket.getAddress(),incomingPacket.getPort(),msg);

//            }
        }
        else if(control==6){
            System.out.println("----------------------------");
            System.out.println("client seq "+msg.substring(6,12));
            System.out.println("server seq"+msg.substring(12,18));
            System.out.println("ack fin ");

        }
        else if(control==2){
            System.out.println("----------------------------");
            System.out.println("client seq "+msg.substring(6,12));
            System.out.println("server seq"+msg.substring(12,18));
            System.out.println("fin ");
            String session = msg.substring(msg.length()-20,msg.length());
            int serverSeq = Integer.parseInt(msg.substring(12,18));
            int clientSeq = Integer.parseInt(msg.substring(6,12));
            for (Map.Entry<Integer, ServerNewClient> entry : server.getConnectedClients().entrySet()) {
              ServerNewClient tempClient = entry.getValue();
//              int currentKey = entry.getKey();
              if(tempClient.getSessionID().equals(session)){
                serverSend = new ServerSend(server,socket);
                serverSend.sendFINack(incomingPacket.getAddress(),incomingPacket.getPort(),serverSeq,(clientSeq+1),tempClient.getSessionID());
//                server.getConnectedClients().remove(currentKey);
                break;
              }
            }
        }
        else if(control==1){
            System.out.println("----------------------------");
            System.out.println("client seq "+msg.substring(6,12));
            System.out.println("server seq"+msg.substring(12,18));
            System.out.println("reset ");
        }
//        else if((control==0)&&msg.substring(12,18).equals("000000")){
//            System.out.println("keep alive received");
//            // serverSend.sendKeepAlive(incomingPacket.getAddress(),incomingPacket.getPort());
//
//        }
        else{
            System.out.println("---DATA MSG RECEIVED !!!!--------");
            System.out.println("client seq "+msg.substring(6,12));
            System.out.println("server seq"+msg.substring(12,18));

            int serverSeq = Integer.parseInt(msg.substring(12,18));
            int clientSeq = Integer.parseInt(msg.substring(6,12));
            String session = msg.substring(msg.length()-20,msg.length());
            byte[] bytes = msg.substring(46,(msg.length()-20)).getBytes();
            if(Integer.parseInt(msg.substring(0,6))==(bytes.length+session.length())){
                  for (Map.Entry<Integer, ServerNewClient> entry : server.getConnectedClients().entrySet())
                  {
                      int key = entry.getKey();
                      System.out.println("KEY-->"+key);
                      if(key==serverSeq){
                          ServerNewClient client = entry.getValue();
                          System.out.println("client seq pre saved = "+client.client_seqNumber);
                          if(session.equals(client.getSessionID())){
                            if((client.client_seqNumber+1)==clientSeq){
//                                System.out.println("client seq matched");
                              serverSend = new ServerSend(server,socket);
                              System.out.println("PROCESS server seq : "+serverSeq+" client seq = "+clientSeq+" IN METH 1");
//                              ServerReceivedData.getData(serverSeq,msg.substring(46,msg.length()));
                              if(client.addData(serverSeq,msg.substring(46,(msg.length()-20)))){
                                serverSend.sendDataACK(incomingPacket.getAddress(),incomingPacket.getPort(),serverSeq,(clientSeq+1),client.getSessionID());
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
            }
            for (Map.Entry<Integer, ServerNewClient> entry : server.getConnectedClients().entrySet())
            {
              int key = entry.getKey();
              int value = entry.getValue().client_seqNumber;
              System.out.println("seq num :"+key+" : client seq num = "+value);
              if(key==serverSeq){
                ServerNewClient client = entry.getValue();
                if(session.equals(client.getSessionID())) {
                  if ((client.client_seqNumber + 1) == clientSeq) {
//                    if(Integer.parseInt(msg.substring(0,6))==(bytes.length+session.length())){
                      serverSend = new ServerSend(server, socket);
                      System.out.println("PROCESS servver seq : "+serverSeq+" client seq = "+clientSeq+" IN METH 2");

  //                    ServerReceivedData.getData(serverSeq,msg.substring(46,msg.length()));
                      if(client.addData(serverSeq,msg.substring(46,(msg.length()-20)))) {
                        serverSend.sendDataACK(incomingPacket.getAddress(), incomingPacket.getPort(), serverSeq, (clientSeq + 1),client.getSessionID());
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
