package udpFile.ServerModule;

/**
 * Created by AdminPC on 7/6/2017.
 */
public class RunServer {
  public static void main(String[] args) {
//    ServerModule(int _portNum, int _winSize, int _mss, int _timeStamp,int _keepAliveInterval,int _receivingWindowSize){
      Server server = new Server(9999,65000,100,5000,6000,3);
    server.serverUp();
    server.addListener(new ClientListener() {
      public void clientRegistered(ClientRegisterEvent event) {
        if (event.getServerNewClient().client_mss!=0) {
          System.out.println("CLIENT CONNECTED ++++++++++++++++++++++++++");
        }
      }

      public void clientSentData(ClientSentDataEvent event) {
        if (event.getDataStream().length()!=0) {
          System.out.println("");
          System.out.println(event.getDataStream());
          System.out.println("");
        }
      }
    });
  }
}
