package udpFile.ServerModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by AdminPC on 7/11/2017.
 */
public class ReceivedStream {
  private StringBuilder message;
  private List _listeners = new ArrayList();
  private Server server;

  ReceivedStream(Server _server){
    server=_server;
  }
  public StringBuilder getReceived(){
    return message;
  }

  public synchronized void setReceived(Map<Integer,String> dataStream){
    Object[] sequenceNumbers = dataStream.keySet().toArray();
    int[] sortedSeq = insertionSort(sequenceNumbers);
    message=new StringBuilder();
    for(int i=0;i<sortedSeq.length;i++){
//      received=received+dataStream.get(sortedSeq[i]);
      System.out.println(sortedSeq[i]+" : "+dataStream.get(sortedSeq[i]));
      message.append(dataStream.get(sortedSeq[i]));
    }
    _fireClientSentDataEvent(message.toString());
  }
  private int[] insertionSort(Object array[]) {
    int[] seqNum = new int[array.length];
    for(int y=0;y<array.length;y++){
      seqNum[y]=Integer.parseInt(array[y].toString());
    }

    int n = seqNum.length;
    for (int j = 1; j < n; j++) {
      int key = seqNum[j];
      int i = j-1;
      while ( (i > -1) && ( seqNum [i] > key ) ) {
        seqNum [i+1] =  seqNum [i];
        i--;
      }
      seqNum[i+1] = key;
    }
    return seqNum;
  }
  private synchronized void _fireClientSentDataEvent( String dataStream) {
    System.out.println("*********ClientModule snt data EVENT FIRED********");
    server._fireDataReceivedEvent(dataStream);
  }
}
