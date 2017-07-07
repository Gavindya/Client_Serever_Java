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
  String filepath="src/test.txt";
  char[] cbuf;
  private int offset = 0;
  private ClientSend clientSend;

  ClientSendFile(DatagramSocket _datagramSocket){

    datagramSocket = _datagramSocket;
    clientSend = new ClientSend(datagramSocket);
  }

  public void send( int client_seq, int server_ack, int window) throws Exception {

    BufferedReader BR = new BufferedReader(new FileReader(filepath));
    cbuf = new char[Client.getServer().getServer_mss()];
//    while(BR.read(cbuf, 0, cbuf.length)!=-1 ){
//    int resendCount=0;
//    while (resendCount<5){
    BR.skip(offset);
    BR.read(cbuf, 0, cbuf.length);
    System.out.println(cbuf);

    if(cbuf[0]!=0){
      clientSend.sendData(cbuf,client_seq,server_ack,window);
    }
//      cbuf = new char[cbuf.length];
      System.gc();
//      resendCount++;
//    }
  }
  public void setOffset(){
    offset=offset+Client.getServer().getServer_mss();
  }
}
