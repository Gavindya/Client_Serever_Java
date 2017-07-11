package udpFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Gavindya Jayawardena on 7/9/2017.
 */
public class ServerReceivedData {

  Map<Integer,String> received;
  File file ;
  FileWriter fw;
  BufferedWriter bw;

  ServerReceivedData(String _filepath) throws Exception{
    file=new File(_filepath);
    received = new HashMap<Integer, String>();
    fw = new FileWriter(file,true);
    bw = new BufferedWriter(fw);
    if(!file.exists()){
      file.createNewFile();
    }
  }
  public void getData(Integer serverSeq,String data) throws Exception{
    received.put(serverSeq,data);
    if(received.size()>5){
//      int min = Integer.MAX_VALUE;

      for(Map.Entry<Integer,String> entry : received.entrySet()){
//        if(entry.getKey()<min){
//          min = entry.getKey();
//        }
        System.out.println("Received buffer -->"+entry.getKey()+":"+entry.getValue());
      }
    }
//        bw.write(data);
//        bw.close();
  }

}
