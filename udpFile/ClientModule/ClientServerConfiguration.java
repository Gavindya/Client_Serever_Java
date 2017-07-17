package udpFile.ClientModule;

import java.net.InetAddress;

/**
 * Created by AdminPC on 7/6/2017.
 */
public class ClientServerConfiguration {
  private int server_windowSize;
  private int server_mss;
  private int server_timestamp;
  private int server_sequenceNumber;
  private InetAddress server_address;
  private int server_port;
  private long keepAliveTime=4000;
  private boolean isAlive;

  ClientServerConfiguration(int _server_port, InetAddress _server_address){
    server_port = _server_port;
    server_address = _server_address;
  }
  protected void setServer_sequenceNumber(int seqNum){
    server_sequenceNumber=seqNum;
  }

  protected boolean getIsAlive(){
    return isAlive;
  }
  protected InetAddress getServerAddress(){
    return server_address;
  }
  protected int getServerPort(){
    return server_port;
  }
  protected long getkeepAliveTime(){
    return keepAliveTime;
  }
  protected void setIsAlive(boolean alive){
    isAlive=alive;
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
  public long getKeepAliveTime(){
    return keepAliveTime;
  }
}
