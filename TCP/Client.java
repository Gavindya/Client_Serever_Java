package TCP;

import java.io.*;
import java.net.Socket;

/**
 * Created by AdminPC on 6/26/2017.
 */
public class Client{

  private static String host;
  private static int port;

  Client(String _host, int _port){
    host = _host;
    port= _port;
  }

  public static void main(String[] args) throws Exception {
    Client c = new Client("localhost",555);
    c.run();
  }


  public void run(){
    try{
      System.out.println("First TCP.ClientModule");
      Socket socket = new Socket(host,port);
      PrintStream ps = new PrintStream(socket.getOutputStream());
      ps.println("Hello from 1");

      InputStreamReader ir = new InputStreamReader(socket.getInputStream());
      BufferedReader br = new BufferedReader(ir);

    StringWriter instr = new StringWriter();
    StringWriter sizeStr= new StringWriter();
    int c;
    while ((c=br.read())!=36){
      sizeStr.append((char)c);
    }
      System.out.println(sizeStr);
    int count =0;
    while ( count<Integer.parseInt(sizeStr.toString())) {
      c=br.read();
      instr.append((char) c);
      count++;
    }
      System.out.println(instr);
      //ONLY IF CONNECTION IS CLOSED!
//    while ( (c=br.read())!=-1) {
//      //      count++;
////      System.out.println((char)c);
//      instr.append((char) c);
//    }
//    System.out.println(instr);

      JsonObjects jo = new JsonObjects();

      Employee emp = jo.GetEmployee(instr.toString());
      System.out.println("Has received TCP.Employee with name "+emp.Name);
      /** Close the socket connection. */
    Thread.sleep(10000);
      socket.close();
    }catch (Exception ex){
      System.out.println(ex.getMessage());
    }

  }
}
