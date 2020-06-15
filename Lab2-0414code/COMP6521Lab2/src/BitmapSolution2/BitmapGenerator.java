package BitmapSolution2;

import java.util.ArrayList;
import java.util.List;

public class BitmapGenerator {
    BitmapGenerator(){
    }

    /**
     * input one digit, convert it to bitmap
     * @param digit
     * @return the string format of the bitmap
     */
    public String digitToBitmap(int digit, int attrib){
        String bitmapStr = "0000000000";
        if(attrib == 1)
            bitmapStr = "00";
        return bitmapStr.substring(0, digit) + "1" + bitmapStr.substring(digit + 1);
    }


    public List<String> generateBitmap(List<String> attributeList, int attrib){

        List<String> attributeBitmapList = new ArrayList<>();

        for (String attribute:attributeList) {

            String bitmap = "";
            char [] digitArray = attribute.toCharArray();

            for(int i = 0; i<digitArray.length; ++i){
                int digit = Character.getNumericValue(digitArray[i]);
                bitmap = new String(bitmap + digitToBitmap(digit, attrib));
            }

            attributeBitmapList.add(bitmap);
        }

        return attributeBitmapList;
    }

    public String getBitmapIndexOfEmployerId(String employerId){

        String bitmapIndex = "";
        char [] digitArray = employerId.toCharArray();

        for(int i = 0; i<digitArray.length; ++i){
            int digit = Character.getNumericValue(digitArray[i]);
            bitmapIndex = new String(bitmapIndex + digitToBitmap(digit, 0));
        }

        return bitmapIndex;
    }
}
