package udpFile.ClientModule;

import java.net.InetAddress;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class Client {

  private int maxStreamSize;
  private int mss;
  private int maxWaitingTime;
  private int seqNumber;
  private int keepAliveInterval;
  private ClientServerConfiguration server;
  private byte[][] window;
  private byte[][] buffer;
  private String sessionID;
  protected boolean noData;
  private Window clientWindow;
  private Buffer clientBuffer;

  Client(int _mss, int _keepAliveInterval,int _window, int _server_port, InetAddress _server_address,int _waitingTime,int bufferSize,int numOfElementInWindow){
    mss=_mss;
    maxWaitingTime=_waitingTime;
    keepAliveInterval=_keepAliveInterval;
    maxStreamSize =_window;
    server = new ClientServerConfiguration(_server_port,_server_address);
    buffer = new byte[bufferSize][];
    window = new byte[numOfElementInWindow][];
    noData=false;
    clientBuffer=new Buffer(bufferSize);
    clientWindow=new Window(numOfElementInWindow);
  }
  protected Buffer getClientBuffer(){
    return clientBuffer;
  }
  protected Window getClientWindow(){
    return clientWindow;
  }
  protected void setSessionID(String session){
    sessionID = session;
  }
  protected String getSessionID(){
    return sessionID;
  }
    public void setBuffer(byte[] data,int index){
      buffer[index]= data;
  }
  protected byte[][] getBuffer(){
  return buffer;
  }
  public int getBufferSize(){
    return buffer.length;
  }
  protected int getWaitingTime(){
    return maxWaitingTime;
  }

  protected void makeConnection(){
    ClientMakeConnection connection = new ClientMakeConnection(this,server);
    connection.connect();
  }

  protected void setSequenceNumber(int sequenceNumber){
    seqNumber=sequenceNumber;
  }
  protected int getSequenceNumber(){
    return seqNumber;
  }
  protected int getMaxStreamSize(){
    return maxStreamSize;
  }
  protected int getMss(){
    return mss;
  }
  protected int getKeepAliveTimeInerval(){
    return keepAliveInterval;
  }
  protected ClientServerConfiguration getServer(){
    return server;
  }
  protected void send(byte[] data){
    try{
      ClientProcessData clientProcessData= new ClientProcessData(data,this);
      clientProcessData.start();
    }catch (Exception e){
      e.printStackTrace();
    }
  }
  protected byte[][] getWindow(){
    return window;
  }
  protected void setWindow(int index, byte[] data){
    window[index]=data;
  }
}
