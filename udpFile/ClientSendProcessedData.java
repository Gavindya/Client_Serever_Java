package udpFile;

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
        System.out.println("MAX RESEND COUNT ="+maxResendCount);
        try {
            while (Client.getServer().isAlive) {
              if( !Client.noData){
                Thread.sleep(500);
  //                Thread.sleep(ClientModule.getServer().getServer_timestamp());
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
  //                                if((numOfElementsInWindow+i)<ClientModule.window.length){
  //                                }
                        String str = createDataMsg(Client.getBuffer()[index + i]);
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
  //                                    ClientModule.window[numOfElementsInWindow+i] = str;
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
  //                        if(ClientModule.getBuffer()[index + ClientModule.window.length]!=null){
  //                            index = index + ClientModule.window.length;
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
                  if (resendCounter < maxResendCount) {
                    sendOutWindow();

                  } else {
                    Client.getServer().isAlive = false;
                  }

                }
              }else{
                sendFIN();
                return;
              }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
    private String createDataMsg(char[] cbuf) {
//    private String createDataMsg(char[] cbuf,int seq, int ack, int window) {
        String text = String.valueOf(cbuf);
        int windowSize=0;
        for(String msg : Client.window){
            if(msg==null){
                windowSize++;
            }
        }
        return (MakeConstantDigits(cbuf.length+Client.getSessionID().length()) +
                MakeConstantDigits(Client.getSequenceNumber()) +
                MakeConstantDigits(Client.getServer().getServer_sequenceNumber()) +
                "0000" +
                MakeConstantDigits(windowSize) +
                MakeConstantDigits(0) +
                MakeConstantDigits(0) +
                MakeConstantDigits(0)+text+Client.getSessionID());
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
  private String createFINMsg() {
    return (MakeConstantDigits(0) +
      MakeConstantDigits(Client.getSequenceNumber()) +
      MakeConstantDigits(Client.getServer().getServer_sequenceNumber()) +
      "0010" +
      MakeConstantDigits(0) +
      MakeConstantDigits(0) +
      MakeConstantDigits(0) +
      MakeConstantDigits(0)+Client.getSessionID());
  }
    public void sendOutWindow() throws Exception{
        for(String msgInWindow : Client.window){
            System.out.println("MSG-->"+msgInWindow);
            byte[] msgByteArray = msgInWindow.getBytes();
            datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length,
                    Client.getServer().server_address, Client.getServer().server_port);
            datagramSocket.send(datagramPacket);
        }
    }
  public void sendFIN() throws Exception{
    byte[] msgByteArray = createFINMsg().getBytes();
    datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length,
      Client.getServer().server_address, Client.getServer().server_port);
    datagramSocket.send(datagramPacket);

  }
}
