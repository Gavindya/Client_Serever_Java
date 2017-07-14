package udpFile.ServerModule;

import java.util.EventObject;

/**
 * Created by AdminPC on 7/14/2017.
 */
public class ClientRegisterEvent extends EventObject {

  private ServerNewClient _serverNewClient;

  public ClientRegisterEvent(Object source, ServerNewClient serverNewClient) {
    super(source);
    _serverNewClient = serverNewClient;
  }

  public ServerNewClient getServerNewClient() {
    return _serverNewClient;
  }
}
