package udpFile.ClientModule;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.DatagramSocket;

/**
 * Created by AdminPC on 7/6/2017.
 */
public class ClientSendFile {

//  private ClientModule client;
  private DatagramSocket datagramSocket;
  String filepath="src/test.txt";
  char[] cbuf;
  private int offset = 0;
  private ClientSend clientSend;

  ClientSendFile(DatagramSocket _datagramSocket){

    datagramSocket = _datagramSocket;
    clientSend = new ClientSend(datagramSocket);
  }

  public void send() throws Exception {

    BufferedReader BR = new BufferedReader(new FileReader(filepath));
    cbuf = new char[Client.getServer().getServer_mss()];
//    while(BR.read(cbuf, 0, cbuf.length)!=-1 ){
//    int resendCount=0;
//    while (resendCount<5){
    BR.skip(offset);
    BR.read(cbuf, 0, cbuf.length);
//    System.out.println(cbuf);
    if(cbuf[0]!=0){
      clientSend.sendData(cbuf);
    }
      cbuf = new char[cbuf.length];
    for(char[] c : Client.getBuffer()){
      System.out.println("client buffer "+c);
    }
    boolean isEmpty =false;
    for(char[] entry : Client.getBuffer()){
      if(entry!=null){
        isEmpty =false;
        break;
      }else{
        isEmpty=true;
      }
    }
    System.out.println("is Empty ? "+isEmpty);
    if(isEmpty){
      for (int i=0;i<Client.getBufferSize();i++) {
        BR.read(cbuf, 0, cbuf.length);
        Client.setBuffer(cbuf,i);
        cbuf = new char[cbuf.length];
      }
      for(char[] c : Client.getBuffer()){
        System.out.println("client buffer : "+String.valueOf(c));
      }
    }
    System.gc();
//    }
  }
  public void setOffset(){
//    offset=offset+ClientModule.getServer().getServer_mss();

    offset=offset+(Client.getBufferSize()*Client.getServer().getServer_mss());
  }


}
