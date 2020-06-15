package Lab1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;



public class DataProcessorDriver {

	public static int counter = 0;
	public static int diskWrite = 0;
	public static int diskRead = 0;
    public static long diskWriteTimer = 0;
    public static long diskReadTimer = 0;
    public static long diskSortTimer = 0;
	
	public static void batchWriter(List<String> StringL, int Counter) throws IOException{
		FileWriter writer = new FileWriter("Batch"+Counter+".txt"); 
		for(String str: StringL) {
		  writer.write(str + System.lineSeparator());
		  diskWrite++;
		}
		writer.close();
	}
	public static int calculateAvailableMemory() {
		System.gc();
		Runtime runtime = Runtime.getRuntime();
		long usedMemory = runtime.totalMemory() - runtime.freeMemory();
		/// System.out.println((runtime.maxMemory() - usedMemory));
		return (int) (runtime.maxMemory() - usedMemory);
	}
	
	
	
	static void whenReadWithFileChannel_thenCorrect(String fileName,int size,int P1Memratio)
            throws IOException {

		File file = new File(fileName);
        BufferedReader reader = null;
        int bufferSize = size*P1Memratio/100;//Optimization------------------------------------
        if (bufferSize > file.length()) {
        	System.out.println("Out of file size, reset buffer size!");
            bufferSize = (int) file.length();
        }
        
        //File information
        System.out.println("Read line by line: ");
        reader = new BufferedReader(new FileReader(file));
        String tempString = null;
        List<String> subList = new ArrayList<>();
        System.out.println("File size(bytes) : "+(int) file.length());
        System.out.println("Buffer size(bytes) : "+bufferSize);
        System.out.println("Before load to buffer Avilable MEM : "+calculateAvailableMemory());
        
     // Data record
        long start = System.currentTimeMillis();
        long startTime = 0;
        int fileCounter = 0;
        String data;

        startTime = System.currentTimeMillis();
        int line=0;
        //Start
        while ((tempString = reader.readLine()) != null) {
        	//System.out.println("Line:"+line);
        	line++;
        	subList.add(tempString);
        	//System.out.println("sub list size : "+subList.size());
            if(subList.size()==(bufferSize/101)){
            	fileCounter++;//Pointer to find buffer not full size.
            	counter++;
            	diskRead++;
            	
            	 //Read time
            	diskReadTimer += System.currentTimeMillis() - startTime;
            	startTime = System.currentTimeMillis();
            	//Sort time
            	Collections.sort(subList);
            	diskSortTimer += System.currentTimeMillis() - startTime;
            	startTime = System.currentTimeMillis();
            	//Write time
            	batchWriter(subList, counter);
            	diskWriteTimer += System.currentTimeMillis() - startTime;
            	startTime = System.currentTimeMillis();
                
            	System.out.println("Batch "+counter+" file generated! Tuples number: "+subList.size()+" Blocks number: "+subList.size()/40);
                subList = new ArrayList<>();
            }
        }
        if (subList.size()>=1) {
        	fileCounter++;//Pointer to find buffer not full size.
        	counter++;
        	diskRead++;
        	
        	 //Read time
        	diskReadTimer += System.currentTimeMillis() - startTime;
        	startTime = System.currentTimeMillis();
        	//Sort time
        	Collections.sort(subList);
        	diskSortTimer += System.currentTimeMillis() - startTime;
        	startTime = System.currentTimeMillis();
        	//Write time
        	batchWriter(subList, counter);
        	diskWriteTimer += System.currentTimeMillis() - startTime;
        	startTime = System.currentTimeMillis();
            
            System.out.println("Batch "+counter+" file generated! Tuples number: "+subList.size()+" Blocks number: "+subList.size()/40);
            subList = new ArrayList<>();
		}
        System.out.println("Disk reading number lines: " + line);
        System.out.println("Disk writing number(tuples number): " + diskWrite);
        System.out.println("Phase1 current file time: " + (System.currentTimeMillis()-start)+"ms");
        reader.close();
        System.out.println("After Phase1 Avilable MEM : "+calculateAvailableMemory());
	    
        
    }
    /**
     * Fast way to load a large file. load 10M (100,000 tuples) into memory
     * @return An array of String, containing 100,000 tuples
     * @throws IOException
     */
    static String [] whenReadWithFileChannel_thenCorrect2(String fileName,int size,int P1Memratio)
            throws IOException {
        String file = fileName;
        RandomAccessFile reader = new RandomAccessFile(file, "r");
        FileChannel channel = reader.getChannel();

        //100,000 tuples at one time
        int bufferSize = size*P1Memratio/100;//Optimization------------------------------------
        if (bufferSize > channel.size()) {
            bufferSize = (int) channel.size();
        }
        System.out.println("File size(bytes) : "+(int) channel.size());
        System.out.println("Buffer size(bytes) : "+bufferSize);
        ByteBuffer buff = ByteBuffer.allocate(bufferSize);
        channel.read(buff);
        System.out.println("After load to buffer Avilable MEM : "+calculateAvailableMemory());
        //buff.flip();
        // While read
        long start = System.currentTimeMillis();
        long startTime = 0;
        int fileCounter = 0;
        String data;
        String [] tuplesArray = null;
        while(true){
        	data = null;//release mem!
        	tuplesArray = null;//release mem!
        	
        	fileCounter++;//Pointer to find buffer not full size.
        	//If buffer is 20MB, file remain is 10MB than reset buffer size to diminish operations.
        	if (Math.ceil(((double)channel.size())/(double)bufferSize) == fileCounter) {
        		int Oldbuffer = bufferSize;
                bufferSize = (int)channel.size()-bufferSize*(fileCounter-1);
                if(Oldbuffer != bufferSize) {
                System.out.println("Final "+fileCounter+"th run reset the buffer size to: "+bufferSize);}
                buff = ByteBuffer.allocate(bufferSize);
            }
        	diskRead++;
	        
	        //Reader
	        startTime = System.currentTimeMillis();
	        int eof = channel.read(buff);//Add 1x memory
	        if(eof == -1 ) break;
	        
	        counter++;
	        buff.flip();
	        buff.clear();
	        
	        System.out.println("Before transfer to string Avilable MEM : "+calculateAvailableMemory());
	        data = new String(buff.array()); 
	        tuplesArray = data.split("\n");//5x Memory cost!
	        
	        diskReadTimer += System.currentTimeMillis() - startTime;
	        //System.out.println("After transfer to string Avilable MEM : "+calculateAvailableMemory());
	        //Sorter
	        startTime = System.currentTimeMillis();
	        Collections.sort(Arrays.asList(tuplesArray));
	        diskSortTimer += System.currentTimeMillis() - startTime;
	        System.out.println("Batch "+counter+" sorting finished!");
	        //System.out.println("After Sorting Avilable MEM : "+calculateAvailableMemory());
	        //Writer
	        startTime = System.currentTimeMillis();
	        //batchWriter(tuplesArray, counter);
	        diskWriteTimer += System.currentTimeMillis() - startTime;
	        System.out.println("Batch "+counter+" file generated! Tuples number: "+bufferSize/101+" Blocks number: "+bufferSize/(101*40));
	        //System.out.println("After writing Avilable MEM : "+calculateAvailableMemory());
        }
        System.out.println("Disk reading number(n+1 times for if break): " + diskRead + " time: " + diskReadTimer +"ms");
        System.out.println("Disk writing number(tuples number): " + diskWrite +" time: " + diskWriteTimer +"ms");
        System.out.println("Tuples sorting time: " + diskSortTimer +"ms");
        System.out.println("Phase1 total time: " + (System.currentTimeMillis()-start)+"ms");
        channel.close();
        reader.close();
        diskRead = 0; 
        diskWrite = 0;
		return tuplesArray;
    }
    public int returnC() {
    	return counter;
    }
    
}