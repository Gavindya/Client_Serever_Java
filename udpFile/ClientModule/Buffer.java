package udpFile.ClientModule;

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
  protected void setBuffer(){

  }
  protected byte[][] getBuffer(){
    return buffer;
  }
  protected void truncate(int length){
    //remove these from buffer
  }
}
