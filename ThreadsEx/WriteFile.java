package ThreadsEx;

import java.io.*;
import java.util.Map;

public class WriteFile {
    public void write(String filepath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filepath));
        for(Map.Entry<String,Long> entry : Main.result.entrySet()){
            writer.append(entry.getKey()+":"+entry.getValue() + "\n");
        }
        writer.flush();
    }
}
