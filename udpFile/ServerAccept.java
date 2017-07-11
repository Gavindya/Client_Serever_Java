package udpFile;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class ServerAccept {

  private Server server;

  ServerAccept(Server _server){
    server = _server;
  }

  public boolean AcceptClient(String clientMsg, DatagramPacket incomingPacket) throws Exception{
    ConcurrentMap<String,Map<Integer,String>> pendingClients = server.getPendingClients();
    ConcurrentMap<Integer,ServerNewClient> acceptedClients = server.getConnectedClients();

    InetAddress incomingAddress = incomingPacket.getAddress();
    //check received ack has session id given by server
    String session = clientMsg.substring((clientMsg.length()-20),clientMsg.length());

    if(pendingClients.containsKey(session)){
      int serverSeq = Integer.parseInt(clientMsg.substring(12,18));
      Map<Integer,String> pendingClient = pendingClients.get(session);
      String clientSYNmsg = pendingClient.get(Integer.parseInt(clientMsg.substring(12,18))-1);

      if(clientSYNmsg!=null){
        int currentClientSeq = Integer.parseInt(clientMsg.substring(6,12));
        String clientFile = "src/"+currentClientSeq+".txt";
        ServerNewClient newClient = new ServerNewClient(serverSeq,clientSYNmsg,currentClientSeq,clientFile,incomingAddress,session);
        newClient.start();
        System.out.println("expected server seq = "+serverSeq);
        System.out.println("current client seq = "+currentClientSeq);
        server.getPendingClients().remove(session);
        return true;
      }
      return false;

//      ServerSend serverSend = new ServerSend(server);
//      serverSend.sendACK();

    }else if(acceptedClients.containsKey(Integer.parseInt(clientMsg.substring(12,18))-1)){
      ServerNewClient tempClient = acceptedClients.get(Integer.parseInt(clientMsg.substring(12,18))-1);
      if(tempClient.getSessionID().equals(session)){
        System.out.println("already accepted client");
        return true;
      }else{
        return false;
      }

    }else{
      System.out.println("discard");
      return false;
    }
//    ServerNewClient newClient = new ServerNewClient();
  }
}
