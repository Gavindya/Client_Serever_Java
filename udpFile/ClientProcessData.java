package udpFile;

import jdk.nashorn.internal.codegen.ClassEmitter;

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
//            cbuf = new char[Client.getServer().getServer_mss()];
            cbuf = new char[50];
            while (true) {
                Thread.sleep(5000);
                if(Client.getServer().isAlive){
                    BR.skip(offset);
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
                            BR.read(cbuf, 0, cbuf.length);
                            Client.setBuffer(cbuf, i);
                            cbuf = new char[cbuf.length];
                        }
                        for (char[] c : Client.getBuffer()) {
                            System.out.println("client buffer : " + String.valueOf(c));
                        }
                    }
                    System.gc();
                }else{
                    break;
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    public void setOffset(){
        offset=offset+(Client.getBufferSize()*10);
//        offset=offset+(Client.getBufferSize()*Client.getServer().getServer_mss());
    }
}
