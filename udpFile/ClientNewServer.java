package udpFile;

import java.net.InetAddress;

/**
 * Created by AdminPC on 7/6/2017.
 */
public class ClientNewServer {
  public int server_windowSize;
  public int server_mss;
  public int server_timestamp;
  public int server_sequenceNumber;
  public InetAddress server_address;
  public int server_port;

  ClientNewServer(int _server_port, InetAddress _server_address){
    server_port = _server_port;
    server_address = _server_address;
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
  public int getServer_windowSize(){return server_windowSize;}
  public int getServer_mss(){return server_mss;}
  public int getServer_timestamp(){return server_timestamp;}
  public int getServer_sequenceNumber(){return server_sequenceNumber;}
  public int getServer_port(){return server_port;}
  public InetAddress getServer_address(){
    return server_address;
  }
}
