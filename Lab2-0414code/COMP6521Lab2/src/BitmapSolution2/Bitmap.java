package BitmapSolution2;

public class Bitmap {


    /**
     * input one digit, convert it to bitmap
     * @param digit
     * @return the string format of the bitmap
     */
    public String digitToBitmap(int digit){
        String bitmapStr = "0000000000";
        return bitmapStr.substring(0, digit) + "1" + bitmapStr.substring(digit + 1);
    }

    public String numToBitmap(int num){

        int remainder = 0;
        String bitmapString = "";

        while(num != 0){
            remainder = num % 10;
            bitmapString  = new String(digitToBitmap(remainder) + bitmapString);
            num = num / 10;
        }
        return bitmapString;
    }
}
