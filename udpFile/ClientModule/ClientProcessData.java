package udpFile.ClientModule;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by Gavindya Jayawardena on 7/9/2017.
 */
public class ClientProcessData extends Thread {
    private String filepath;
    private char[] cbuf;
    private int offset;
    BufferedReader BR;
    boolean isEmpty = false;

    ClientProcessData(String _filepath) throws Exception{
        filepath = _filepath;
        offset = 0;
        BR = new BufferedReader(new FileReader(filepath));
    }

    public void run(){
        try{
//            cbuf = new char[ClientModule.getServer().getServer_mss()];

          //define the data size to be sent to mss of server - (session key size)
          //          System.out.println("ServerModule MSS = "+ClientModule.getServer().getServer_mss());
          //          cbuf = new char[ClientModule.getServer().getServer_mss()-20];

          while (true) {
                Thread.sleep(5000);
                if(Client.getServer().isAlive){
                  System.out.println("ServerModule MSS = "+Client.getServer().getServer_mss());
                  cbuf = new char[Client.getServer().getServer_mss()-20];
//                    BR.skip(offset);
                    for (char[] entry : Client.getBuffer()) {
                        if (entry != null) {
                            isEmpty = false;
                            break;
                        } else {
                            isEmpty = true;
                        }
                    }
                    System.out.println("is Empty ? " + isEmpty);

                    if (isEmpty) {
//                    setOffset();
                        for (int i = 0; i < Client.getBufferSize(); i++) {
                            if(BR.read(cbuf, 0, cbuf.length)!=-1){
                              Client.setBuffer(cbuf, i);
                              cbuf = new char[cbuf.length];
                              Client.noData=false;
                            }else {
                              Client.noData=true;
                              break;
                            }
                        }
                        for (char[] c : Client.getBuffer()) {
                          if(c!=null){
                            System.out.println("client buffer : " + String.valueOf(c));
                          }else{
                            System.out.println("client buffer : " + c);
                          }
                        }
                    }
                    System.gc();
                }else{
                    Client.noData=true;
                    return;
//                    break;
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public void setOffset(){
        offset=offset+(Client.getBufferSize()*10);
//        offset=offset+(ClientModule.getBufferSize()*ClientModule.getServer().getServer_mss());
    }
}
