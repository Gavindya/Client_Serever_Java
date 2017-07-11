package udpFile;

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
     String msg;


    ServerProcessIncomingMessage(Server _server,DatagramSocket _socket,DatagramPacket _incomingPacket, String _msg){
        server=_server;
        socket= _socket;
        incomingPacket = _incomingPacket;
        msg=_msg;

    }
    public void run(){
      try{

        if(msg.substring(18,19).equals("1")&&msg.substring(19,20).equals("1")){
            System.out.println("----------------------------");
            System.out.println("client seq "+msg.substring(6,12));
            System.out.println("server seq"+msg.substring(12,18));
            System.out.println("syn ack");
        }else if(msg.substring(18,19).equals("1")){
            System.out.println("----------------------------");
            System.out.println("client seq "+msg.substring(6,12));
            System.out.println("server seq"+msg.substring(12,18));
            System.out.println("syn ");
          serverSend = new ServerSend(server,socket);
            serverSend.sendSYN_ACK(incomingPacket.getAddress(),incomingPacket.getPort(),msg);
        }
        else if(msg.substring(19,20).equals("1")){
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
        else if(msg.substring(19,20).equals("1") && msg.substring(20,21).equals("1")){
            System.out.println("----------------------------");
            System.out.println("client seq "+msg.substring(6,12));
            System.out.println("server seq"+msg.substring(12,18));
            System.out.println("ack fin ");

        }
        else if(msg.substring(20,21).equals("1")){
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
        else if(msg.substring(21,22).equals("1")){
            System.out.println("----------------------------");
            System.out.println("client seq "+msg.substring(6,12));
            System.out.println("server seq"+msg.substring(12,18));
            System.out.println("reset ");
        }
        else if(msg.substring(18,22).equals("0000")&&msg.substring(12,18).equals("000000")){
            System.out.println("keep alive received");
            // serverSend.sendKeepAlive(incomingPacket.getAddress(),incomingPacket.getPort());

        }
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
