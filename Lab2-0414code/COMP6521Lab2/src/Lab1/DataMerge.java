package Lab1;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.*;

public class DataMerge {

    private static BufferedReader [] readers;
    
    //counter for I/O
	public static int diskWrite = 0;
	public static int diskRead = 0;
	public static long diskWriteTime = 0;
	public static long diskreadTime = 0;
	public static int duplicatedN = 0;
    //stores pointers to the next block to load from each batch file
    private static int [] batchFileBlockPointers;

    //stores # of blocks of each batch file
    private static int [] batchFileBlocksCount;

    //# of batch files
    int batchFileNum;

    //ArrayList of 10 blocks loaded from the head of each batch files
    private ArrayList<List<String>>blocksList;

    //ArrayList of sorted tuples to be output to file
    private ArrayList<String> sortedTuplesList;

    private int outputTuplesCount = 0;

    //default memoryLimit 10M, unit MB
    private int memoryLimit = 10;

    private int bytesPerTuple = 101;

    private int tuplesPerBlock = 40;
    public int totalReadLines=0;

    /**
     *
     * @param batchNum # of sorted batch files to merge
     * @param memoryLimit memory limit, unit in MB
     */
    public DataMerge(int batchNum, int memoryLimit){
    	File file = new File("sorted.txt");
    	file.delete();

        this.memoryLimit = memoryLimit;

        this.batchFileNum = batchNum;

        //pointers to the head block of each file
        batchFileBlockPointers = new int [batchNum];
        batchFileBlocksCount = new int [batchNum];
        //file readers
        System.out.println("Initilize buffers for "+batchNum+" files now.");
        readers = new BufferedReader[batchNum];//new BufferedReader(new FileReader(file),bufferSize);
        
        for(int i=0; i<batchNum; ++i){
            String fileName = "Batch" + (i+1) + ".txt";
            File file2 = new File(fileName);
            try {
            	
                readers[i] = new BufferedReader(new FileReader(fileName));
                
                long fileLength = file2.length();
				LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(file2));
    			lineNumberReader.skip(fileLength);
    	        int lines = lineNumberReader.getLineNumber();
    	        //System.out.println("Total number of lines : " + lines);
    	        lineNumberReader.close();
                //System.out.println("file length:"+file2.length()+"batchFileBlocksCount[i]: "+(int)(file2.length()/bytesPerTuple/tuplesPerBlock));
                batchFileBlocksCount[i] = lines/40;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        //10 empty blocks, not loaded yet
        blocksList = new ArrayList<>();
        for(int i=0; i<this.batchFileNum; ++i){
            blocksList.add(new LinkedList<>());
        }

        sortedTuplesList = new ArrayList<>();
    }

    /**
     * loadOneBlockFromFile
     * @param batchNum to load from which batch
     * @param blockSize # of tuples in one block
     * @param blockNum to load which block in the batch.txt
     * @return the loaded block
     * @throws IOException
     */
    /*
private List<String> loadOneBlockFromFile(int batchNum, int blockSize, int blockNum) throws IOException {
    	
        RandomAccessFile reader = readers[batchNum];
        FileChannel channel = reader.getChannel();

        //100,000 tuples at one time
        int tupleSize = 101;
        int channelPosition = tupleSize * blockSize * blockNum;
        channel.position(channelPosition);

        int bufferSize = tupleSize * blockSize;
        if (bufferSize > channel.size()) {
            bufferSize = (int) channel.size();
        }

        ByteBuffer buff = ByteBuffer.allocate(bufferSize);
        channel.read(buff);
        buff.flip();

        String blockData;
        String [] tuplesArray = null;
        blockData = new String(buff.array());
        tuplesArray = blockData.split("\n");
        List<String> tuplesList = new LinkedList<>(Arrays.asList(tuplesArray));
        return tuplesList;
    }*///File channel wrong on windows.

    
    
    private List<String> loadOneBlockFromFile(int batchNum, int blockSize, int blockNum) throws IOException {
    	BufferedReader reader = readers[batchNum];//Here not end, each time continuing!
    	//blockSize = 40 tuples.
        //100,000 tuples at one time
        
        String tempString = null;
        List<String> tuplesList = new LinkedList<>();
        //int line = 0;
        while ((tempString = reader.readLine()) != null) {
        	tuplesList.add(tempString);
        	//System.out.println(tempString);
        	//line++;
        	//System.out.println("sub list size : "+subList.size());
            if(tuplesList.size()==blockSize){
            	//System.out.println("Now tuplesList loaded: "+line+"size: "+tuplesList.size());
            	break;
            }
        
		}
        return tuplesList;
    }

    /**
     * check if there are still more blocks can be load from the specified batch file
     * @param batchFileIndex
     * @return
     */
    private boolean moreToLoad(int batchFileIndex){
        if(batchFileBlockPointers[batchFileIndex] < batchFileBlocksCount[batchFileIndex])
            return true;
        else
            return false;
    }

    /**
     * check if the loaded head block of the corresponding batch file is empty
     * @param batchFileIndex
     * @return
     */
    private boolean blockIsEmpty(int batchFileIndex){
        List<String> block = blocksList.get(batchFileIndex);
        if(block.size() == 0) return true;
        else return false;
    }

    /**
     * load blocks from each batch file
     * get and remove the min tuple from the loaded blocks
     * add the min tuple to output buffer list
     * when buffer full, out put
     * @return
     * @throws IOException
     */
    public void merge(int MEMratio) throws IOException{
    	long startTime = System.currentTimeMillis();
        while(mergeIter(MEMratio)){

        }
        long sortTime = System.currentTimeMillis() - startTime-diskreadTime-diskWriteTime;
        System.out.println("Final merge files generated!");
        System.out.println("Disk read number: "+diskRead*tuplesPerBlock+" tuples, "+diskRead*tuplesPerBlock*101+" bytes,"+diskRead*tuplesPerBlock*101/(40*101)+" I/O.");
        System.out.println("Disk write number: "+outputTuplesCount+" tuples "+outputTuplesCount*101+" bytes"+outputTuplesCount*101/(40*101)+" I/O.");
        System.out.println("-------------- "+batchFileNum+" files finshed!--------------");
        System.out.println("Phase 2 Finished!");
        System.out.println("Find duplicated lines: " + duplicatedN);
        System.out.println("Disk read time: "+diskreadTime/1000.0+" s");
        System.out.println("Disk write time: "+diskWriteTime/1000.0+" s");
        System.out.println("Sorting and update duplicated tuples time: " + sortTime/1000.0 + "s");
    }

    public String lastTupleBackup=null;
    
    private boolean mergeIter(int MEMratio) throws IOException{
    	int tuplesInOutputBufferCount =
                ((this.memoryLimit * 1000000 - this.batchFileNum * tuplesPerBlock * bytesPerTuple)/bytesPerTuple)*MEMratio/100;
        //System.out.println("Tuples in output buffer are: "+tuplesInOutputBufferCount);
    	//go through each head block and check if loading needed
        for(int i = 0; i<this.batchFileNum; ++i){

            //block is empty && there are more blocks on the batch file can be loaded
            if(blockIsEmpty(i) && moreToLoad(i)){
                //load/fill the block
            	long startTime = System.currentTimeMillis();
                List<String> block = loadOneBlockFromFile(i, 40, batchFileBlockPointers[i]);
                diskreadTime += -startTime + System.currentTimeMillis();
                diskRead++;
                blocksList.set(i, block);

                //update the pointer position
                batchFileBlockPointers[i] = batchFileBlockPointers[i] + 1;
            }

//            else if(!blockIsEmpty(i)){//block is not empty
//                //not need to load the block again
//
//            }else{//block is empty && there are no more blocks on the batch file can be loaded
//                //skip this block, no more to load
//            }
        }

        //get the head of each block and compare
        //get the smallest tuple, remove it from its block
        String tuple = minTupleOfBlocks();
      //all the blocks are empty, no more tuples can be loaded now.
        if(tuple == null){
        	//System.out.println("Last tuple...");
            //output all the rest sorted tuples
        	long startTime = System.currentTimeMillis();
            writeToFile(sortedTuplesList, outputTuplesCount);
            diskWriteTime += -startTime + System.currentTimeMillis();
            diskWrite++;
            outputTuplesCount = outputTuplesCount + sortedTuplesList.size();
            sortedTuplesList.clear();
            return false;
        }

        //add to the sorted list to be output
        if(sortedTuplesList.size() == 0) {
        	//-----------------Bug fix here, compare last back up with new tuple-----------------
        	if (lastTupleBackup!=null) {//if have backup
        		int prevId = getId(lastTupleBackup);
                int curId = getId(tuple);

                if(curId > prevId){
                    sortedTuplesList.add(tuple);
                } else if (curId == prevId){
                	//System.out.println("Last Tuple Backup trigle!:");
                	duplicatedN++;
                    long prevTimestamp = getUnixTime(lastTupleBackup);
                    long curTimestamp = getUnixTime(tuple);

                    if(curTimestamp >= prevTimestamp) {
                    	//---------------Bug2 fixed!, need replace the older file last line-----------------
                    	deleteSortedFileLastLine();
                    	diskRead--;
                        sortedTuplesList.add(tuple);}
                    else{
                    	//System.out.println("Discard new tuple, it's older than privious one!"+curTimestamp+" "+prevTimestamp);
                        //discard the cur tuple not add it
                        //do nothing
                    }
                }else {//less than, put it to back-ward position.
    				System.out.println("Less then backup tuple bug!");
    			}
			}else {
				sortedTuplesList.add(tuple);
			}
            
        }else{

            //compare the tuple with the tuple at the end of the list
            String lastTuple = sortedTuplesList.get(sortedTuplesList.size() - 1);
            int prevId = getId(lastTuple);
            int curId = getId(tuple);

            if(curId > prevId){//Larger, put it as last element
                sortedTuplesList.add(tuple);
            } else if (curId == prevId){//Equal, put it checking duplicated
            	duplicatedN++;
                long prevTimestamp = getUnixTime(lastTuple);
                long curTimestamp = getUnixTime(tuple);

                if(curTimestamp >= prevTimestamp)
                    sortedTuplesList.set(sortedTuplesList.size()-1, tuple);
                else{
                    //discard the cur tuple not add it
                    //do nothing
                }
            }else {//less than, put it to back-ward position.
            	System.out.println("normal less than bug");
			}
        }

      
        
        //tuplesInOutputBufferCount -= 100;   //soft boundary
        //System.out.println("Buffer size: "+tuplesInOutputBufferCount);
        // around 90000
        //System.out.println(sortedTuplesList.size());
        
        if(sortedTuplesList.size() > tuplesInOutputBufferCount){//Reach buffer size limitation!!!
        	
        	//System.out.println("Buffer size: "+tuplesInOutputBufferCount+"currunt sorted tuples list size:"+sortedTuplesList.size());
        	long startTime = System.currentTimeMillis();
            writeToFile(sortedTuplesList, outputTuplesCount);
            diskWriteTime += -startTime + System.currentTimeMillis();
            diskWrite++;
            outputTuplesCount = outputTuplesCount + sortedTuplesList.size();
            //------------------------------Bug--------------------------------------------------
            lastTupleBackup = sortedTuplesList.get(sortedTuplesList.size() - 1);
            //Need consider last tuple for next new sorted tuples list for next 1st element!!!!
            
            sortedTuplesList.clear();//clear the current sort slot.
            System.out.printf(".");
        }
        return true;
    }

    private void deleteSortedFileLastLine() throws IOException {
		// TODO Auto-generated method stub
    	RandomAccessFile f = new RandomAccessFile("sorted.txt", "rw");
    	long length = f.length() - 1;
    	byte b = f.readByte();
    	while(b != 10){                     
    	  length -= 1;
    	  f.seek(length);
    	  b = f.readByte();
    	}
    	f.setLength(length+1);
    	f.close();
	}

	/**
     * get the min Tuple of 10 blocks, and remove it from the head of its block
     * @return the min tuple
     */
    private String minTupleOfBlocks(){

        ArrayList<List<String>> nonEmptyBlocksList = new ArrayList<>();
        for(List<String> block: blocksList){
            if(block.size() > 0)
                nonEmptyBlocksList.add(block);
        }

        if(nonEmptyBlocksList.size() == 0){
            //all the blocks are empty now
            return null;
        }


        String minTuple = nonEmptyBlocksList.get(0).get(0);//Sorted block 1st element is min.
        List<String> minTupleBlock = nonEmptyBlocksList.get(0);

        for(List<String>block: nonEmptyBlocksList){
            String tuple = block.get(0);
            if(getId(tuple) < getId(minTuple)){

                minTuple = tuple;
                minTupleBlock = block;

            }else if(getId(tuple) == getId(minTuple)){

                long tupleTimestamp = getUnixTime(tuple);
                long minTupleTimestamp = getUnixTime(minTuple);

                if(tupleTimestamp > minTupleTimestamp){
                    //this tuple is newer than the previous min tuple
                    minTuple = tuple;
                    minTupleBlock = block;
                }
            }else {
            	//donothing, keep min as min
            	//System.out.println("get min tuple less than bug!");
            }
        }

        minTupleBlock.remove(0);
        return minTuple;
    }


    /**
     * Get the id of the tuple
     * @param tupleStr
     * @return
     */
    private int getId(String tupleStr){
        String id = tupleStr.substring(0, 8);
        int idInt = Integer.parseInt(id);
        return idInt;
    }

    /**
     * get the unix timestamp of the tuple
     * @param tupleStr
     * @return
     */
    private long getUnixTime(String tupleStr){
        String dateString = tupleStr.substring(8, 18);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        long unixTime = 0;

        try {
            Date date = formatter.parse(dateString);
            unixTime = date.getTime() / 1000;
        }catch (Exception e){
            e.printStackTrace();
        }

        return unixTime;
    }

    /**
     * Write all the merged tuples into files
     * @param mergedTuples the list of merged tuples to be output to file
     * @param n start output line position in the txt file
     * @throws IOException
     */
    public void writeToFile(ArrayList<String> mergedTuples, int n)
            throws IOException {

    	FileWriter writer = new FileWriter("sorted.txt",true); 
		for(String str: mergedTuples) {
		  writer.write(str + System.lineSeparator());
		}
		writer.close();
    	
    }

}