package Lab1;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataReader extends BufferedReader {

    public float preserveMemory;
    public File file;
    public short tupleNumInOneBlock = 40;
    public int ioCounter;
    public boolean finish;

    public DataReader(File file, float preserveMemPercentage) throws FileNotFoundException {
        super(new java.io.FileReader(file));
        this.preserveMemory = Runtime.getRuntime().maxMemory() * preserveMemPercentage;
        this.file = file;
    }

    public List<Tuple> getOneBlock() {
        List<Tuple> oneBlock = new ArrayList<>(tupleNumInOneBlock);
        if (Runtime.getRuntime().freeMemory() > preserveMemory) {
            for (int i = 0; i < tupleNumInOneBlock; i++) {
                Tuple oneTuple = getOneTuple();
                if (oneTuple == null)
                    break;
                oneBlock.add(oneTuple);
            }
            if (oneBlock.size() != 0)
                ioCounter++;
        }
        return oneBlock;
    }

    private Tuple getOneTuple() {
        try {
            String nextLine = this.readLine();
            if (nextLine == null || nextLine.trim().equals("")) {
                finish = true;
                return null;
            }
            return stringParser(nextLine);
        } catch (IOException e) {
            e.printStackTrace();
            finish = true;
            return null;
        }
    }

    //parse one line file string to one tuple object
    private Tuple stringParser(String line) {
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-mm-dd");
            int EmpID = Integer.valueOf(line.substring(0, 8));
            Date LastUpdate = dateFormatter.parse(line.substring(8, 18));
            String EmName = line.substring(18, 43).trim();
            int Gender = Integer.valueOf(line.substring(43, 44));
            int Dept = Integer.valueOf(line.substring(44, 47));
            int SIN = Integer.valueOf(line.substring(47, 56));
            String Address = line.substring(56, 99).trim();
            
            return new Tuple(EmpID, LastUpdate, EmName, Gender, Dept,
            		SIN, Address);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
