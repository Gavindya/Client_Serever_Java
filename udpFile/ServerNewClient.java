package udpFile;

import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class ServerNewClient extends Thread{

  public int client_windowSize;
  public int client_mss;
  public int client_timestamp;
  private int server_seqNumber;
  public int client_seqNumber;
  public byte[] incomingBuffer;
  private ServerReceivedData receivedData;
  private InetAddress address;
  private String sessionID;
  ReceivedStream receivedStream ;

  ServerNewClient(int server_seqNum, String clientSYN, int clientSeq, String filePath, InetAddress clientAddress,String _sessionID) throws Exception{
    address = clientAddress;
    incomingBuffer = new byte[Server.getServer_windowSize()];
    client_mss=Integer.parseInt(clientSYN.substring(28,34));
    client_seqNumber=clientSeq;
    client_timestamp=Integer.parseInt(clientSYN.substring(40,46));
    client_windowSize = Integer.parseInt(clientSYN.substring(34,40));
    server_seqNumber = server_seqNum;
    sessionID = _sessionID;
    receivedData = new ServerReceivedData(filePath);
    Server.setConnectedClients(server_seqNum,this);
    receivedStream= new ReceivedStream();

  }

  public String getSessionID(){
    return sessionID;
  }
  public InetAddress getAddress(){
    return address;
  }
  public boolean addData(int seqNum, String data) throws Exception{
    return receivedData.getData(seqNum, data);
  }
  public ServerReceivedData getReceivedData(){
    return receivedData;
  }

  public StringBuilder getMessageBuilt(){
    return receivedStream.getReceived();
  }
  public void run(){
    try{
//      System.out.println("client window = "+client_windowSize);
//      System.out.println("client mss = "+client_mss);
//      System.out.println("client time = "+client_timestamp);
//      System.out.println("client seqNum = "+client_seqNumber);
//      System.out.println("Server seqNum = "+server_seqNumber);
      System.out.println("client started");
      while (true){
        //even if receiving window is not full, after 5 seconds, clear the window
        if(receivedData.receivedDataMap.size() == Server.getReceivingWindowSize() || ((System.currentTimeMillis()-receivedData.getDataLastReceivedTime())>5000)){
//      if(receivedData.received.size() == Server.getReceivingWindowSize() || ((System.currentTimeMillis()-receivedData.getDataLastReceivedTime())>client_timestamp)){

          receivedStream.setReceived(receivedData.receivedDataMap);
          receivedData.receivedDataMap=new ConcurrentHashMap<Integer, String>();
        }
      }
    }catch (Exception ex){
      ex.printStackTrace();
    }
  }

}
