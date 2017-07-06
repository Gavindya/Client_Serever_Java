package udpFile;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class ServerReceive extends Thread{

  private Server server;

  ServerReceive(Server _server){
    server=_server;
  }

  public void run() {

    try {
      DatagramSocket socket = new DatagramSocket(server.port);
      byte[] incomingBuffer = new byte[server.getWindowSize()];
      DatagramPacket incomingPacket = new DatagramPacket(incomingBuffer, incomingBuffer.length);
      System.out.println("UDP.Server is Up");
      while (true) {
        try {
//          socket.setSoTimeout(5000);

          socket.receive(incomingPacket);
          String msg = new String(incomingPacket.getData(), incomingPacket.getOffset(), incomingPacket.getLength());
          System.out.println("received-->" + msg);
          System.out.println(msg.length());

          if(msg.substring(18,19).equals("1")&&msg.substring(19,20).equals("1")){
            System.out.println("syn ack");
          }else if(msg.substring(18,19).equals("1")){
            System.out.println("syn ");
            ServerSend serverSend = new ServerSend(server,socket);
            serverSend.sendSYN_ACK(incomingPacket.getAddress(),incomingPacket.getPort(),msg);
          }
          else if(msg.substring(19,20).equals("1")){
            System.out.println("ack ");
            ServerAccept serverAccept = new ServerAccept(server);
            boolean accepted =  serverAccept.AcceptClient(msg);
            if(accepted){
              ServerSend serverSend = new ServerSend(server,socket);
              serverSend.sendACK(incomingPacket.getAddress(),incomingPacket.getPort(),msg);

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
          }else{

//            for (Map.Entry<Integer, ServerNewClient> entry : server.getConnectedClients().entrySet())
//            {
//              int key = entry.getKey();
//              int value = entry.getValue().client_seqNumber;
//              System.out.println("seq num :"+key+" : client seq num = "+value);
//            }

            byte[] bytes = msg.substring(46,msg.length()).getBytes();
            System.out.println(new String(bytes,"UTF-8"));

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
            ServerSend serverSend = new ServerSend(server,socket);
            serverSend.sendACK(incomingPacket.getAddress(),incomingPacket.getPort(),msg);
          }


        } catch (Exception ex) {
          System.out.println(ex.getMessage());
        }
      }
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
    }
  }
}
