package BitmapSolution2;

import java.io.IOException;
import java.util.List;

public class BitmapGeneratorDriver {

    private static void printer(List<String> list){
        for (String str:list) {
            System.out.println(str);
        }
    }

    private static String [] attributeArray = {"EmpID", "Gender", "Dept"};


    private static void generateBitmapForAttribute(String inputFileName, int inputFileIndex, int attribute) throws IOException{
        String outputFileName = null;
        if(inputFileIndex == 0){
            if(attribute == 0) outputFileName = "id_bitmap1.txt";
            else if(attribute == 1) outputFileName = "gender_bitmap1.txt";
            else if(attribute == 2) outputFileName = "department_bitmap1.txt";
            else return;
        }else if(inputFileIndex == 1){
            if(attribute == 0) outputFileName = "id_bitmap2.txt";
            else if(attribute == 1) outputFileName = "gender_bitmap2.txt";
            else if(attribute == 2) outputFileName = "department_bitmap2.txt";
            else return;
        }else
            return;

        String oneBlock = null;
        DataIO dataIO = new DataIO();
        //read one block from file, set num of tuples/block: 10000
        int inputPos = 0;       //indicates which line the dataIO is reading from the input file
        int numTuples = 5000;  //num of lines to load from the input file
        int lineSize = 101;     //bytes of each line of input file
        oneBlock = dataIO.readBlockFromFile(inputFileName, inputPos, numTuples, lineSize);

        long start = System.currentTimeMillis();
        while (oneBlock != ""){
            // process each line of the block, get the block's id/gender/department List
            DataProcessor dataProcessor = new DataProcessor(oneBlock);
            List<String> attributeList = dataProcessor.getAttributeList(attribute);

            // generate bitmapList for id, gender and department
            BitmapGenerator bitmapGenerator = new BitmapGenerator();
            List<String> bitmapList = bitmapGenerator.generateBitmap(attributeList, attribute);
            dataIO.writeBitmapToFile(outputFileName, inputPos, bitmapList);

            inputPos += numTuples;
            oneBlock = dataIO.readBlockFromFile(inputFileName, inputPos, numTuples, lineSize);
        }
        long finish = System.currentTimeMillis();

        System.out.println(inputFileName + "'s " + attributeArray[attribute] + " Bitmap creation time:");
        System.out.println((finish - start) + " ms \n");
    }

    public static void main(String [] args) throws IOException{
        generateBitmapForAttribute("T1.txt", 0, 0);
        generateBitmapForAttribute("T1.txt", 0, 1);
        generateBitmapForAttribute("T1.txt", 0, 2);

        generateBitmapForAttribute("T2.txt", 1, 0);
        generateBitmapForAttribute("T2.txt", 1, 1);
        generateBitmapForAttribute("T2.txt", 1, 2);
    }
}
