package udpFile;

import java.lang.reflect.Array;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class Client {

  private static int windowSize;
  private static int mss;
  private static int maxWaitingTime;
  private static int seqNumber;
  private static int keepAliveInterval;
  private static ClientServerConfiguration server;
  private static byte[] incomingBuffer;
  private static byte[] outgoingBuffer;
  public static String[] window;
  private static char[][] buffer;
  private static String sessionID;
  public static boolean noData;
//  public static Map<Integer,byte[]> buffer; //seqNum n bytes
//  private static long sentTime;
//  private Queue<String> buffer;

  Client(int _mss, int _keepAliveInterval,int _window, int _server_port, InetAddress _server_address,int _waitingTime,int bufferSize,int numOfElementInWindow){
    mss=_mss;
    maxWaitingTime=_waitingTime;
    keepAliveInterval=_keepAliveInterval;
    windowSize=_window;
    incomingBuffer = new byte[windowSize];
//    buffer = new LinkedList<String>();
    server = new ClientServerConfiguration(_server_port,_server_address);
    outgoingBuffer = new byte[windowSize];
    buffer = new char[bufferSize][];
//    buffer = new HashMap<>();
    window = new String[numOfElementInWindow];
    noData=false;
  }
  public static void setSessionID(String session){
    sessionID = session;
  }
  public static String getSessionID(){
    return sessionID;
  }
  public static void setBuffer(char[] data,int index){
    buffer[index]= data;
  }
  public static char[][] getBuffer(){
    return buffer;
  }
  public static int getBufferSize(){
    return buffer.length;
  }
  public static int getWaitingTime(){
    return maxWaitingTime;
  }

//  public static void setWindow(String element){
//  }
//  public static String[] getWindow(){
//    return window;
//  }
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
//    buffer.put(Integer.parseInt(String.valueOf(msg).substring(12,18)),msg);
    outgoingBuffer=msg;
  }
  public static void currentOutgoingMsg(byte[] msg){
    outgoingBuffer=msg;
  }
  public static byte[] getOutgoingBuffer() {
    return outgoingBuffer;
  }
  public static void clearOutgoingBuffer(int serverSeqNum){
    outgoingBuffer = new byte[windowSize];
//    buffer.remove(serverSeqNum);
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
  public static int getKeepAliveTimeInerval(){
    return keepAliveInterval;
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
//  public void setServer() throws Exception{
//    server = new ClientServerConfiguration(9999,InetAddress.getLocalHost());
//    server.setServer_sequenceNumber(6666);
//    server.setServer_mss(100);
//    server.setServer_timestamp(5000);
//    server.setServer_windowSize(2);
//
//  }
}
