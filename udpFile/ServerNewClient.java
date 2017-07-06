package udpFile;

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

  ServerNewClient(int server_seqNum, String clientSYN, int clientSeq){
    incomingBuffer = new byte[Server.getServer_windowSize()];
    client_mss=Integer.parseInt(clientSYN.substring(28,34));
    client_seqNumber=clientSeq;
    client_timestamp=Integer.parseInt(clientSYN.substring(40,46));
    client_windowSize = Integer.parseInt(clientSYN.substring(34,40));
    server_seqNumber = server_seqNum;
    Server.setConnectedClients(server_seqNum,this);
  }

  public void run(){

    System.out.println("client window = "+client_windowSize);
    System.out.println("client mss = "+client_mss);
    System.out.println("client time = "+client_timestamp);
    System.out.println("client seqNum = "+client_seqNumber);
    System.out.println("Server seqNum = "+server_seqNumber);
    System.out.println("client started");

  }
}
