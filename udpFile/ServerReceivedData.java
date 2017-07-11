package udpFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
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
  public boolean getData(Integer serverSeq,String data) throws Exception{
    if(receivedDataMap.size()<Server.getReceivingWindowSize()){
        receivedDataMap.put(serverSeq,data);
        dataLastReceived=System.currentTimeMillis();
        for(Map.Entry<Integer,String> entry : receivedDataMap.entrySet()){
          System.out.println("Received buffer -->"+entry.getKey()+":"+entry.getValue());
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
