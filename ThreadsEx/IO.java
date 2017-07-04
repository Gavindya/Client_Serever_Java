package ThreadsEx;

import java.io.*;
import java.util.Arrays;

public class IO {
    private static String filepath;
    private static int numOfWords;
    private static CalculationThreadPool threadPool;
    private static long fileLength;
    private static File file;
    private static long workDone;

    IO(String file_path, int num_Of_Words, CalculationThreadPool thread_Pool){
        filepath = file_path;
        numOfWords=num_Of_Words;
        threadPool=thread_Pool;
        file = new File(filepath);
        fileLength = file.length();
        workDone=0;
    }

    public void readFile() throws Exception{

        BufferedReader reader = new BufferedReader(new FileReader(filepath));
        String[] words = new String[numOfWords];
        int currentIndex = 0;
        int currentChar;

        StringWriter writer = new StringWriter();

        // terminate when eof reached
        while((currentChar = reader.read()) != -1) {
            workDone = workDone+1;
            if (currentChar != ' ' && currentChar != ',' && currentChar != '.'
                    && currentChar != ';'  && currentChar != ':' && currentChar != '-'
                    && currentChar != '/'  && currentChar != '\\' && currentChar != '\t'
                    && currentChar != '0'  && currentChar != '1' && currentChar != '2'
                    && currentChar != '3'  && currentChar != '4' && currentChar != '5'
                    && currentChar != '6'  && currentChar != '7' && currentChar != '8'
                    && currentChar != '9') {
                        writer.append((char)currentChar);
            } else {
                if(!writer.toString().trim().isEmpty()) {
                    words[currentIndex] = writer.toString().trim();
                    currentIndex++;
                    writer = new StringWriter(); //always clear writer once added to words string array

                    if(currentIndex == numOfWords){
                        System.out.println((workDone*100)/fileLength+"%");
                        threadPool.addWork(new Counter(words));

                        words = new String[numOfWords];
                        currentIndex = 0;
                    }
                }
            }
        }
        // Send remaining words to thread pool as well
        if(!writer.toString().trim().isEmpty()) {
            words[currentIndex] = writer.toString().trim();
            currentIndex++;
        }
        if(currentIndex > 0){
            System.out.println((workDone*100)/fileLength+"%");
            threadPool.addWork(new Counter(Arrays.copyOfRange(words, 0, currentIndex)));
        }
        System.out.println("100%");
        System.out.println("File Read Completed");
        System.out.println(workDone);
        System.out.println(fileLength);
        threadPool.shutdown();
        while(!threadPool.getCountservice().isTerminated()){}
    }

}
