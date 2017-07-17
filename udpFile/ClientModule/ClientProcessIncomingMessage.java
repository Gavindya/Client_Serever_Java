package udpFile.ClientModule;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.math.BigInteger;
import java.net.DatagramSocket;

/**
 * Created by AdminPC on 7/13/2017.
 */
public class ClientProcessIncomingMessage implements Runnable {

  private DataInputStream dataInputStream;
  private Client client;
  private ClientServerConfiguration server;
  private ClientSendProcessedData sendProcessedData;
  private DatagramSocket datagramSocket;
  private int maxResendTimes;
  private int clientWaitingTime;
  private ClientSend clientSend;
  private int dataLength;
  private int serverSequence;
  private int clientSequence;
  private int control;
  private int window;
  private int mss;
  private int timestamp;
  private long sessionID;

  protected ClientProcessIncomingMessage( DataInputStream _dataInputStream,Client _client, ClientServerConfiguration _server,
                                       DatagramSocket _dataDatagramSocket,int _maxResendTimes,ClientSend _clientSend) {
    client=_client;
    clientWaitingTime=client.getWaitingTime();
    server=_server;
    maxResendTimes=_maxResendTimes;
    this.dataInputStream = _dataInputStream;
    datagramSocket=_dataDatagramSocket;
    sendProcessedData= new ClientSendProcessedData(datagramSocket,client);
    clientSend = _clientSend;
  }
  public void run() {
    process();
  }

  private void process(){
    try{
      splitData();
      switch (control) {
        case 12:
          System.out.println("syn ack");
          setServer(clientSequence,serverSequence,mss,timestamp,window,sessionID);
          break;
        case 8:
          System.out.println("syn ");
          break;
        case 6:
          System.out.println("ack fin");
          String session =String.valueOf(sessionID);
          if(session.equals(client.getSessionID())){
            client.getServer().setIsAlive(false);
          }
          break;
        case 4:
          System.out.println("ack ");
          clearWindowCell();
          break;
        default:
          System.out.println("Not a control msg");
          break;
      }

    }catch (Exception ex){
      ex.printStackTrace();
    }
  }
  private String MakeEight(String str) {
    switch (str.length()) {
      case 1:
        return ("0000000" + str);
      case 2:
        return ("000000" + str);
      case 3:
        return ("00000" + str);
      case 4:
        return ("0000" + str);
      case 5:
        return ("000" + str);
      case 6:
        return ("00" + str);
      case 7:
        return ("0" + str);
      case 8:
        return (str);
      default:
        return (null);
    }
  }
  private void setServer(int clientSequence,int serverSequence,int mss,int timestamp,int window,long sessionID){
    try{
      if((client.getSequenceNumber()+1)==clientSequence){
        server.setServer_sequenceNumber(serverSequence+1);
        server.setServer_mss(mss);
        server.setServer_timestamp(timestamp);
        server.setServer_windowSize(window);

        client.setSequenceNumber(clientSequence);
        client.setSessionID(String.valueOf(sessionID));
        client.getServer().setIsAlive(true);
        datagramSocket.setSoTimeout(server.getServer_timestamp());
        maxResendTimes = clientWaitingTime /server.getServer_timestamp();

        System.out.println("expecting server seq "+server.getServer_sequenceNumber());
        System.out.println("expecting client seq "+client.getSequenceNumber());
        //here must send client window
        clientSend.sendACK(server.getServer_address(),server.getServer_port(),clientSequence,server.getServer_sequenceNumber());

        client.setSequenceNumber(client.getSequenceNumber()+1);
        sendProcessedData= new ClientSendProcessedData(datagramSocket,client);
        sendProcessedData.start();
      }
    }catch (Exception ex){
      ex.printStackTrace();
    }
  }

  private void clearWindowCell(){
    try{
      if(String.valueOf(sessionID).equals(client.getSessionID())){
        for(int i =0;i<client.getWindow().length;i++){
          if(client.getWindow()[i]!=null ){
            ByteArrayInputStream bis = new ByteArrayInputStream(client.getWindow()[i]);
            DataInputStream dis = new DataInputStream(bis);
            StringBuilder temp = new StringBuilder();
            dis.skipBytes(6);
            for(int j=0;j<4;j++){
              temp.append(MakeEight(Integer.toBinaryString(dis.read())));
            }
            int seqNum =Integer.parseInt(temp.toString(),2);
            if(seqNum==serverSequence){
              client.setWindow(i,null);
            }
          }
        }
      }
    }catch (Exception ex){
      ex.printStackTrace();
    }
  }

  protected void splitData(){
    try{
      StringBuilder strBuilder = new StringBuilder();
      for(int i=0;i<2;i++){
        strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
      }
      dataLength =Integer.parseInt(strBuilder.toString(),2);
      strBuilder=new StringBuilder();
      for(int i=0;i<4;i++){
        strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
      }
      serverSequence =Integer.parseInt(strBuilder.toString(),2);
      strBuilder=new StringBuilder();
      for(int i=0;i<4;i++){
        strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
      }
      clientSequence =Integer.parseInt(strBuilder.toString(),2);
      strBuilder=new StringBuilder();
      control = dataInputStream.read();
      //        System.out.println("control="+control);
      for(int i=0;i<2;i++){
        strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
      }
      window =Integer.parseInt(strBuilder.toString(),2);
      strBuilder=new StringBuilder();
      for(int i=0;i<2;i++){
        strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
      }
      mss =Integer.parseInt(strBuilder.toString(),2);
      strBuilder=new StringBuilder();
      for(int i=0;i<2;i++){
        strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
      }
      timestamp =Integer.parseInt(strBuilder.toString(),2);
      strBuilder=new StringBuilder();
      for(int i=0;i<8;i++){
        strBuilder.append(MakeEight(Integer.toBinaryString(dataInputStream.read())));
      }
      sessionID =new BigInteger(strBuilder.toString(), 2).longValue();
    }catch (Exception e){
      e.printStackTrace();
    }
  }
}

