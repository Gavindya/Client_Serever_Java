import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringWriter;
import java.net.Socket;

/**
 * Created by AdminPC on 6/26/2017.
 */
public class Client2 {

  private static String host;
  private static int port;
  private Employee employee;

  Client2(String _host, int _port){
    host = _host;
    port= _port;
  }

  public static void main(String[] args) throws Exception {
    Client2 c = new Client2("localhost",555);
    c.run();
  }

  public void run() throws Exception{
    System.out.println("Second Client");
    Socket socket = new Socket(host,port);
    PrintStream ps = new PrintStream(socket.getOutputStream());
    ps.println("Hello From 2");

    InputStreamReader ir = new InputStreamReader(socket.getInputStream());
    BufferedReader br = new BufferedReader(ir);

//    StringBuilder inputStr = new StringBuilder();
//    String str;
//    while(( str = br.readLine())!=null){
//      inputStr.append(str);
//    }
//    System.out.println(inputStr);
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
    JsonObjects jo = new JsonObjects();

    employee = jo.GetEmployee(instr.toString());
    System.out.println("Has received Employee with name "+employee.Name);
    /** Close the socket connection. */
    Thread.sleep(10000);
    socket.close();
  }

}
