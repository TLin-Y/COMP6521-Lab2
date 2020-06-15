package Lab1;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class DataWriter extends BufferedWriter {
    public int ioCounter = 0;
    public boolean isNewBatch = true;

    public DataWriter(File file) throws IOException {
        super(new java.io.FileWriter(file));
    }

    //write one batch back to file
    public void writeOneBatch(List<Tuple> oneBatch, Byte tupleNumInOneBlock) {
        try {
            if (oneBatch != null) {
                if (oneBatch.size() % tupleNumInOneBlock == 0)
                    ioCounter += Math.floorDiv(oneBatch.size(), tupleNumInOneBlock);
                else
                    ioCounter += Math.floorDiv(oneBatch.size(), tupleNumInOneBlock) + 1;
                for (Tuple tuple : oneBatch) {
                    if (isNewBatch) {
                        this.write(tupleParser(tuple));
                        isNewBatch = false;
                    } else {
                        this.newLine();
                        this.write(tupleParser(tuple));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //parse tuple to string format
    public String tupleParser(Tuple tuple) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-mm-dd");
        return String.format("%1$8d", tuple.EmpID) +
                dateFormatter.format(tuple.LastUpdate) +
                String.format("%1$-25s", tuple.EmName) +
                String.format("%1$1d", tuple.Gender) +
                String.format("%1$3d", tuple.Dept) +
                String.format("%1$9d", tuple.SIN) +
                String.format("%1$-43s", tuple.Address);
    }

}
