package TCP;

import java.net.*;

public class Server {

  private static int port;
//  private static int numOfConnections;

  Server( int _port){
    port= _port;
  }

  public static void main(String[] args) throws Exception {
    Server s = new Server(555);
    s.run();
  }

  public void run() throws Exception{

    ServerSocket serverSocket = new ServerSocket(port);
    System.out.println("TCP.ServerModule Started");
    while (true){
      Socket socket = serverSocket.accept();
      ServerWorker serverWorker = new ServerWorker(socket);
      serverWorker.start();

//      TCP.ServerWorkerThreadPool tpool = new TCP.ServerWorkerThreadPool()


//      InputStreamReader ir = new InputStreamReader(socket.getInputStream());
//      BufferedReader br = new BufferedReader(ir);
//
//      String msg = br.readLine();
//      System.out.println("msg from client-->"+msg);
//
//      if(msg!=null){
//        PrintStream ps = new PrintStream(socket.getOutputStream());
//        ps.print(endCharacter);
//        ps.print(sendJson());
//        ps.print(endCharacter);
//        ps.flush();
//        socket.getOutputStream().flush();
//      }
//
//      Thread.sleep(10000);
    }
  }

//  public String sendJson(){
//    TCP.JsonObjects joClass = new TCP.JsonObjects();
//    JsonObject j_object = joClass.CreateJSON();
//    String msg = joClass.GetJsonString(j_object);
//    return msg;
//  }
}
