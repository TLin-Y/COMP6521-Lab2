package BitmapSolution2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BitmapCompressor {
    private String compressBitmapLine(String line){
       //String line = "0000000000000101";
        char [] bitmapArray = line.toCharArray();
        ArrayList<Integer> counterArraylist = new ArrayList<>();

        int count = 0;
        for(int i=0; i < bitmapArray.length; ++i){
            char c = bitmapArray[i];
            if(c == '0')
                count++;
            else{
                counterArraylist.add(count);
                count = 0;
            }
        }

        String compressLine = "";
        for (int num:counterArraylist) {
            String part1 = "";
            String part2 = new String(Integer.toBinaryString(num));
            int part2Len = part2.length();
            for(int i = 0; i<part2Len-1; ++i){
                part1 = new String (part1 + "1");
            }
            part1 = new String(part1 + "0");
            compressLine = new String(compressLine + part1 + part2);
        }

        return compressLine;
    }

    public void compressBitmapFile(String inputFileName) throws IOException {
        //read one line from the file
        FileReader fr = new FileReader(inputFileName);
        BufferedReader br = new BufferedReader(fr);
        String line;
        line = br.readLine();

        DataIO dataIO = new DataIO();
        int outputCharPos = 0;
        int inputLinePos = 0;
        int numTuples = 1000;
        int lineSize = line.length() + 2; //compute lineSize
        String oneBlock = dataIO.readBlockFromFile(inputFileName, inputLinePos, numTuples, lineSize);

        while(!oneBlock.equals("")){
            DataProcessor dataProcessor = new DataProcessor(oneBlock);
            List<String> bitmapLineList = dataProcessor.getBitmapLineList();
            List<String> compressedBitmapLineList = new ArrayList<>();
            for (String bitmapLine:bitmapLineList) {
                compressedBitmapLineList.add(compressBitmapLine(bitmapLine));
            }
            outputCharPos = dataIO.writeCompressedBitmapToFile("compressed_" + inputFileName, outputCharPos, compressedBitmapLineList);
            inputLinePos += numTuples;
            oneBlock = dataIO.readBlockFromFile(inputFileName, inputLinePos, numTuples, lineSize);
        }
    }
}
