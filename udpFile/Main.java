package udpFile;

import java.net.InetAddress;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class Main {

//  private static BufferedReader BR;
//  private static String filepath="src/test1.txt";
//  private static int bufferSize =100;
//
  public static void main(String[] args)throws Exception {
//
//    BR = new BufferedReader(new FileReader(filepath));
//    char[] cbuf = new char[bufferSize];
//    while(BR.read(cbuf, 0, bufferSize)!=-1){
//
//      System.out.println(cbuf);
//      cbuf = new char[bufferSize];
//      System.gc();
//    }
//    Client(int _mss, int _timestamp,int _window, int _server_port, InetAddress _server_address){
    Server server = new Server(7777,65000,1024,4500);
    server.serverUp();

    Client client = new Client(1024,5000,65000,7777, InetAddress.getLocalHost());
    client.makeConnection();
  }
}
