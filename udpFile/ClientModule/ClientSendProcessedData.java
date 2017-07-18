package udpFile.ClientModule;

import udpFile.ServerModule.Server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

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
    private Client client;
    ClientSendProcessedData(DatagramSocket _datagramSocket,Client _client){
      client=_client;
        datagramSocket = _datagramSocket;
      if(client.getServer().getIsAlive()){
        maxResendCount = client.getWaitingTime() /client.getServer().getServer_timestamp();
      }else{
        maxResendCount = client.getWaitingTime() /3000;
      }

    }
    public void run(){
        try {
            while (client.getServer().getIsAlive()) {
              maxResendCount = client.getWaitingTime() /client.getServer().getServer_timestamp();
              if( client.noData) {
                Thread.sleep(3000);
                processRemainingBuffer();
                return;
              }
              else if( !client.noData){
                Thread.sleep(500);
  //                Thread.sleep(ClientModule.getServer().getServer_timestamp());
//                numOfElementsInWindow = client.getClientWindow().getNumberOfElements();
                numOfElementsInWindow = 0;
                for (int p = 0; p < client.getWindow().length; p++) {
                  if (client.getWindow()[p] != null) {
                    numOfElementsInWindow = numOfElementsInWindow + 1;
                  }
                }
                if (numOfElementsInWindow > 0) {
                  isEmpty = false;
//                  if (numOfElementsInWindow <client.getClientWindow().getWindowSize()) {
                  if (numOfElementsInWindow <client.getWindow().length) {
                    resendCounter = 0;
                  }
                } else if (numOfElementsInWindow == 0) {
                  isEmpty = true;
                  resendCounter = 0;
                }
//                Map<String,Integer> results = client.getClientWindow().processBuffer(index, client.getClientBuffer(), client.getSequenceNumber()
//                  , client.getServer().getServer_sequenceNumber(),client.getSessionID(),resendCounter);
//                if(results.containsKey("resendCounter")){
//                  resendCounter=results.get("resendCounter");
//                  client.setSequenceNumber(results.get("clientSequence"));
//                  client.getServer().setServer_sequenceNumber(results.get("serverSequence"));
//                    if(resendCounter < maxResendCount) {
//                      sendOutWindow();
//                    }
//                }
                processBuffer();
              }else{
                sendFIN();
                return;
              }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

  public void sendOutWindow() throws Exception{
//        for(byte[] msgInWindow : client.getClientWindow().getWindow()){
        for(byte[] msgInWindow : client.getWindow()){
            System.out.println("MSG-->"+msgInWindow);
          DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(msgInWindow,0, msgInWindow.length));
//            byte[] msgByteArray = msgInWindow;

          if(msgInWindow!=null){
            dataInputStream.skipBytes(25);
            System.out.println("MSG CONTENT-->"+dataInputStream.readUTF());
            datagramPacket = new DatagramPacket(msgInWindow, msgInWindow.length,
              client.getServer().getServer_address(), client.getServer().getServer_port());
            datagramSocket.send(datagramPacket);
          }
        }
    }
  public void sendFIN() throws Exception{
//    public static byte[] createMsg(int sequenceNumber,int serverSequence,int control,int windowSize,int mss,int keepAliveTime, long sessionID,byte[] cbuf)
      byte[] msgByteArray = CreateMessage.createMsg(client.getSequenceNumber(),client.getServer().getServer_sequenceNumber(),
        2,0,0,0,Long.parseLong(client.getSessionID()),null);
    if(msgByteArray!=null){
      datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length,
        client.getServer().getServer_address(), client.getServer().getServer_port());
      datagramSocket.send(datagramPacket);
    }
  }
  private void processBuffer(){
    try{

    //                System.out.println("IS WINDOW EMPTY??? :" + isEmpty);
//                System.out.println("Num of elements in window =" + numOfElementsInWindow);
//  if (numOfElementsInWindow < client.getClientWindow().getWindowSize()) {
    if (numOfElementsInWindow < client.getWindow().length) {
//                  System.out.println("BUFFER SIZE :- " + client.getBuffer().length);
//                  System.out.println("INDEX :- " + index);
      if (index <= (client.getBufferSize() - 1)) {
        for (int i = 0; i <client.getWindow().length; i++) {
//                      System.out.println("-------------round " + i + "-------------");
          if (((index + i) < client.getBufferSize()) && (client.getBuffer()[index + i] != null)) {
            int windowSize = 0;
            for (byte[] msg :client.getWindow()) {
              if (msg == null) {
                windowSize++;
              }
            }
            byte[] str = CreateMessage.createMsg(client.getSequenceNumber(),client.getServer().getServer_sequenceNumber(),
              0,windowSize,0,0,Long.parseLong(client.getSessionID()),client.getBuffer()[index + i]);
            System.out.println("message --> " + str);
            if (isEmpty) {
              System.out.println("Since window is empty : adding to widow's " + i + "th location");
              client.setWindow(i,str);
              client.setBuffer(null, (index + i));
              client.setSequenceNumber(client.getSequenceNumber() + 1);
              client.getServer().setServer_sequenceNumber(client.getServer().getServer_sequenceNumber() + 1);
              numOfElementsInWindow++;
            } else if ((numOfElementsInWindow + i) <client.getWindow().length) {
              System.out.println("Since window is NOT empty : adding to widow's " + numOfElementsInWindow + "th location");
              int indexEmpty = numOfElementsInWindow;
              for (int y = 0; y < client.getWindow().length; y++) {
                if (client.getWindow()[y] == null) {
                  indexEmpty = y;
                  System.out.println("Empty location-------" + y);
                  break;
                }
              }
              client.setWindow(indexEmpty,str);
              client.setBuffer(null, (index + i));
              client.setSequenceNumber(client.getSequenceNumber() + 1);
              client.getServer().setServer_sequenceNumber(client.getServer().getServer_sequenceNumber() + 1);
              numOfElementsInWindow++;
            } else if ((numOfElementsInWindow + i) ==client.getWindow().length) {
              System.out.println("window is FUL");
              System.out.println("INDEX = " + index + " :: NUM_OF_ELEMENTS=" + numOfElementsInWindow + " ::");
              break;
            }
          } else if ((index + i) == client.getBufferSize()) {
            index = 0;
            break;
          }

        }
        for (int r = 0; r < client.getBuffer().length; r++) {
          if (client.getBuffer()[r] != null) {
            index = r;
            break;
          }
        }
        System.out.println("FINAL INDEX==" + index);
      }
    } else if (numOfElementsInWindow ==client.getWindow().length) {
      System.out.println("in sending section where window is full");
      resendCounter++;
      System.out.println("resending for  =" + resendCounter + " th time");
      System.out.println("max resend count = "+maxResendCount);
      if (resendCounter < maxResendCount) {
        sendOutWindow();

      } else {
//                    ClientModule.getServer().isAlive = false;
      }
    }
    }catch (Exception ex){
      ex.printStackTrace();
    }
  }
  private void processRemainingBuffer(){
    try{
      int clientBuffRemaining = client.getBuffer().length - 1;
//      int clientBuffRemaining = client.getClientBuffer().getRemainingIndex();
      for (int x = client.getBuffer().length - 1; x > -1; x--) {
        if (client.getBuffer()[x] != null) {
          clientBuffRemaining = x;
          break;
        }
      }
      numOfElementsInWindow=0;
//      numOfElementsInWindow=client.getClientWindow().getNumberOfElements();
      for(int p=0;p<client.getWindow().length;p++){
        if(client.getWindow()[p]!=null){
          numOfElementsInWindow++;
        }
      }
      if(numOfElementsInWindow==client.getWindow().length){
        sendOutWindow();
      }
      Thread.sleep(1000);
      int indexEmpty=0;
//      int indexEmpty=client.getClientWindow().getEmptyIndex();
      for(int y=0;y<client.getWindow().length;y++){
        if(client.getWindow()[y]==null){
          indexEmpty=y;
          break;
        }
      }

      int winSize = 0;
//      int winSize = client.getClientWindow().getWindowSize()-client.getClientWindow().getNumberOfElements();
      for (byte[] msg :client.getWindow()) {
        if (msg == null) {
          winSize++;
        }
      }
      byte[] dataMsg = CreateMessage.createMsg(client.getSequenceNumber(),client.getServer().getServer_sequenceNumber(),
        0,winSize,0,0,Long.parseLong(client.getSessionID()),client.getBuffer()[clientBuffRemaining]);

//      client.getClientWindow().setWindow(indexEmpty,dataMsg);
      client.setWindow(indexEmpty,dataMsg);
      numOfElementsInWindow++;
//      client.getClientBuffer().addToBuffer(clientBuffRemaining,null);
      client.setBuffer(null,clientBuffRemaining);
      client.setSequenceNumber(client.getSequenceNumber()+1);
      client.getServer().setServer_sequenceNumber(client.getServer().getServer_sequenceNumber()+1);
      int countElementsInWin=0;
      for(byte[] bt :client.getWindow()){
        if(bt!=null)countElementsInWin++;
      }
      if(countElementsInWin!=0){
        sendOutWindow();
      }
    }catch (Exception ex){
      ex.printStackTrace();
    }
  }
}
