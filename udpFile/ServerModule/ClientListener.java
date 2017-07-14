package udpFile.ServerModule;

/**
 * Created by AdminPC on 7/14/2017.
 */
public interface ClientListener {
  public void clientRegistered( ClientRegisterEvent event );
  public void clientSentData( ClientSentDataEvent event );
}
