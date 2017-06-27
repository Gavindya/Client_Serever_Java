import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Created by AdminPC on 6/26/2017.
 */
public class ServerWorker implements Runnable {
  private Socket socket;

  ServerWorker(Socket clientSocket) {
    socket = clientSocket;
  }
  public void start(){
    this.run();
  }

  public void run() {
    try {
      InputStreamReader ir = new InputStreamReader(socket.getInputStream());
      BufferedReader br = new BufferedReader(ir);

      String msg = br.readLine();
      System.out.println("msg from client-->" + msg);

      if (msg != null) {
        PrintStream ps = new PrintStream(socket.getOutputStream());
        long size = sendJson().length();
        ps.print(size+"$");
        ps.print(sendJson());
        ps.flush();
        socket.getOutputStream().flush();
//        socket.close();
      }
//      Thread.sleep(3000);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public String sendJson() {
    JsonObjects joClass = new JsonObjects();
    JsonObject j_object = joClass.CreateJSON();
    String msg = joClass.GetJsonString(j_object);
    return msg;
  }
}
