//package udpFile.Server;
//
//import java.util.Map;
//
///**
// * Created by AdminPC on 7/14/2017.
// */
//public class ServerUser implements ClientListener {
//  public void clientRegistered(ClientRegisterEvent event) {
//    if( event.getServerNewClient().client_mss!=0)
//    {
//      System.out.println( "*******////////=====+++++++++++****CLIENT CONNECTED :D :D" );
//    }
//  }
//  public void clientSentData( ClientSentDataEvent event ){
//    if(event.getDataStream().size()!=0){
//      System.out.println("IN EVENT TRIGGERED METHOD");
//      for(Map.Entry<Integer,String> entry : event.getDataStream().entrySet()){
//        System.out.println("#################"+entry.getKey()+"#"+entry.getValue());
//      }
//    }
//  }
//}
