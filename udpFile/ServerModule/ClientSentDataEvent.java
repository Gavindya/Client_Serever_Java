package udpFile.ServerModule;

import java.util.EventObject;

/**
 * Created by AdminPC on 7/14/2017.
 */
public class ClientSentDataEvent extends EventObject {
  private String _dataStream;

  public ClientSentDataEvent(Object source,  String dataStream) {
    super(source);
    _dataStream = dataStream;
  }
  public String  getDataStream() {
    return _dataStream;
  }
}
