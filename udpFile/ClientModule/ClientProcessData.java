package udpFile.ClientModule;


public class ClientProcessData extends Thread {

  private byte[] cbuf;
  private int offset;
  private boolean isEmpty = false;
  private byte[] data;
  private Client client;

      ClientProcessData(byte[] _data,Client _client) throws Exception{
        client=_client;
        offset = 0;
        data=_data;
    }

    public void run(){
        try{
          while (true) {
                Thread.sleep(3500);
                if(client.getServer().getIsAlive()){
                  cbuf = new byte[client.getServer().getServer_mss()-25];
                   isEmpty=clientBufferEmpty();
//                  isEmpty=client.getClientBuffer().isBufferEmpty();
                    if (isEmpty) {
                      System.out.println("");
                      if(data==null|| data.length==0){
                        client.noData=true;
                        return;
                      }
                      else if( data.length<cbuf.length){
                        int remainingIndex =0;
                        for(int k=client.getBuffer().length-1;k>-1;k--){
                          if(client.getBuffer()[k]!=null){
                            remainingIndex=k+1;
                          }
                        }
                        addToClientBuffer(data,remainingIndex);
//                        int remainingIndex = client.getClientBuffer().getRemainingIndex();
//                        client.getClientBuffer().addToBuffer(remainingIndex,data);
                        data=null;
                        client.noData=true;
                      }
                      else {
                        for (int i = 0; i < client.getBufferSize(); i++) {
                          if (data.length != 0) {
                            byte[] temp = new byte[cbuf.length];
                            int p = 0;
                            for (int y = 0; y < cbuf.length; y++) {
                              if (y < cbuf.length && y <data.length) {
                                temp[y] = data[y];
                                p++;
                              } else {
                                break;
                              }
                            }
                            if (data.length - cbuf.length > 0) {
                              byte[] remaining = new byte[data.length - cbuf.length];
                              for (int r = 0; r < remaining.length; r++) {
                                remaining[r] = data[r + p];
                              }
                              data = new byte[remaining.length];
                              data = remaining;
                            }
//                            client.getClientBuffer().addToBuffer(i,temp);
                            client.setBuffer(temp, i);
                            cbuf = new byte[cbuf.length];
                            client.noData = false;
                          } else {
                            client.noData = true;
                            break;
                          }
                        }
                      }
                    }
                    System.gc();
                }else{
                    client.noData=true;
                    return;
//                    break;
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
  private boolean clientBufferEmpty(){
    boolean empty=false;
    for (byte[] entry : client.getBuffer()) {
      if (entry != null) {
        empty = false;
        break;
      } else {
        empty = true;
      }
    }
    return empty;
  }
  private void addToClientBuffer(byte[] dataPortion, int index){
    client.setBuffer(dataPortion,index);
  }

}
