package udpFile.Server;

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

  public boolean AcceptClient(DatagramPacket incomingPacket , long sessionID,int serverSequence,int clientSeq) throws Exception{
    ConcurrentMap<String,Map<Integer,ServerNewClient>> pendingClients = server.getPendingClients();
    ConcurrentMap<Integer,ServerNewClient> acceptedClients = server.getConnectedClients();

    InetAddress incomingAddress = incomingPacket.getAddress();
    //check received ack has session id given by server
    if(pendingClients.containsKey(String.valueOf(sessionID))){
      Map<Integer,ServerNewClient> pendingClient = pendingClients.get(String.valueOf(sessionID));
      ServerNewClient clientPreSaved = pendingClient.get(serverSequence-1);
      if(clientPreSaved!=null){
        String clientFile = "src/"+clientSeq+".txt";
        ServerNewClient newClient = new ServerNewClient(serverSequence,clientSeq,clientFile,incomingAddress,
          String.valueOf(sessionID),clientPreSaved.client_windowSize,clientPreSaved.client_mss,
          clientPreSaved.client_timestamp,server);
//        Server.setConnectedClients(serverSequence,newClient);
        //non static method
        server.setConnectedClient(serverSequence,newClient);
        newClient.start();
//        System.out.println("expected server seq = "+serverSequence);
//        System.out.println("current client seq = "+clientSeq);
        server.getPendingClients().remove(String.valueOf(sessionID));
        return true;
      }
      return false;
//      ServerSend serverSend = new ServerSend(server);
//      serverSend.sendACK();

    }else if(acceptedClients.containsKey(serverSequence-1)){
      ServerNewClient tempClient = acceptedClients.get(serverSequence-1);
      if(tempClient.getSessionID().equals(String.valueOf(sessionID))){
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
