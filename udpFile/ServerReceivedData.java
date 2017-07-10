package udpFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Created by Gavindya Jayawardena on 7/9/2017.
 */
public class ServerReceivedData {
    public void getData(String data) throws Exception{

        File file =new File("src/output.txt");

        if(!file.exists()){
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file,true);
        BufferedWriter bw = new BufferedWriter(fw);
//        for(String str : data){
            bw.write(data);
//        }
        bw.close();

        System.out.println("Data successfully appended at the end of file");
    }

}
