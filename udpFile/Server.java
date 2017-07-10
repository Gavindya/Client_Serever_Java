package udpFile;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class Server {

  public int port;
  private static int windowSize;
  private int mss;
  private int timestamp;
  private int waitingTime;
  private int keepAliveInterval;
  private static ConcurrentMap<String,Map<Integer,String>> pendingClients; //saving initial seq num given by server and syn from client
  private static ConcurrentMap<Integer,ServerNewClient> connectedClients;

  Server(int _portNum, int _winSize, int _mss, int _timeStamp,int _keepAliveInterval){
    port= _portNum;
    mss=_mss;
    windowSize=_winSize;
    timestamp=_timeStamp;
    pendingClients = new ConcurrentHashMap<String, Map<Integer,String>>();
    connectedClients=new ConcurrentHashMap<Integer, ServerNewClient>();
    keepAliveInterval=_keepAliveInterval;
  }

  public void serverUp(){
    ServerReceive serverReceive = new ServerReceive(this);
    serverReceive.start();

  }

  public int getKeepAliveInterval(){return keepAliveInterval;}

  public static void setPendingClients(int server_sequenceNumber, String syn, String session){
    Map<Integer,String> clientDetails  = new HashMap<Integer, String>();
    clientDetails.put(server_sequenceNumber,syn);
    pendingClients.put(session,clientDetails);
  }
  public ConcurrentMap<String, Map<Integer,String>> getPendingClients(){
    return pendingClients;
  }
  public static int getServer_windowSize(){
    return windowSize;
  }
  public void setServer_windowSize(int window_size){
    windowSize=window_size;
  }
  public void setServer_mss(int _mss){
    mss=_mss;
  }
  public void setServer_timestamp(int _timestamp){
    timestamp=_timestamp;
  }
  public int getWindowSize(){
    return windowSize;
  }
  public int getMss(){
    return mss;
  }
  public int getTimestamp(){
    return timestamp;
  }
  public static void setConnectedClients(int seqNum, ServerNewClient client){
    connectedClients.put(seqNum,client);
  }
  public ConcurrentMap<Integer,ServerNewClient> getConnectedClients(){
    return connectedClients;
  }
}

