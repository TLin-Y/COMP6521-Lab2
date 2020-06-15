package Lab1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.*;

public class Main {
    public static String inputFileName1 = "T1.txt";
    public static String inputFileName2 = "T2.txt";
    public static int MemoryMB = 10;//Memory Limitation
    public static int OutputBatchNum = 0;
    public static byte tupleNumInOneBlock = 40;//Number of tuples in one block
    public static int P1MEMRatio = 25;//%25 heap size optimized
    public static int P2MEMratio = 15;//%20 heap size optimized
    public static int IO = 0;
    public static double ptime1 = 0;
    public static double ptime2 = 0;
    
    public static void main(String[] args) throws IOException {
        long startTime = System.nanoTime();
        System.out.println("-------------------------------------------------------------");
        System.out.println("Target file: "+inputFileName1+" "+inputFileName2);
        int cmem = calculateAvailableMemory();
        System.out.println("Avaliable Memory(bytes) :"+cmem);
        if (cmem/1000000>=10) {//Size allow larger buffer.
        	//P1MEMRatio += 5;
            P2MEMratio -= 5;
		}
        PhaseOne();
        PhaseTwo();
        
        System.out.println("-------------------------------------------------------------");
        System.out.printf("All finished! The total time is: %.2f(s) %n", ptime1+ptime2);
        int l1 = IO/101;
        System.out.println("Total I/O is(without final output): "+IO+" bytes, "+IO/101+" tuples,"+IO/(40*101)+" I/O.");
        File file3 = new File("sorted.txt");
        IO += file3.length();
        
        long fileLength = file3.length();
		LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file3));
		lineNumberReader.skip(fileLength);
        int lines = lineNumberReader.getLineNumber();
        //System.out.println("Total number of lines : " + lines);
        lineNumberReader.close();
        System.out.println("Total I/O is(with final output): "+IO+" bytes, "+(l1+lines)+" tuples,"+IO/(40*101)+" I/O.");
        System.out.println("Output sorted.txt has: "+file3.length()+" bytes, "+lines+" lines(tuples).");
        /*
        System.out.println("-------------------------------------------------------------");
        System.out.println("Solution2 start!");
        DataMerge2 s = new DataMerge2();
        s.start(20*10000);
        System.out.println("Solution2 finished!");
        */
        
    }

    private static void PhaseOne() {//Fast vision to directly operate 10MB size tuples.
    	System.out.println("-------------------------------------------------------------");
        System.out.println("Phase 1: separating and sorting start!");
        System.out.println("-------------------------------------------------------------");
        DataProcessorDriver inputReader = null;
        DataWriter outputWriter = null;
        File file1 = new File(inputFileName1);
        File file2 = new File(inputFileName2);
        long startTime = System.nanoTime();
        
        try {
            inputReader = new DataProcessorDriver();
            // Repeatedly fill the M buffers with new tuples form whole file, then sorted output
            System.gc();
            DataProcessorDriver.whenReadWithFileChannel_thenCorrect(inputFileName1,MemoryMB*1010000,P1MEMRatio);
            System.out.println("-------------- File "+inputFileName1+" finshed!--------------");
            DataProcessorDriver.whenReadWithFileChannel_thenCorrect(inputFileName2,MemoryMB*1010000,P1MEMRatio);
            System.out.println("-------------- File "+inputFileName2+" finshed!--------------");
            System.out.println("Phase 1 Finished!");
            
            System.out.println("Total read time is: "+DataProcessorDriver.diskReadTimer/1000.0+" s");
            System.out.println("Total write time is: "+DataProcessorDriver.diskWriteTimer/1000.0+" s");
            System.out.println("Total sort time is: "+DataProcessorDriver.diskSortTimer/1000.0+" s");
            ptime1 = ((System.nanoTime() - startTime) / 1000000000.0);
            System.out.printf("2 files finished! The total time is: %.2f(s) %n", ptime1);
            long fsize = 2*(file1.length()+file2.length());
            System.out.println("Total I/O(half in,half out) is: "+fsize+" bytes, "+fsize/101+" tuples,"+fsize/(40*101)+" I/O.");
            IO+=fsize;
            int BatchNumber = DataProcessorDriver.counter;
            OutputBatchNum = BatchNumber;
            //System.out.println("Batch number: "+BatchNumber);
            
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputWriter != null) {
                try {
                    outputWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //System.out.printf("Phase1 Total Time: %.2f(s) %n", ((System.nanoTime() - startTime1) / 1000000000.0));
        //System.out.printf("Number Of I/O Read = %d, Number Of I/O Write = %d %n%n", diskReadCounter, diskWriteCounter);
    }
    
    
    

    private static void PhaseTwo() throws IOException {
    	File file1 = new File(inputFileName1);
        File file2 = new File(inputFileName2);
        
        
    	System.out.println("-------------------------------------------------------------");
    	System.out.println("Phase 2: Merge files and update the duplicated items start!");
    	System.out.println("-------------------------------------------------------------");
    	long startTime = System.currentTimeMillis();
        DataMerge dataMerge = new DataMerge(OutputBatchNum, MemoryMB);
        dataMerge.merge(P2MEMratio);
        //File file3 = new File("sorted.txt");
        long fsize = file1.length()+file2.length();
        ptime2=((System.currentTimeMillis()-startTime-dataMerge.diskWriteTime)/1000.0);
        System.out.println("Phase2 total time: "+ptime2+"s");
        System.out.println("Total I/O is: "+fsize+" bytes, "+fsize/101+" tuples,"+fsize/(40*101)+" I/O.");
        IO+=fsize;
        
}
    
    
    
    public static int calculateAvailableMemory() {
		System.gc();
		Runtime runtime = Runtime.getRuntime();
		long usedMemory = runtime.totalMemory() - runtime.freeMemory();
		/// System.out.println((runtime.maxMemory() - usedMemory));
		return (int) (runtime.maxMemory() - usedMemory);
	}
    
    
    
    
    public static String inputPath = System.getProperty("user.dir")+"/";
    public static String outputPath = System.getProperty("user.dir")+"/";

    public static float preserveMemory1 = 0.20f;
    public static float preserveMemory2 = 0.0f;
    public static float preserveMemory3 = 0.12f;
    public static short maxFilesToMerge = 80;//TODO:increase to save time
    

    @SuppressWarnings("unused")
	private static void PhaseOne2() {//Slow, 40 tuples - 1 block, including conversion of the data structure.
        System.out.println("Phase 1: separating and sorting start!");
        long startTime1 = System.nanoTime();
        int diskReadCounter = 0;
        int diskWriteCounter = 0;
        DataReader inputReader = null;
        DataWriter outputWriter = null;

        try {
            inputReader = new DataReader(new File(inputPath + inputFileName1), preserveMemory1);
            short batchCounter = 0;
            long diskReadTimer = 0;
            long diskWriteTimer = 0;
            int BlockCounter = 0;
            // Repeatedly fill the M buffers with new tuples form whole file
            while (!inputReader.finish) {
                System.gc();
                ArrayList<Tuple> oneBatch = new ArrayList<>();

                long startTime = System.nanoTime();

                //fill blocks in one batch until run out of memory
                
                System.out.printf("Now is block number: ");
                while (true) {
                	BlockCounter++;
                	System.out.println(BlockCounter);
                    List<Tuple> oneBlock = inputReader.getOneBlock();
                    //finish read or no left memory
                    if (oneBlock.isEmpty()) {
                        break;
                    }
                    oneBatch.addAll(oneBlock);
                }
                diskReadTimer += System.nanoTime() - startTime;
                // Sort the batch
                if (!oneBatch.isEmpty()) {
                	System.out.printf("Quick sort now......");
                    quickSort(oneBatch, 0, oneBatch.size() - 1);
                    System.out.printf("Quick sort finished!");
                    // Dump the batch to a file
                    startTime = System.nanoTime();
                    batchCounter++;
                    outputWriter = new DataWriter(new File(String.format(outputPath + "%d.txt", batchCounter)));
                    outputWriter.writeOneBatch(oneBatch, tupleNumInOneBlock);
                    diskWriteCounter += outputWriter.ioCounter;
                    outputWriter.close();
                    diskWriteTimer += System.nanoTime() - startTime;
                }
            }
            System.out.printf("Phase 1 Finished: Batch# = %d, IO Read Time = %.2f(s), " +
                            "IO Write Time = %.2f(s) %n", batchCounter, diskReadTimer / 1000000000.0,
                    diskWriteTimer / 1000000000.0);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputReader != null) {
                diskReadCounter = inputReader.ioCounter;
                try {
                    inputReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputWriter != null) {
                try {
                    outputWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.printf("Total Time: %.2f(s) %n", ((System.nanoTime() - startTime1) / 1000000000.0));
        System.out.printf("Number Of I/O Read = %d, Number Of I/O Write = %d %n%n", diskReadCounter, diskWriteCounter);
    }

    
    // quick sort base on EmpID
    public static void quickSort(List<Tuple> batch, int low, int high) {
        int i = low, j = high;
        Tuple pivot = batch.get(low + (high - low) / 2);
        while (i <= j) {

            while (batch.get(i).EmpID < pivot.EmpID) {
                i++;
            }
            while (batch.get(j).EmpID > pivot.EmpID) {
                j--;
            }
            if (i <= j) {
                Tuple temp = batch.get(i);
                batch.set(i, batch.get(j));
                batch.set(j, temp);
                i++;
                j--;
            }

        }
        if (low < j) {
            quickSort(batch, low, j);
        }
        if (i < high) {
            quickSort(batch, i, high);
        }
    }

}