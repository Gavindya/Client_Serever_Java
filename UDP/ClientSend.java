package UDP;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by AdminPC on 7/4/2017.
 */
public class ClientSend extends Thread {
  private static InetAddress hostAddr;
  private int bufferSize;
  private static DatagramSocket datagramSocket;
  private static DatagramPacket datagramPacket;
  private String msg;
  BufferedReader BR;
  private static byte[] msgByteArray;

  ClientSend(InetAddress hostAd, int buffSize, DatagramSocket dsoc){
    hostAddr = hostAd;
    bufferSize = buffSize;
    datagramSocket = dsoc;
    BR = new BufferedReader(new InputStreamReader(System.in));
  }

  public void run(){
    while (true) {
      try {
        System.out.println("Enter msg :");
        msg = BR.readLine();
//      msg = "dsf";
//      System.out.println(msg.getBytes().length);

        if (msg.getBytes().length < (bufferSize + 1)) {
          String seq = MakeConstantDigits(UDPclient.seqNumber);
          String len = MakeConstantDigits(msg.length());
          if ((seq != null) && (seq.length() == 6) && (len != null) && (len.length() == 6)) {
            UDPclient.waitingForAckDataBuffer.put(UDPclient.seqNumber,msg);
            msg = UDPclient.sessionID + "#seq#" + seq + "#len#" + len + "#msg#" + msg;

            msgByteArray = msg.getBytes();
            datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length, hostAddr, UDPclient.portToBeConnected);
            datagramSocket.send(datagramPacket);

          } else {
            System.out.println("seq num / length is not in correct format");
          }

        } else {
          System.out.println("data is too long for the buffer");
        }
      } catch (Exception ex) {
        //
      }
    }
  }
  private static String MakeConstantDigits(int num){
    String str = Integer.toString(num);

    switch(str.length()) {
      case 1 :
        System.out.println("00000"+str);
        return ("00000"+str);
      case 2 :
        System.out.println("0000"+str);
        return ("0000"+str);
      case 3 :
        System.out.println("000"+str);
        return ("000"+str);
      case 4 :
        System.out.println("00"+str);
        return ("00"+str);
      case 5 :
        System.out.println("0"+str);
        return ("0"+str);
      case 6 :
        System.out.println(str);
        return (str);
      default :
        System.out.println(str);
        return (null);
    }
  }
  public static void SendData(int seqNum,String msg) throws Exception{
    System.out.println(seqNum+":"+msg);
    String seq = MakeConstantDigits(UDPclient.seqNumber);
    String len = MakeConstantDigits(msg.length());
    if ((seq != null) && (seq.length() == 6) && (len != null) && (len.length() == 6)) {
      UDPclient.waitingForAckDataBuffer.put(UDPclient.seqNumber,msg);
      msg = UDPclient.sessionID + "#seq#" + seq + "#len#" + len + "#msg#" + msg;

      msgByteArray = msg.getBytes();
      datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length, hostAddr, UDPclient.portToBeConnected);
      datagramSocket.send(datagramPacket);

    } else {
      System.out.println("seq num / length is not in correct format");
    }
  }

}
