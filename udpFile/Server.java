package udpFile;

import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class Server {

  public InetAddress server_address;
  public int port;
  private int windowSize;
  private int mss;
  private int timestamp;
  private ConcurrentMap<Integer,String> pendingClients;

  Server(int _portNum, int _winSize, int _mss, int _timeStamp){
    port= _portNum;
    mss=_mss;
    windowSize=_winSize;
    timestamp=_timeStamp;
    pendingClients = new ConcurrentHashMap<Integer, String>();
  }

  public void serverUp(){
    ServerReceive serverReceive = new ServerReceive(this);
    serverReceive.start();
  }

  public void setPendingClients(int server_sequenceNumber,String syn){
    pendingClients.put(server_sequenceNumber,syn);
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
}

