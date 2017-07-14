package udpFile.Client;

import udpFile.Server.Server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

/**
 * Created by Gavindya Jayawardena on 7/9/2017.
 */
public class ClientSendProcessedData extends Thread {
    static int index =0;
    boolean isEmpty = false;
    DatagramSocket datagramSocket;
    DatagramPacket datagramPacket;
    int numOfElementsInWindow=0;
    int maxResendCount;
    int resendCounter=0;
    ClientSendProcessedData(DatagramSocket _datagramSocket){
        datagramSocket = _datagramSocket;
        maxResendCount = Client.getWaitingTime() /Client.getServer().getServer_timestamp();
    }
    public void run(){

        try {
            while (Client.getServer().isAlive) {
              maxResendCount = Client.getWaitingTime() /Client.getServer().getServer_timestamp();
              System.out.println("ClIENT WAITING TIME = "+Client.getWaitingTime() );
              System.out.println("SERVER TIME = "+Client.getServer().getServer_timestamp());
              System.out.println("MAX RESEND COUNT ="+maxResendCount);
              if( !Client.noData){
                Thread.sleep(500);
  //                Thread.sleep(Client.getServer().getServer_timestamp());
                numOfElementsInWindow = 0;
                for (int p = 0; p < Client.window.length; p++) {
                  if (Client.window[p] != null) {
                    numOfElementsInWindow = numOfElementsInWindow + 1;
                  }
                }
                if (numOfElementsInWindow > 0) {
                  isEmpty = false;
                  if (numOfElementsInWindow < Client.window.length) {
                    resendCounter = 0;
                  }
                } else if (numOfElementsInWindow == 0) {
                  isEmpty = true;
                  resendCounter = 0;
                }
                System.out.println("IS WINDOW EMPTY??? :" + isEmpty);
                System.out.println("Num of elements in window =" + numOfElementsInWindow);
                if (numOfElementsInWindow < Client.window.length) {
                  System.out.println("BUFFER SIZE :- " + Client.getBuffer().length);
                  System.out.println("INDEX :- " + index);
                  if (index <= (Client.getBufferSize() - 1)) {
                    for (int i = 0; i < Client.window.length; i++) {
                      System.out.println("-------------round " + i + "-------------");
                      if (((index + i) < Client.getBufferSize()) && (Client.getBuffer()[index + i] != null)) {
  //                                if((numOfElementsInWindow+i)<Client.window.length){
  //                                }
                        byte[] str = createDataMsg(Client.getBuffer()[index + i]);
                        System.out.println("message --> " + str);
                        if (isEmpty) {
                          System.out.println("Since window is empty : adding to widow's " + i + "th location");
                          Client.window[i] = str;
                          Client.setBuffer(null, (index + i));
                          Client.setSequenceNumber(Client.getSequenceNumber() + 1);
                          Client.getServer().setServer_sequenceNumber(Client.getServer().getServer_sequenceNumber() + 1);
                          numOfElementsInWindow++;
                        } else if ((numOfElementsInWindow + i) < Client.window.length) {
                          System.out.println("Since window is NOT empty : adding to widow's " + numOfElementsInWindow + "th location");
  //                                    Client.window[numOfElementsInWindow+i] = str;
                          int indexEmpty = numOfElementsInWindow;
                          for (int y = 0; y < Client.window.length; y++) {
                            if (Client.window[y] == null) {
                              indexEmpty = y;
                              System.out.println("Empty location-------" + y);
                              break;
                            }
                          }
                          Client.window[indexEmpty] = str;
                          Client.setBuffer(null, (index + i));
                          Client.setSequenceNumber(Client.getSequenceNumber() + 1);
                          Client.getServer().setServer_sequenceNumber(Client.getServer().getServer_sequenceNumber() + 1);
                          numOfElementsInWindow++;
                        } else if ((numOfElementsInWindow + i) == Client.window.length) {
                          System.out.println("window is FUL");
  //                                    index=index+i;
                          System.out.println("INDEX = " + index + " :: NUM_OF_ELEMENTS=" + numOfElementsInWindow + " ::");
                          break;
                        }
                      } else if ((index + i) == Client.getBufferSize()) {
                        index = 0;
                        break;
                      }

                    }
  //                        if(Client.getBuffer()[index + Client.window.length]!=null){
  //                            index = index + Client.window.length;
  //                        }
                    for (int r = 0; r < Client.getBuffer().length; r++) {
                      if (Client.getBuffer()[r] != null) {
                        index = r;
                        break;
                      }
                    }
                    System.out.println("FINAL INDEX==" + index);
                  }
                } else if (numOfElementsInWindow == Client.window.length) {
                  System.out.println("in sending section where window is full");
                  resendCounter++;
                  System.out.println("resending for  =" + resendCounter + " th time");
                  System.out.println("max resend count = "+maxResendCount);
                  if (resendCounter < maxResendCount) {
                    sendOutWindow();

                  } else {
//                    Client.getServer().isAlive = false;
                  }
                }
              }else{
//                sendFIN();
                return;
              }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
    private byte[] createDataMsg(char[] cbuf) {
//    private String createDataMsg(char[] cbuf,int seq, int ack, int window) {
      try {
        String text = String.valueOf(cbuf);
        int windowSize = 0;
        for (byte[] msg : Client.window) {
          if (msg == null) {
            windowSize++;
          }
        }
//        return (MakeConstantDigits(cbuf.length+Client.getSessionID().length()) +
//                MakeConstantDigits(Client.getSequenceNumber()) +
//                MakeConstantDigits(Client.getServer().getServer_sequenceNumber()) +
//                "0000" +
//                MakeConstantDigits(windowSize) +
//                MakeConstantDigits(0) +
//                MakeConstantDigits(0) +
//                MakeConstantDigits(0)+text+Client.getSessionID());

        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        java.io.DataOutputStream dos = new java.io.DataOutputStream(boas);
//    //data length 2 bytes
//        int len = cbuf.length + Client.getSessionID().length();
        int len = cbuf.length;
        String len1;
        String len2;
        if (len < 256) {
          len1 = Integer.toBinaryString(0);
          len2 = Integer.toBinaryString(len);
        } else {
          len1 = MakeSixteen(Integer.toBinaryString(len)).substring(0, 8);
          len2 = MakeSixteen(Integer.toBinaryString(len)).substring(8,  MakeSixteen(Integer.toBinaryString(len)).length());
        }
        dos.write((byte) Integer.parseInt(len1, 2));
        dos.write((byte) Integer.parseInt(len2, 2));
//    //sequence number - 4 bytes
        dos.writeInt(Client.getSequenceNumber());
//     //ack # - 4 bytes
        dos.writeInt(Client.getServer().getServer_sequenceNumber());
//     //control value - 1 byte
        dos.write(Byte.parseByte(Integer.toBinaryString(0), 2));
        //window length 2 bytes
        String win1;
        String win2;
        if (Server.getServer_windowSize() < 256) {
          win1 = Integer.toBinaryString(0);
          win2 = Integer.toBinaryString(windowSize);
        } else {
          win1 =  MakeSixteen(Integer.toBinaryString(windowSize)).substring(0, 8);
          win2 =  MakeSixteen(Integer.toBinaryString(windowSize)).substring(8,  MakeSixteen(Integer.toBinaryString(windowSize)).length());
        }
        dos.write((byte) Integer.parseInt(win1, 2));
        dos.write((byte) Integer.parseInt(win2, 2));
//      //mss 2 bytes
        dos.write((byte) Integer.parseInt(Integer.toBinaryString(0), 2));
        dos.write((byte) Integer.parseInt(Integer.toBinaryString(0), 2));
//      //timestamp 2 bytes
        dos.write((byte) Integer.parseInt(Integer.toBinaryString(0), 2));
        dos.write((byte) Integer.parseInt(Integer.toBinaryString(0), 2));
        dos.writeLong(Long.parseLong(Client.getSessionID()));// 8 bytes
        dos.writeUTF(text);

        return boas.toByteArray();
      }catch (Exception ex){
        ex.printStackTrace();
        return null;
      }
    }
    private static String MakeConstantDigits(int num) {
        String str = Integer.toString(num);

        switch (str.length()) {
            case 1:
                return ("00000" + str);
            case 2:
                return ("0000" + str);
            case 3:
                return ("000" + str);
            case 4:
                return ("00" + str);
            case 5:
                return ("0" + str);
            case 6:
                return (str);
            default:
                return (null);
        }
    }
  private String MakeSixteen(String str) {
    switch (str.length()) {
      case 1:
        return ("000000000000000" + str);
      case 2:
        return ("00000000000000" + str);
      case 3:
        return ("0000000000000" + str);
      case 4:
        return ("000000000000" + str);
      case 5:
        return ("00000000000" + str);
      case 6:
        return ("0000000000" + str);
      case 7:
        return ("000000000" + str);
      case 8:
        return ("00000000" + str);
      case 9:
        return ("0000000" + str);
      case 10:
        return ("000000" + str);
      case 11:
        return ("00000" + str);
      case 12:
        return ("0000" + str);
      case 13:
        return ("000" + str);
      case 14:
        return ("00" + str);
      case 15:
        return ("0" + str);
      case 16:
        return (str);
      default:
        return (null);
    }
  }
  private byte[] createFINMsg() {
    try {
      ByteArrayOutputStream boas = new ByteArrayOutputStream();
      java.io.DataOutputStream dos = new java.io.DataOutputStream(boas);
      dos.write((byte) Integer.parseInt(Integer.toBinaryString(0), 2));
      dos.write((byte) Integer.parseInt(Integer.toBinaryString(0), 2));
//    //sequence number - 4 bytes
      dos.writeInt(Client.getSequenceNumber());
//     //ack # - 4 bytes
      dos.writeInt(Client.getServer().getServer_sequenceNumber());
//     //control value - 1 byte
      dos.write(Byte.parseByte(Integer.toBinaryString(2), 2));
      //window length 2 bytes
      dos.write((byte) Integer.parseInt(Integer.toBinaryString(0), 2));
      dos.write((byte) Integer.parseInt(Integer.toBinaryString(0), 2));
//      //mss 2 bytes
      dos.write((byte) Integer.parseInt(Integer.toBinaryString(0), 2));
      dos.write((byte) Integer.parseInt(Integer.toBinaryString(0), 2));
//      //timestamp 2 bytes
      dos.write((byte) Integer.parseInt(Integer.toBinaryString(0), 2));
      dos.write((byte) Integer.parseInt(Integer.toBinaryString(0), 2));
      dos.writeLong(Long.parseLong(Client.getSessionID()));// 8 bytes

      return boas.toByteArray();
    }catch (Exception ex){
      ex.printStackTrace();
      return null;
    }

  }
    public void sendOutWindow() throws Exception{
        for(byte[] msgInWindow : Client.window){
            System.out.println("MSG-->"+msgInWindow);
//            byte[] msgByteArray = msgInWindow;
            datagramPacket = new DatagramPacket(msgInWindow, msgInWindow.length,
                    Client.getServer().server_address, Client.getServer().server_port);
            datagramSocket.send(datagramPacket);
        }
    }
  public void sendFIN() throws Exception{
    byte[] msgByteArray = createFINMsg();
    if(msgByteArray!=null){
      datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length,
        Client.getServer().server_address, Client.getServer().server_port);
      datagramSocket.send(datagramPacket);
    }
  }
}
