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
        System.out.println("MAX RESESND COUNT ="+maxResendCount);
        try {
            while (Client.getServer().isAlive){
                Thread.sleep(500);
                numOfElementsInWindow=0;
                for(int p=0;p<Client.window.length;p++){
                    if(Client.window[p]!=null){
                        numOfElementsInWindow=numOfElementsInWindow+1;
                    }
                }
                if(numOfElementsInWindow>0){
                    isEmpty=false;
                    if(numOfElementsInWindow<Client.window.length){
                        resendCounter=0;
                    }
                }else if(numOfElementsInWindow==0){
                    isEmpty=true;
                    resendCounter=0;
                }
                System.out.println("IS WINDOW EMPTY??? :"+isEmpty);
                if(numOfElementsInWindow<Client.window.length){
                    System.out.println("BUFFER SIZE :- " + Client.getBuffer().length);
                    System.out.println("INDEX :- " + index);
                    if (index <= (Client.getBufferSize()-1)) {
                        for (int i = 0; i < Client.window.length; i++) {
                            System.out.println("-------------round "+i+"-------------");
                            if (((index + i) <Client.getBufferSize() )&& (Client.getBuffer()[index + i] != null)) {
//                                if((numOfElementsInWindow+i)<Client.window.length){
//                                }
                                String str = createDataMsg(Client.getBuffer()[index + i]);
                                System.out.println("message --> "+str);
                                if(isEmpty){
                                    System.out.println("Since window is empty : adding to widow's "+i+"th location");
                                    Client.window[i] = str;
                                }else if((numOfElementsInWindow+i)<Client.window.length){
                                    System.out.println("Since window is NOT empty : adding to widow's "+numOfElementsInWindow+"th location");
                                    Client.window[numOfElementsInWindow+i] = str;
                                }
                                else if((numOfElementsInWindow+i)==Client.window.length){
                                    System.out.println("window is FUL");
                                    index=index+i;
                                    System.out.println("INDEX = "+index+" :: NUM_OF_ELEMENTS="+numOfElementsInWindow+" ::");
                                    break;
                                }
                                Client.setBuffer(null,(index + i));
                                Client.setSequenceNumber(Client.getSequenceNumber() + 1);
                                Client.getServer().setServer_sequenceNumber(Client.getServer().getServer_sequenceNumber() + 1);
                            }else if((index+i)==Client.getBufferSize()){
                                index=0;
                                break;
                            }

                        }
//                        if(Client.getBuffer()[index + Client.window.length]!=null){
//                            index = index + Client.window.length;
//                        }
                        for(int r=0;r<Client.getBuffer().length;r++){
                            if(Client.getBuffer()[r]!=null){
                                index=r;
                                break;
                            }
                        }
                        System.out.println("FINAL INDEX=="+index);

                    }
                }else if(numOfElementsInWindow==Client.window.length){
                    System.out.println("in sending section where window is full");
                    resendCounter++;
                    System.out.println("resending for  ="+resendCounter+" th time");
                    if(resendCounter<maxResendCount){
                        sendOutWindow();
                    }else {
                        Client.getServer().isAlive=false;
                    }

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
        return (MakeConstantDigits(cbuf.length) +
                MakeConstantDigits(Client.getSequenceNumber()) +
                MakeConstantDigits(Client.getServer().getServer_sequenceNumber()) +
                "0000" +
                MakeConstantDigits(windowSize) +
                MakeConstantDigits(0) +
                MakeConstantDigits(0) +
                MakeConstantDigits(0)+text);
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
    public void sendOutWindow() throws Exception{
        for(String msgInWindow : Client.window){
            System.out.println("MSG-->"+msgInWindow);
            byte[] msgByteArray = msgInWindow.getBytes();
            datagramPacket = new DatagramPacket(msgByteArray, msgByteArray.length,
                    Client.getServer().server_address, Client.getServer().server_port);
            datagramSocket.send(datagramPacket);
        }
    }
}
