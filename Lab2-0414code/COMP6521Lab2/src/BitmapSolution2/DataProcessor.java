package BitmapSolution2;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class DataProcessor {
    private String dataBlock;

    DataProcessor(String data){
        dataBlock = data;
    }

    public List<String> getAttributeList(int attribute){
        String [] linesArray = dataBlock.split("\\r?\\n");

        List<String> attributeList = new LinkedList<>();

        for(int i=0; i<linesArray.length; ++i){
            String line = linesArray[i];
            if(attribute == 0){
                attributeList.add(line.substring(0, 8));
            }else if(attribute == 1){
                attributeList.add(line.substring(43, 44));
            }else if(attribute == 2){
                attributeList.add(line.substring(44, 47));
            }
        }

        return attributeList;
    }

    public List<String> getBitmapLineList(){
        String [] linesArray = dataBlock.split("\\r?\\n");
        return Arrays.asList(linesArray);
    }

    public List<String> getBlockLineList(){
        String [] linesArray = dataBlock.split("\\r?\\n");
        return Arrays.asList(linesArray);
    }
}
