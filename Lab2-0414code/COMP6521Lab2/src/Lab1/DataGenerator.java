package Lab1;
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class DataGenerator {
    static Random rand = new Random();

    /**
     * generate an 8-digit Id, the 1st digit should not be 1
     * @return an 8-digit Id String
     */
    static String generateId(){
        //generate 1st non-zero digit
        int firstDigit = rand.nextInt(9) + 1;

        //generate the rest 7 digits
        int nextSevenDigits = rand.nextInt(10000000);
        Integer id = firstDigit * 10000000 + nextSevenDigits;
        return id.toString();
    }

    /**
     * Generate a date String in the format of yyyy-mm-dd
     * date is between 01/01/2000 and 31/01/2020
     * @return a date String
     */
    static String generateDate(){

        String dateString;
        Date startDate, endDate, randDate = null;

        try {
            dateString = "01/01/2000";
            startDate = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
            dateString = "31/01/2020";
            endDate = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
            randDate = between(startDate, endDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = dateFormat.format(randDate);
        return strDate;
    }

    /**
     * generate a date between given start Date and end Date
     * @param startInclusive
     * @param endExclusive
     * @return a Date date
     */
    public static Date between(Date startInclusive, Date endExclusive) {
        long startMillis = startInclusive.getTime();
        long endMillis = endExclusive.getTime();
        long randomMillisSinceEpoch = ThreadLocalRandom
                .current()
                .nextLong(startMillis, endMillis);

        return new Date(randomMillisSinceEpoch);
    }

    /**
     * generate a string of length 25, filled with first name and last name
     * @return a name string of size 25
     */
    static String generateName(){
        String name = "firstname lastname";
        char[] charArray = new char[7];
        Arrays.fill(charArray, ' ');
        String space = new String(charArray);
        return name + space;
    }

    /**
     * generate gender(1) + dept(3) + SIN(9) + space(1)
     * @return String gender(1) + dept(3) + SIN(9) + space(1)
     */
    static String generateGenderDeptSinSpace(){
        String str = "1234567890123 ";
        return str;
    }

    /**
     * generate address
     * @return
     */
    static String generateAddress(){
        String str = "Raleigh NC 27606 South";
        char[] charArray = new char[21];
        Arrays.fill(charArray, ' ');
        String space = new String(charArray);
        return str + space;
    }

    static String generateLine(){
        return generateId() + generateDate() + generateName() + generateGenderDeptSinSpace() + generateAddress();
    }

    public static void writeToFile(String fileName, int tupleNum) throws IOException {
        File fout = new File(fileName);
        FileOutputStream fos = new FileOutputStream(fout);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (int i = 0; i < tupleNum; i++) {
            bw.write(generateLine());
            bw.newLine();
        }

        bw.close();
    }

    public static void main(String [] args) throws IOException {
    	System.out.println("Data generating now......");
        writeToFile("T1.txt", 50000);
        System.out.println("Finished! Please check the folder T1.txt");

        System.out.println("Data generating now......");
        writeToFile("T2.txt", 50000);
        System.out.println("Finished! Please check the folder T2.txt");
    }
}
