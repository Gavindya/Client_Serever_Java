//package udpFile;
//
//import UDP.*;
//
//import java.net.DatagramSocket;
//
///**
// * Created by Gavindya Jayawardena on 7/8/2017.
// */
//public class ClientSendKeepAlive extends Thread {
//    private long time=0;
//    private DatagramSocket datagramSocket;
//    private ClientSend clientSend;
//    ClientSendKeepAlive( DatagramSocket _datagramSocket){
//        datagramSocket=_datagramSocket;
//        clientSend = new ClientSend(datagramSocket);
//    }
//    public void run(){
//        while (Client.getServer().isAlive){
//            if((System.currentTimeMillis()-time)>=Client.getKeepAliveTimeInerval()){
//                clientSend.sendKeepAlive();
//                time = System.currentTimeMillis();
//            }
//        }
//    }
//}
