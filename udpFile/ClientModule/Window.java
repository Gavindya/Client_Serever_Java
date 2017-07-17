package udpFile.ClientModule;

/**
 * Created by AdminPC on 7/17/2017.
 */
public class Window {
  private int size;
  byte[][] window;
  Window(int _size){
    size=_size;
    window=new byte[size][];
  }
  protected void setWindow(){

  }
  protected void copy(Buffer buffer){
    //int min = min(remainigWindow, buffer)
    //copy that value to window
  }
}
