package udpFile.ServerModule;

import java.net.InetAddress;
import java.util.*;
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
  private int keepAliveInterval;
  private static ConcurrentMap<String,Map<Integer,ServerNewClient>> pendingClients; //saving sessionID, initial seq num given by server and syn from client
  private static ConcurrentMap<Integer,ServerNewClient> connectedClients;
  private static int receivingWindowSize;
  private List _listeners = new ArrayList();

  Server(int _portNum, int _winSize, int _mss, int _timeStamp,int _keepAliveInterval,int _receivingWindowSize){
    port= _portNum;
    mss=_mss;
    windowSize=_winSize;
    timestamp=_timeStamp;
    pendingClients = new ConcurrentHashMap<String, Map<Integer,ServerNewClient>>();
    connectedClients=new ConcurrentHashMap<Integer, ServerNewClient>();
    keepAliveInterval=_keepAliveInterval;
    receivingWindowSize=_receivingWindowSize;
  }

  public void serverUp(){
    ServerReceive serverReceive = new ServerReceive(this);
    serverReceive.start();

  }

  public int getKeepAliveInterval(){return keepAliveInterval;}

  public void setPendingClients(int server_sequenceNumber, String session, int window, int mss, int timestamp, InetAddress clientAddr,int clientSequence){
    try{
      Map<Integer,ServerNewClient> clientDetails  = new HashMap<Integer, ServerNewClient>();
      ServerNewClient tempClient =new ServerNewClient(server_sequenceNumber,clientSequence,"",clientAddr,session,window,mss,timestamp,this);
      clientDetails.put(server_sequenceNumber,tempClient);
      pendingClients.put(session,clientDetails);
    }catch (Exception ex){
      ex.printStackTrace();
    }

  }
  public static int getReceivingWindowSize(){
    return receivingWindowSize;
  }
  public ConcurrentMap<String, Map<Integer,ServerNewClient>> getPendingClients(){
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
  public int getMss(){
    return mss;
  }
  public int getTimestamp(){
    return timestamp;
  }
  public synchronized static void setConnectedClients(int seqNum, ServerNewClient client){
    connectedClients.put(seqNum,client);
  }
  public synchronized void setConnectedClient(int seqNum, ServerNewClient client){
    connectedClients.put(seqNum,client);
    _fireClientRegisteredEvent(client);
  }
  public ConcurrentMap<Integer,ServerNewClient> getConnectedClients(){
    return connectedClients;
  }

  public StringBuilder getReceivedData(int key){
    ServerNewClient client = connectedClients.get(key);
    return client.getMessageBuilt();

  }
  public synchronized void addListener(ClientListener listener ){
    _listeners.add(listener);

  }
  public synchronized void removeListener( ClientListener listener ) {
    _listeners.remove( listener );
  }

  private synchronized void _fireClientRegisteredEvent( ServerNewClient client) {
    System.out.println("*********EVENT FIRED********");
    ClientRegisterEvent clientRegisterEvent= new ClientRegisterEvent( this, client);
    for (Object _listener : _listeners) {
      ((ClientListener) _listener).clientRegistered(clientRegisterEvent);
    }
  }
  public synchronized void _fireDataReceivedEvent(String dataStream){
    ClientSentDataEvent clientSentDataEvent = new ClientSentDataEvent( this, dataStream);
    for (Object _listener : _listeners) {
      ((ClientListener) _listener).clientSentData(clientSentDataEvent);
    }
  }
}

