package udpFile.ClientModule;

import java.util.ArrayList;

/**
 * Created by AdminPC on 7/17/2017.
 */
public class Buffer {
  private int size;
  private byte[][] buffer;
  Buffer(int _size){
    size=_size;

    buffer=new byte[size][];
  }
  protected byte[][] getBuffer(){
    return buffer;
  }
  protected void truncate(int length){
    //remove these from buffer
  }
  protected boolean isBufferEmpty(){
    boolean empty=false;
    for (byte[] entry : buffer) {
      if (entry != null) {
        empty = false;
        break;
      } else {
        empty = true;
      }
    }
    return empty;
  }

  protected int getRemainingIndex(){
    int remainingIndex =0;
    for(int k=buffer.length-1;k>-1;k--){
      if(buffer[k]!=null){
        remainingIndex=k+1;
      }
    }
    return remainingIndex;
  }
  protected void addToBuffer(int index, byte[] data){
    buffer[index]=data;
  }
  protected ArrayList<Integer>  remainingElementIndexes(){
    ArrayList<Integer> remaining = new ArrayList<Integer>();
    for(int b=0;b<buffer.length;b++){
      if(buffer[b]!=null){
        remaining.add(b);
      }
    }
    return remaining;
  }

  protected int remainingElementIndex(){
    return getRemainingIndex()-1;
  }
}
