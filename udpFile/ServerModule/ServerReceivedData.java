package udpFile.ServerModule;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Gavindya Jayawardena on 7/9/2017.
 */
public class ServerReceivedData {

  ConcurrentMap<Integer,String> receivedDataMap;
  File file ;
  FileWriter fw;
  BufferedWriter bw;
  private long dataLastReceived;

  ServerReceivedData(String _filepath) throws Exception{
    file=new File(_filepath);
    receivedDataMap = new ConcurrentHashMap<Integer, String>();
    fw = new FileWriter(file,true);
    bw = new BufferedWriter(fw);
    if(!file.exists()){
      file.createNewFile();
    }
  }
  public long getDataLastReceivedTime(){
    return dataLastReceived;
  }
  public synchronized boolean getData(Integer serverSeq,String data){
    System.out.println("received data map size = "+receivedDataMap.size()+" : : : ServerModule receiving winow size ==="+Server.getReceivingWindowSize());
    if(receivedDataMap.size()<Server.getReceivingWindowSize()){
//      System.out.println("INNNN");
//      System.out.println(serverSeq+":::"+data);
      receivedDataMap.put(serverSeq,data);

        dataLastReceived=System.currentTimeMillis();
//      System.out.println("received data map size = "+receivedDataMap.size());
        for(Map.Entry<Integer,String> entry : receivedDataMap.entrySet()){
          System.out.println("RECEIVED BUFFER -->"+entry.getKey()+":"+entry.getValue());
        }
        return true;
    }else{
      return false;
    }

//    if(received.size()>5){
////      int min = Integer.MAX_VALUE;
//
//      for(Map.Entry<Integer,String> entry : received.entrySet()){
////        if(entry.getKey()<min){
////          min = entry.getKey();
////        }
//        System.out.println("Received buffer -->"+entry.getKey()+":"+entry.getValue());
//      }
//    }
//        bw.write(data);
//        bw.close();
  }

}
