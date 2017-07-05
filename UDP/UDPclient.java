package UDP;

import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by AdminPC on 6/27/2017.
 */
public class UDPclient {
  public static int portToBeConnected;
  private static int bufferSize;
  private static DatagramSocket datagramSocket;
  public static Map<Integer,String> waitingForAckDataBuffer;
  public static int seqNumber;
  public static int sessionID;
  public static ArrayList<Integer> ackNumbers;
  private ClientReceive receive;
  private ClientSend write;

  UDPclient(int _port,int _bufferSize){
    waitingForAckDataBuffer = new HashMap<Integer, String>();
    portToBeConnected = _port;
    bufferSize = _bufferSize;
    seqNumber =1;
    sessionID=(int)(Math.random()*1000000);
    System.out.println("Session = "+sessionID);
    ackNumbers = new ArrayList<Integer>();

  }

  public void connect(){
    try {
      datagramSocket = new DatagramSocket();
      InetAddress hostAddr = InetAddress.getLocalHost();

      receive = new ClientReceive(datagramSocket,bufferSize);
      write  = new ClientSend(hostAddr,bufferSize,datagramSocket);
//      datagramSocket.setSoTimeout(10000);

        write.start();
        receive.start();

    }catch(SocketException se){
      //
    }catch(Exception e){
      e.printStackTrace();
    }

  }
//  public void SendData( StringWriter words ) throws Exception{
//    System.out.println("In Send Data meth");
//    InetAddress hostAddr = InetAddress.getLocalHost();
//    byte[] msgByteArray = words.toString().getBytes();
//
//    datagramPacket = new DatagramPacket(msgByteArray,msgByteArray.length,hostAddr, portToBeConnected);
//    datagramSocket.send(datagramPacket);
//  }
//  public void SendFile() {
//    try{
//      System.out.println(filepath);
//      BufferedReader reader = new BufferedReader(new FileReader(filepath));
//      System.out.println("file found");
//      int count = 0;
//      int currentChar;
//      StringWriter writer = new StringWriter();
//
//      // terminate when eof reached
//      while((currentChar = reader.read()) != -1) {
//        if(count<bufferSize){
//          System.out.println((char)currentChar);
//
//          writer.append((char)currentChar);
//          count++;
//        }
//        if(count==bufferSize){
//          System.out.println(writer.toString());
//          SendData(writer);
//          count=0;
//          writer=new StringWriter();
//        }
//      }
//    }catch (Exception ex){
//      System.out.println(ex.getMessage());
//    }
//
//  }

}
