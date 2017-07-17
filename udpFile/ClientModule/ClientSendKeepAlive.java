package udpFile.ClientModule;

import java.net.DatagramSocket;

/**
 * Created by Gavindya Jayawardena on 7/8/2017.
 */
public class ClientSendKeepAlive extends Thread {
    private long time=0;
    private DatagramSocket datagramSocket;
    private udpFile.ClientModule.ClientSend clientSend;
    private Client client;

    ClientSendKeepAlive( DatagramSocket _datagramSocket,Client _client){
      client=_client;
        datagramSocket=_datagramSocket;
        clientSend = new udpFile.ClientModule.ClientSend(datagramSocket,client);
    }
    public void run(){
        while (client.getServer().getIsAlive()){
            if((System.currentTimeMillis()-time)>=client.getKeepAliveTimeInerval()){
                clientSend.sendKeepAlive();
                time = System.currentTimeMillis();
            }
        }
    }
}
