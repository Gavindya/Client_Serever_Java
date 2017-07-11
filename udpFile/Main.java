package udpFile;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Created by AdminPC on 7/5/2017.
 */
public class Main {

  private static BufferedReader BR;
  private static String filepath="src/test1.txt";
  private static int bufferSize =100;

  public static void main(String[] args)throws Exception {

//    BR = new BufferedReader(new FileReader(filepath));
//    char[] cbuf = new char[bufferSize];
//    while(BR.read(cbuf, 0, bufferSize)!=-1){
//
//      System.out.println(cbuf);
//      cbuf = new char[bufferSize];
//      System.gc();
//    }
    UUID randomID = UUID.randomUUID();
    System.out.println(randomID.getLeastSignificantBits());
    System.out.println(randomID.getMostSignificantBits());
    System.out.println("******");
    UUID randomID1 = UUID.randomUUID();
    System.out.println(randomID1.getLeastSignificantBits());
    System.out.println(randomID1.getMostSignificantBits());
    System.out.println("******");

    UUID randomID2 = UUID.randomUUID();
    System.out.println(randomID2.getLeastSignificantBits());
    System.out.println(randomID2.getMostSignificantBits());
    System.out.println("******");

    UUID randomID3 = UUID.randomUUID();
    System.out.println(randomID3.getLeastSignificantBits());
    System.out.println(randomID3.getMostSignificantBits());
    System.out.println("******");

    UUID randomID4 = UUID.randomUUID();
    System.out.println(randomID4.getLeastSignificantBits());
    System.out.println(randomID4.getMostSignificantBits());
    System.out.println(Math.abs(randomID4.hashCode()));
    System.out.println("******");

    UUID randomID5 = UUID.randomUUID();
    System.out.println(randomID5.getLeastSignificantBits());
    System.out.println(Long.toBinaryString(randomID5.getLeastSignificantBits()).length());

    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
    buffer.putLong(randomID5.getLeastSignificantBits());
//    System.out.println(buffer.getLong());

    System.out.println(buffer.position());
    buffer.flip();//need flip
    System.out.println(buffer.getLong());
    System.out.println(buffer.remaining());
//    System.out.println(String.valueOf(randomID5.hashCode()).getBytes().length);
//    System.out.println(Math.abs(randomID5.hashCode()));
//    System.out.println(randomID5);
  }
}
