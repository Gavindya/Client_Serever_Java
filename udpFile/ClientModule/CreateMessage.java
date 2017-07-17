package udpFile.ClientModule;

import java.io.ByteArrayOutputStream;

public class CreateMessage {
  public static byte[] createMsg(int sequenceNumber,int serverSequence,int control,int windowSize,int mss,int keepAliveTime, long sessionID,byte[] cbuf) {
    ByteArrayOutputStream boas = new ByteArrayOutputStream();
    java.io.DataOutputStream dos = new java.io.DataOutputStream(boas);
    try {
      //    //data length 2 bytes
      if(cbuf==null){
        dos.write(Byte.parseByte(Integer.toBinaryString(0), 2));
        dos.write(Byte.parseByte(Integer.toBinaryString(0), 2));
      }else{
        int len = cbuf.length;
        String len1;
        String len2;
        if (len < 256) {
          len1 = Integer.toBinaryString(0);
          len2 = Integer.toBinaryString(len);
        } else {
          len1 = MakeSixteen(Integer.toBinaryString(len)).substring(0, 8);
          len2 = MakeSixteen(Integer.toBinaryString(len)).substring(8,  MakeSixteen(Integer.toBinaryString(len)).length());
        }
        dos.write((byte) Integer.parseInt(len1, 2));
        dos.write((byte) Integer.parseInt(len2, 2));
      }

      //    //sequence number - 4 bytes
      dos.writeInt(sequenceNumber);
      //     //ack # - 4 bytes
      dos.writeInt(serverSequence);
      //     //control value - 1 byte
      dos.write(Byte.parseByte(Integer.toBinaryString(control), 2));
      //      //window 2 bytes
      String win1;
      String win2;
      if (windowSize < 256) {
        win1 = Integer.toBinaryString(0);
        win2 = Integer.toBinaryString(windowSize);
      } else {
        win1 = MakeSixteen(Integer.toBinaryString(windowSize)).substring(0, 8);
        win2 = MakeSixteen(Integer.toBinaryString(windowSize)).substring(8, MakeSixteen(Integer.toBinaryString(windowSize)).length());
      }
      dos.write((byte) Integer.parseInt(win1, 2));
      dos.write((byte) Integer.parseInt(win2, 2));
      //      //mss 2 bytes
      String mss1;
      String mss2;
      if (mss< 256) {
        mss1 = Integer.toBinaryString(0);
        mss2 = Integer.toBinaryString(mss);
      } else {
        mss1 = MakeSixteen(Integer.toBinaryString(mss)).substring(0, 8);
        mss2 = MakeSixteen(Integer.toBinaryString(mss)).substring(8, MakeSixteen(Integer.toBinaryString(mss)).length());

      }
      dos.write((byte) Integer.parseInt(mss1, 2));
      dos.write((byte) Integer.parseInt(mss2, 2));
      //      //timestamp 2 bytes
      String time1;
      String time2;
      if (keepAliveTime < 256) {
        time1 = Integer.toBinaryString(0);
        time2 = Integer.toBinaryString(keepAliveTime);
      } else {
        time1 = MakeSixteen(Integer.toBinaryString(keepAliveTime)).substring(0, 8);
        time2 = MakeSixteen(Integer.toBinaryString(keepAliveTime)).substring(8, MakeSixteen(Integer.toBinaryString(keepAliveTime)).length());
      }
      dos.write((byte) Integer.parseInt(time1, 2));
      dos.write((byte) Integer.parseInt(time2, 2));
      //session
      if(sessionID!=0){
        dos.writeLong(sessionID);
      }
      if(cbuf!=null){
        dos.writeUTF(new String(cbuf));
      }

      return boas.toByteArray();
    }catch (Exception ex){
      ex.printStackTrace();
      return null;
    }
  }

  private static String MakeSixteen(String str) {
    switch (str.length()) {
      case 1:
        return ("000000000000000" + str);
      case 2:
        return ("00000000000000" + str);
      case 3:
        return ("0000000000000" + str);
      case 4:
        return ("000000000000" + str);
      case 5:
        return ("00000000000" + str);
      case 6:
        return ("0000000000" + str);
      case 7:
        return ("000000000" + str);
      case 8:
        return ("00000000" + str);
      case 9:
        return ("0000000" + str);
      case 10:
        return ("000000" + str);
      case 11:
        return ("00000" + str);
      case 12:
        return ("0000" + str);
      case 13:
        return ("000" + str);
      case 14:
        return ("00" + str);
      case 15:
        return ("0" + str);
      case 16:
        return (str);
      default:
        return (null);
    }
  }
}
