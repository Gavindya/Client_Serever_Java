package udpFile;

import java.util.concurrent.ConcurrentMap;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class ServerAccept {

  private Server server;

  ServerAccept(Server _server){
    server = _server;
  }

  public boolean AcceptClient(String clientMsg){
    ConcurrentMap<Integer,String> pendingClients = server.getPendingClients();
    ConcurrentMap<Integer,ServerNewClient> acceptedClients = server.getConnectedClients();

    if(pendingClients.containsKey(Integer.parseInt(clientMsg.substring(12,18))-1)){

      int serverSeq = Integer.parseInt(clientMsg.substring(12,18));
      String clientSYNmsg = pendingClients.get(Integer.parseInt(clientMsg.substring(12,18))-1);
      int currentClientSeq = Integer.parseInt(clientMsg.substring(6,12));
      ServerNewClient newClient = new ServerNewClient(serverSeq,clientSYNmsg,currentClientSeq);
      newClient.start();
      System.out.println("expected server seq = "+serverSeq);
      System.out.println("current client seq = "+currentClientSeq);
      server.getPendingClients().remove(Integer.parseInt(clientMsg.substring(12,18))-1);
      return true;

//      ServerSend serverSend = new ServerSend(server);
//      serverSend.sendACK();

    }else if(acceptedClients.containsKey(Integer.parseInt(clientMsg.substring(12,18))-1)){
      System.out.println("already accepted client");
      return true;
    }else{
      System.out.println("discard");
      return false;
    }
//    ServerNewClient newClient = new ServerNewClient();
  }
}
