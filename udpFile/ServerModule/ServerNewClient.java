package udpFile.ServerModule;

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
  private Server server;
  ReceivedStream receivedStream ;

  ServerNewClient(int server_seqNum,int clientSeq, String filePath, InetAddress clientAddress,
                  String _sessionID,int window,int mss,int timestamp,Server _server) throws Exception{
    address = clientAddress;
    incomingBuffer = new byte[Server.getServer_windowSize()];
    client_seqNumber=clientSeq;
    client_windowSize =window;
    client_mss=mss;
    client_timestamp=timestamp;
    server_seqNumber = server_seqNum;
    server=_server;
    sessionID = _sessionID;
    if(!filePath.equals("")) {
      receivedData = new ServerReceivedData(filePath);
    }
    receivedStream= new ReceivedStream(server);
  }

  private String MakeEight(String str) {
    switch (str.length()) {
      case 1:
        return ("0000000" + str);
      case 2:
        return ("000000" + str);
      case 3:
        return ("00000" + str);
      case 4:
        return ("0000" + str);
      case 5:
        return ("000" + str);
      case 6:
        return ("00" + str);
      case 7:
        return ("0" + str);
      case 8:
        return (str);
      default:
        return (null);
    }
  }

  public String getSessionID(){
    return sessionID;
  }
  public InetAddress getAddress(){
    return address;
  }
  public boolean addData(int seqNum, String data){
    try {
      return receivedData.getData(seqNum, data);
    }catch (Exception e){
      e.printStackTrace();
      return false;
    }
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
//      System.out.println("ServerModule seqNum = "+server_seqNumber);
      System.out.println("client started");
      while (true){
        //even if receiving window is not full, after 5 seconds, clear the window
        if(receivedData.receivedDataMap.size() == Server.getReceivingWindowSize()){
//      if(receivedData.received.size() == ServerModule.getReceivingWindowSize() || ((System.currentTimeMillis()-receivedData.getDataLastReceivedTime())>client_timestamp)){

          receivedStream.setReceived(receivedData.receivedDataMap);
          receivedData.receivedDataMap=new ConcurrentHashMap<Integer, String>();
        }
//        else if((System.currentTimeMillis()-receivedData.getDataLastReceivedTime())>5000){
//          receivedStream.setReceived(receivedData.receivedDataMap);
//        }
      }
    }catch (Exception ex){
      ex.printStackTrace();
    }
  }

}
