package udpFile;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class Client {

  private static int windowSize;
  private static int mss;
  private static int timestamp;
  private static int seqNumber;
  private static ClientNewServer server;
  private static byte[] incomingBuffer;
//  private Queue<String> buffer;

  Client(int _mss, int _timestamp,int _window, int _server_port, InetAddress _server_address){
    mss=_mss;
    timestamp=_timestamp;
    windowSize=_window;
    incomingBuffer = new byte[windowSize];
//    buffer = new LinkedList<String>();
    server = new ClientNewServer(_server_port,_server_address);
  }

  public void makeConnection(){
    ClientMakeConnection connection = new ClientMakeConnection(this,server);
    connection.connect();
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
  public static ClientNewServer getServer(){
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
