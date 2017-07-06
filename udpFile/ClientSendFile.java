package udpFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by AdminPC on 7/6/2017.
 */
public class ClientSendFile {

//  private Client client;
  private DatagramSocket datagramSocket;

  ClientSendFile(DatagramSocket _datagramSocket){

    datagramSocket = _datagramSocket;
  }

  public void send( int client_seq, int server_ack, int window) throws Exception {
    String filepath="src/test1.txt";
    BufferedReader BR = new BufferedReader(new FileReader(filepath));
    char[] cbuf = new char[Client.getServer().getServer_mss()];
//    while(BR.read(cbuf, 0, cbuf.length)!=-1 ){
    int i=0;
    while (i<5){
      BR.read(cbuf, 0, cbuf.length);
      ClientSend clientSend = new ClientSend(datagramSocket);
      clientSend.sendData(cbuf,client_seq,server_ack,window);

//      cbuf = new char[cbuf.length];
      System.gc();
      i++;
    }
  }
}
