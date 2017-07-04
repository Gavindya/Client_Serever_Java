package ThreadsEx;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Main {
    private static String inputFilePath = "src/test3.txt";
    private static String outputFilePath = "src/output.txt";
    public static ConcurrentMap<String, Long> result = new ConcurrentHashMap<String, Long>();

    public static void main(String[] args) throws Exception {
        CalculationThreadPool threadPool = new CalculationThreadPool(20);
        IO io = new IO(inputFilePath, 1024, threadPool);
        Long start = System.currentTimeMillis();
        io.readFile();
        Long end = System.currentTimeMillis();
        printAll();
        System.out.println((end - start)/1000);
    }

    public static void printAll() throws Exception{
//        for(Map.Entry<String,Long> entry : result.entrySet()){
//            System.out.print(entry.getKey()+":"+entry.getValue() + ", ");
//        }
//        System.out.println("");
        WriteFile writeFile = new WriteFile();
        writeFile.write(outputFilePath);
    }
}
