package udpFile;

import java.net.InetAddress;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class Client {
  public int server_windowSize;
  public int server_mss;
  public int server_timestamp;
  public int server_sequenceNumber;
  public InetAddress server_address;
  public int server_port;
  private int windowSize;
  private int mss;
  private int timestamp;
  private int seqNumber;


  Client(int _mss, int _timestamp,int _window, int _server_port, InetAddress _server_address){
    mss=_mss;
    timestamp=_timestamp;
    windowSize=_window;
    server_port = _server_port;
    server_address = _server_address;
  }

  public void makeConnection(){
    ClientMakeConnection connection = new ClientMakeConnection(this);
    connection.connect();
  }
  public void setServer_sequenceNumber(int seqNum){
    server_sequenceNumber=seqNum;
  }
  public void setServer_windowSize(int windowSize){
    server_windowSize=windowSize;
  }
  public void setServer_mss(int mss){
    server_mss=mss;
  }
  public void setServer_timestamp(int timestamp){
    server_timestamp=timestamp;
  }
  public void setSequenceNumber(int sequenceNumber){
    seqNumber=sequenceNumber;
  }
  public int getSequenceNumber(){
    return seqNumber;
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
