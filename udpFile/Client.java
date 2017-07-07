package udpFile;

import java.net.InetAddress;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class Client {

  private static int windowSize;
  private static int mss;
  private static int timestamp;
  private static int seqNumber;
  private static int waitingTime;
  private static ClientServerConfiguration server;
  private static byte[] incomingBuffer;
  private static byte[] outgoingBuffer;
//  private static long sentTime;
//  private Queue<String> buffer;

  Client(int _mss, int _timestamp,int _window, int _server_port, InetAddress _server_address,int _waitingTime){
    mss=_mss;
    timestamp=_timestamp;
    waitingTime=_waitingTime;
    windowSize=_window;
    incomingBuffer = new byte[windowSize];
//    buffer = new LinkedList<String>();
    server = new ClientServerConfiguration(_server_port,_server_address);
    outgoingBuffer = new byte[windowSize];

  }
  public static int getWaitingTime(){
    return waitingTime;
  }

  public void makeConnection(){
    ClientMakeConnection connection = new ClientMakeConnection(this,server);
    connection.connect();
  }
  /*public static void setSentTime(long time){
    sentTime = time;
  }
  public static long getSentTime(){
    return sentTime;
  }*/
  public static void addOutgoingBuffer(byte[] msg) {
    outgoingBuffer=msg;
  }
  public static byte[] getOutgoingBuffer() {
    return outgoingBuffer;
  }
  public static void clearOutgoingBuffer(){
    outgoingBuffer = new byte[windowSize];
  }
  public static void setSequenceNumber(int sequenceNumber){
    seqNumber=sequenceNumber;
  }
  public static int getSequenceNumber(){
    return seqNumber;
  }
  public static int getWindowSize(){
    return windowSize;
  }
  public static int getMss(){
    return mss;
  }
  public static int getTimestamp(){
    return timestamp;
  }
  public static ClientServerConfiguration getServer(){
    return server;
  }
  public static byte[] getIncomingBuffer(){
    return incomingBuffer;
  }
//  public void addToBuffer(String message){
//    buffer.add(message);
//  }
//  public Queue<String> getBuffer(){
//    return buffer;
//  }
}
