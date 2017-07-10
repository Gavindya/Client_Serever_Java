package udpFile;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

/**
 * Created by Gavindya Jayawardena on 7/9/2017.
 */
public class ServerProcessIncomingMessage {
    private Server server;
    ServerSend serverSend;
    DatagramSocket socket;
    DatagramPacket incomingPacket;
    ServerReceivedData receivedData;

    ServerProcessIncomingMessage(Server _server,DatagramSocket _socket,DatagramPacket _incomingPacket){
        server=_server;
        socket= _socket;
        incomingPacket = _incomingPacket;
        receivedData = new ServerReceivedData();

    }
    public void processMsg(String msg) throws Exception{
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
            ServerSend serverSend = new ServerSend(server,socket);
            serverSend.sendSYN_ACK(incomingPacket.getAddress(),incomingPacket.getPort(),msg);
        }
        else if(msg.substring(19,20).equals("1")){
            System.out.println("----------------------------");
            System.out.println("client seq "+msg.substring(6,12));
            System.out.println("server seq"+msg.substring(12,18));
            System.out.println("ack ");
            ServerAccept serverAccept = new ServerAccept(server);
            boolean accepted =  serverAccept.AcceptClient(msg);
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

//            for (Map.Entry<Integer, ServerNewClient> entry : server.getConnectedClients().entrySet())
//            {
//              int key = entry.getKey();
//              int value = entry.getValue().client_seqNumber;
//              System.out.println("seq num :"+key+" : client seq num = "+value);
//            }

            int serverSeq = Integer.parseInt(msg.substring(12,18));
            System.out.println("DATA MSG SERVER SEQ + "+serverSeq );
            int clientSeq = Integer.parseInt(msg.substring(6,12));
            System.out.println("DATA MSG CLIENT SEQ + "+clientSeq );

            byte[] bytes = msg.substring(46,msg.length()).getBytes();
            if(Integer.parseInt(msg.substring(0,6))==bytes.length){
                System.out.println("DATA IS CORRECT!");
//                if(server.getConnectedClients().containsKey(serverSeq)){
//                    System.out.println("sequence number is available");
                    for (Map.Entry<Integer, ServerNewClient> entry : server.getConnectedClients().entrySet())
                    {
                        int key = entry.getKey();
                        System.out.println("KEY-->"+key);
                        if(key==serverSeq){
                            ServerNewClient client = entry.getValue();
                            System.out.println("client seq pre saved = "+client.client_seqNumber);
                            if((client.client_seqNumber+1)==clientSeq){
                                client.client_seqNumber++;
                                server.getConnectedClients().remove(key);
                                server.getConnectedClients().put(key+1,client);
//                                receivedData.getData(msg);
                                serverSend.sendACK(incomingPacket.getAddress(),incomingPacket.getPort(),msg);
                                break;
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
    }
}
