package bitMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.LongFunction;
import java.util.stream.Stream;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/**
 * 
 * @author TLIN Tianlin Yang
 *
 */

public class bitMapMain {
	
	static int ID1FilesNo = 0;
	static int ID2FilesNo = 0;
	static int bitmapNumberPerLoad = 5000;
	static int finishednumber= 0;
	static int extratReaderNo = 0;
	
	public static void main(String[] args) throws IOException {
		try{
            File file = new File("DeptT1.txt");file.delete();
            file = new File("DeptT2.txt");file.delete();
            file = new File("GenderT1.txt");file.delete();
            file = new File("GenderT2.txt");file.delete();
            file = new File("IDT11.txt");file.delete();
            file = new File("IDT12.txt");file.delete();
            file = new File("IDT13.txt");file.delete();
            file = new File("IDT14.txt");file.delete();
            file = new File("IDT15.txt");file.delete();
            file = new File("IDT16.txt");file.delete();
            file = new File("IDT21.txt");file.delete();
            file = new File("IDT22.txt");file.delete();
            file = new File("IDT23.txt");file.delete();
            file = new File("IDT24.txt");file.delete();
            file = new File("IDT25.txt");file.delete();
            file = new File("IDT26.txt");file.delete();
            file = new File("BitMapIDT1.txt");file.delete();
            file = new File("BitMapIDT2.txt");file.delete();
            file = new File("BitMapIDT1-Encoded.txt");file.delete();
            file = new File("BitMapIDT2-Encoded.txt");file.delete();
            file = new File("GenderT1-Encoded.txt");file.delete();
            file = new File("GenderT2-Encoded.txt");file.delete();
            file = new File("DeptT1-Encoded.txt");file.delete();
            file = new File("DeptT2-Encoded.txt");file.delete();

        }catch(Exception e){
            e.printStackTrace();
        }
		
		System.gc();
		
		System.out.println("------------------Lab2:Bitmap index------------------");
		//System.out.println("Available memory at the beginning is: "+ (Runtime.getRuntime().freeMemory() / 1048576) +" MB");
		System.out.println("------Bitmap Establishment-----");
		System.out.println("");
		long startTime = System.currentTimeMillis();
		ID1FilesNo = bitMapGenerator("", "files/output/", "T1.txt");
		double p1t1 = (System.currentTimeMillis()-startTime)/1000.0;
		//System.out.println("Gender/Dept Bitmap establish time for T1.txt："+p1t1+" ms");
		System.gc();
		startTime = System.currentTimeMillis();
		ID2FilesNo = bitMapGenerator("", "files/output/", "T2.txt");
		double p1t2 = (System.currentTimeMillis()-startTime)/1000.0;
		//System.out.println("Gender/Dept Bitmap establish time for T2.txt："+p1t2+" ms");
		System.out.println("");
		System.out.println("Gender/Dept Bitmap establish time："+(p1t2+p1t2)+" s");
		System.gc();
		//System.out.println("Bitmap ID T1.txt spilt to: "+ID1FilesNo);
		//System.out.println("Bitmap ID T2.txt spilt to: "+ID2FilesNo);

		startTime = System.currentTimeMillis();
		combineBitmap(ID1FilesNo,"BitMapIDT1.txt","IDT1");
		combineBitmap(ID2FilesNo,"BitMapIDT2.txt","IDT2");
		double p1ID = (System.currentTimeMillis()-startTime)/1000.0;
		System.out.println("ID Bitmap establish time："+p1ID+" s");
		System.out.println("");
		
		//--------------------------Bitmap size calculation------------------
		File fileID = new File("BitMapIDT1.txt");
		File fileID2 = new File("BitMapIDT2.txt");
		File fileGender = new File("GenderT1.txt");
		File fileGender2 = new File("GenderT2.txt");
		File fileDept = new File("DeptT1.txt");
		File fileDept2 = new File("DeptT2.txt");
		System.out.println("Uncompressed ID Bitmap size："+(fileID.length()+fileID2.length())+" bytes");
		System.out.println("Uncompressed Gender Bitmap size："+(fileGender.length()+fileGender2.length())+" bytes");
		System.out.println("Uncompressed Department Bitmap size："+(fileDept.length()+fileDept2.length())+" bytes");
		long bitmapSize = (fileID.length()+fileID2.length())+(fileGender.length()+fileGender2.length())+(fileDept.length()+fileDept2.length());
		File fileT1 = new File("T1.txt");
		File fileT2 = new File("T2.txt");
		long fileSize = fileT1.length()+fileT2.length();
		System.out.println("Disk I/O for bitmap generation:"+(fileSize)+" bytes");
		System.out.println("");
		
		
		//ID Compression
		System.out.println("------Bitmap Compression-----");
		System.out.println("");
		startTime = System.currentTimeMillis();
		CompressionByRunLengthEncoding("BitMapIDT1.txt", "BitMapIDT1-Encoded", 8);
		CompressionByRunLengthEncoding("BitMapIDT2.txt", "BitMapIDT2-Encoded", 8);
		CompressionByRunLengthEncoding("GenderT1.txt", "GenderT1-Encoded", 0);
		CompressionByRunLengthEncoding("GenderT2.txt", "GenderT2-Encoded", 0);
		CompressionByRunLengthEncoding("DeptT1.txt", "DeptT1-Encoded", 3);
		CompressionByRunLengthEncoding("DeptT2.txt", "DeptT2-Encoded", 3);
		double pc = (System.currentTimeMillis()-startTime)/1000.0;
		System.out.println("Compressed Bitmap establish time："+pc+" s");
		System.out.println("");
		File fileIDe = new File("BitMapIDT1-Encoded.txt");
		File fileID2e = new File("BitMapIDT2-Encoded.txt");
		File fileGendere = new File("GenderT1-Encoded.txt");
		File fileGender2e = new File("GenderT2-Encoded.txt");
		File fileDepte = new File("DeptT1-Encoded.txt");
		File fileDept2e = new File("DeptT2-Encoded.txt");
		System.out.println("Compressed ID Bitmap size："+(fileIDe.length()+fileID2e.length())+" bytes");
		System.out.println("Compressed Gender Bitmap size："+(fileGendere.length()+fileGender2e.length())+" bytes");
		System.out.println("Compressed Department Bitmap size："+(fileDepte.length()+fileDept2e.length())+" bytes");
		long encodedSize = (fileIDe.length()+fileID2e.length())+(fileGendere.length()+fileGender2e.length())+(fileDepte.length()+fileDept2e.length());
		//System.out.println("Disk I/O for bitmap Compression:"+(bitmapSize)+" bytes");
		System.out.println("");
		
		System.out.println("------Merge T1.txt and T2.txt-----");
		startTime = System.currentTimeMillis();
		MergeOperation();
		double p2 = (System.currentTimeMillis()-startTime)/1000.0;
		System.out.println("Merge total time: " + p2+" s");
		System.out.println("");
		System.out.println("Merge diskI/O: " + (bitmapSize+fileSize)+" bytes");
		System.out.println("");
		System.out.println("------------------------------------------------------");
		System.out.println("Total time: " +(p1t2+p1t2+p2+p1ID)+" s");
		System.out.println("");
		System.out.println("Total disk I/O: " +(bitmapSize+fileSize*2)+" bytes");
		
	}

	public static void CompressionByRunLengthEncoding(String FileName, String outputfilename, int startIndex) throws IOException {
		//ID compression:
		File fileID = new File(FileName);
		Scanner fileScanner = new Scanner(fileID);
		ArrayList<String> oneLoadTemp = new ArrayList<String>();
		int tuplesNoPerLoad = (int) (( Runtime.getRuntime().freeMemory()*0.01)/(100));
		int fileNumber = 0;
		int lineNumber = 0;

		while (fileScanner.hasNext()) {
			System.gc();
			//Refill one load
			readData(fileScanner, oneLoadTemp, tuplesNoPerLoad);
			fileNumber++;
			 if (!oneLoadTemp.isEmpty()) {
					while(!oneLoadTemp.isEmpty()) {
						lineNumber++;
						
						String tempString = oneLoadTemp.remove(0);//Use first line as temp String for operations
						String prefixString = tempString.substring(0,startIndex);
						//System.out.println("tempString:"+tempString);
						//System.out.println("prefixString:"+prefixString);
						int endIndex = tempString.length();
						String encodedString = encodeData(tempString,startIndex,endIndex);
						//System.out.println("encodeDataString:"+tempString);
						//System.out.println("encodeDataString:"+outputString);
						outputEncodedline(prefixString+encodedString, outputfilename);
						//System.out.println("compression lines:"+lineNumber);
					}
			 }
		}
		
	}
	private static void outputEncodedline(String string, String outputfilename) throws IOException {
		// TODO Auto-generated method stub
		FileWriter writer = new FileWriter(outputfilename+".txt",true); 
		writer.write(string + System.lineSeparator());
		
		writer.close();
	}
	
	static String encodeData(String line,int startIndex, int endIndex){
	       //String line = "0000000000000101";
			String tobeEncode = line.substring(startIndex,endIndex);
	        char [] bitmapArray = tobeEncode.toCharArray();
	        ArrayList<Integer> counterArraylist = new ArrayList<>();

	        int count = 0;
	        for(int i=0; i < bitmapArray.length; ++i){
	            char c = bitmapArray[i];
	            if(c == '0')
	                count++;
	            else{
	                counterArraylist.add(count);
	                count = 0;
	            }
	        }

	        String compressLine = "";
	        for (int num:counterArraylist) {
	            String part1 = "";
	            String part2 = new String(Integer.toBinaryString(num));
	            int part2Len = part2.length();
	            for(int i = 0; i<part2Len-1; ++i){
	                part1 = new String (part1 + "1");
	            }
	            part1 = new String(part1 + "0");
	            compressLine = new String(compressLine + part1 + part2);
	        }

	        return compressLine;
	    }
	

	//----------------------Step1-------------------
	public static int bitMapGenerator(String inputPath, String outputPath, String inputName) {
		int tempLineCounter = 0;
        int tempFileNo = 0;
		int tuplesNoPerLoad = (int) (( Runtime.getRuntime().freeMemory()*0.25)/(100*3));
		//int tuplesNoPerLoad = 2;
		File file = new File(inputPath+inputName);
		Scanner fileScanner = null;
		BitSet gendarBitMap = new BitSet();//Label female here, rest is male
		TreeMap<Integer, BitSet> deptBitMap = new TreeMap<Integer, BitSet>();
		TreeMap<Integer, BitSet> idBitMap = new TreeMap<Integer, BitSet>();
		String outputString = null;
		
		try {
			fileScanner = new Scanner(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> oneLoadTemp = new ArrayList<String>();
		int fileNumber = 0;
		while (fileScanner.hasNext()) {
			System.gc();
			//Refill one load
			readData(fileScanner, oneLoadTemp, tuplesNoPerLoad);
        	tempLineCounter=0;
            if (!oneLoadTemp.isEmpty()) {
				while(!oneLoadTemp.isEmpty()) {
	                int lineNumberTemp = tempFileNo*tuplesNoPerLoad + tempLineCounter ;//Get current line number
					String tempString;
					//ID---------------------------------
					int empId;
	                tempString = oneLoadTemp.remove(0);//Use first line as temp String for operations
	                empId = Integer.parseInt(tempString.substring(0, 8));

	                //gender-----------------------------
					int index = (tempString.charAt(43)=='1')?1:0;
	                if(index == 1) {
	                	gendarBitMap.set( lineNumberTemp );}
	                //dept-------------------------------
	                int curDept = Integer.parseInt(tempString.substring(44, 47));//Current dept
	                BitSet bs = deptBitMap.getOrDefault(curDept, null);
	                if(bs!=null) {
	                	bs.set(lineNumberTemp);
	                	deptBitMap.put(curDept,bs);}
	                else {
	                	bs = new BitSet();
	                	bs.set(lineNumberTemp);
	                	deptBitMap.put(curDept,bs);}
	                //create id index
	                BitSet bsId = idBitMap.getOrDefault(empId, null);
	                if(bsId!=null) {
	                	bsId.set(lineNumberTemp);
	                	idBitMap.put(empId,bsId);}
	                else {
	                	bsId = new BitSet();
	                	bsId.set(lineNumberTemp);
	                	idBitMap.put(empId,bsId);}
	                tempLineCounter++;//Go to next line
	                if (idBitMap.size()%bitmapNumberPerLoad==0) {//5000----------------------10mb limitation!!!!!!!!!!!!!!!!!!!!
	                	System.out.println("File split trigger!!!"+tempLineCounter);
	                	fileNumber++;
	                	outputString = inputName.substring(0,2)+fileNumber;
	                	IDBitMapToTXT(idBitMap, outputString);
					}
				}
            }
            
			tempFileNo++;//Go to next temp piece	
		}
		//Write the temp split piece to output bitmap.txt
		fileNumber++;
		IDBitMapToTXT(idBitMap, inputName.substring(0,2)+fileNumber);
        bitmapWritor(deptBitMap, gendarBitMap, inputName);
		//release the memory, ready for next piece.
		deptBitMap.clear();
		idBitMap.clear();
		gendarBitMap.clear();

		//System.out.println("\n  Time to read and sort input file: "+ inpuFile1Name+" is: "+(diskReadTime/Values.NANO_SECOND) + " ms");
		//System.out.println("  Number of temporary files: "+tempFileNo);
		//System.out.println("  Time to write sorted tuples to temporary files is: "+(diskWriteTime/Values.NANO_SECOND) + " ms");
		return fileNumber;
	}

	private static void bitmapWritor(TreeMap<Integer, BitSet> deptBitMap,
			BitSet gendarBitMap, String inputName) {
		// TODO Auto-generated method stub
		DeptBitMapToTXT(deptBitMap, inputName);
		GenderToTXT(gendarBitMap, inputName);
	}

	public static boolean readData(Scanner fs, ArrayList<String> at, int numOfLines) {
		while(numOfLines-- > 0 && fs.hasNext()) {	
				String temp = new String();
				temp = fs.nextLine();
				at.add(temp);
		}
		return true;
	}

	
	
	
	
	

@SuppressWarnings({ "unused", "null" })
public static void combineBitmap(int ID1FilesNo,String Outputfilename,String inputname) throws IOException{
	int batchT1Num = ID1FilesNo;
	String tempT1Data;
	String tempT2Data;
	//System.out.println("Initilize buffers for "+batchT1Num+"T1 files now.");
    BufferedReader[] readersT1 = new BufferedReader[batchT1Num];//new BufferedReader(new FileReader(file),bufferSize);
    //Create buffer for all batch files:
    for(int i=0; i<batchT1Num; ++i){
        String fileName = inputname + (i+1) + ".txt";
        File fileT1 = new File(fileName);
        try {
            readersT1[i] = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
		
    //Get 1st line here:-------------------
    LinkedList<String> TemplineArrayT1 = new LinkedList<String>();
    BufferedReader oneReaderT1 = null;
    List<Integer> fileNoOnReading = new ArrayList<Integer>();
    for (int i = 0; i < readersT1.length; i++) {
    	fileNoOnReading.add(i);
    }
    int remove = 0;
    for (Integer i :  fileNoOnReading) {
    	//System.out.println("i="+i);
    	oneReaderT1 = readersT1[i];
    	String tempString = oneReaderT1.readLine();
    	//System.out.println("File NO."+(i+1)+"Reading the line:"+tempString);
    	if (tempString!=null) {
    		TemplineArrayT1.add(i+tempString);
		}else {
			//readersT1[i].close();
			remove = i;
			//System.out.println(TemplineArrayT1);
			
		}
	}
    fileNoOnReading.remove(remove);
    //System.out.println("TemplineArrayT1："+ TemplineArrayT1);
    if (TemplineArrayT1.size()==0) {
    	//System.out.println("No lines to load now!");
	}
    //System.out.println(TemplineArrayT1);
    
    
    
    
    
    
    
    //--------------------------------------------------------------------------------------------------------------------------
    
    boolean run = true;
    while (run) {
    //System.out.println("TemplineArrayT1: "+TemplineArrayT1);
	//Save All first line at IDT1 batch file to List.
    //ID format 0+0000000+bitmap
	List<String> tempT1IDList = new ArrayList<String>();
	if(TemplineArrayT1 != null) {
		String tempID1String = null;
		int finished = 0;
			for (int i = 0; i < TemplineArrayT1.size(); i++) {
				String tempString = TemplineArrayT1.get(i);
				if (tempString.length()<8) {
					finished = i;
				}else {
				//IDString ID+fileNo.
				tempID1String = TemplineArrayT1.get(i).substring(1,9)+TemplineArrayT1.get(i).substring(0,1);///bug here.........when file done, need relocated
				//System.out.println("---------------tempID1string:"+tempID1String+"  i:"+i);
				tempT1IDList.add(tempID1String);
				}
			}
			
			if (finished+1==TemplineArrayT1.size()) {
				int count= 0;
				for (String string : TemplineArrayT1) {
					if (string.length()<8) {
						count++;
					}
				}
				if (count==TemplineArrayT1.size()) {
					//System.out.println("Finished:"+finished+" TTemplineArrayT1.size() "+TemplineArrayT1.size());
					//System.out.println("All finished!");
					break;
				}
				
			}
		}
	//Sorted ID, find min one is 0
	String combinedBitmapString="";
	Collections.sort(tempT1IDList);
	//Find duplicated
	List<String> duplicated = findRepeat(tempT1IDList);
	//System.out.println("duplicated list:"+duplicated);
	if (duplicated.size()>0) {//duplicated!
		//System.out.println("========================Duplicated!======================");
		
		String samllestID = checkSmallestID(duplicated,tempT1IDList);
		//System.out.println("Smallest ID:"+samllestID);
		
		if (duplicated.contains(samllestID)) {//current line ID:[972258980, 972291601, 972291602]
			
			//System.out.println("current line ID:"+tempT1IDList);
			String combinedString = FindMaxLengthBitmap(duplicated,TemplineArrayT1,samllestID);
			int  combinedStringFileNo = Integer.parseInt(combinedString.substring(0,1));
			
			
			
			combinedBitmapString = combinedString.substring(1);
			//System.out.println("When is smallest, output this first: "+combinedBitmapString);
			extratReaderNo = combinedStringFileNo;
			writMergedBitmap(combinedBitmapString,Outputfilename);
			
			for (String s : duplicated) {//Update duplicated lines, move them to next lines.
				int tempNo = Integer.parseInt(s.substring(8));//Get original data file No.
				extratReaderNo = tempNo;
				//System.out.println("FileNumber="+tempNo);
				run = loadOneLine(extratReaderNo, readersT1, TemplineArrayT1, oneReaderT1);
				continue;
			}
			
		}else {//Duplicated is not smallest:
				//System.out.println("==========================================Duplicated is not smallest==================================================");
				//System.out.println("current line ID:"+tempT1IDList);
				int  samllestIDFileNo = Integer.parseInt(samllestID.substring(8));
				//System.out.println("FileNumber="+samllestIDFileNo);
				combinedBitmapString = TemplineArrayT1.get(samllestIDFileNo).substring(1);
				//System.out.println("When not smallest, output this first: "+combinedBitmapString);
				writMergedBitmap(combinedBitmapString,Outputfilename);
				extratReaderNo = samllestIDFileNo;
				run = loadOneLine(extratReaderNo, readersT1, TemplineArrayT1, oneReaderT1);
			continue;
		}
		
		
		
	}else {
		//System.out.println("current line ID:"+tempT1IDList);
		int x = Integer.parseInt(tempT1IDList.get(0).substring(8));//Get file No.
		//System.out.println("FileNumber="+x);
		combinedBitmapString = TemplineArrayT1.get(x).substring(1);//set min tuple to output.
		//System.out.println("Normal tuple:"+combinedBitmapString);
		extratReaderNo = x;//update output number.
		writMergedBitmap(combinedBitmapString,Outputfilename);
		run = loadOneLine(extratReaderNo, readersT1, TemplineArrayT1, oneReaderT1);
		continue;
	}

    }
}

private static String checkSmallestID(List<String> duplicated, List<String> tempT1IDList) {
	// TODO Auto-generated method stub
	//current line ID:[972258980, 972291601, 972291602]
	int t1 = Integer.parseInt(duplicated.get(0));
	int t2 = Integer.parseInt(tempT1IDList.get(0));//Smallest one
	if (t1>t2) {
		return tempT1IDList.get(0);//972258980
	}else {
	return duplicated.get(0);//972291601
	}
}



private static String FindMaxLengthBitmap(List<String> duplicated, LinkedList<String> templineArrayT1, String samllestID) {
	// TODO Auto-generated method stub
	//get the max length string
			String maxString = "";
			int maxsize=0;
			//System.out.println("tempT1DataList: " + TemplineArrayT1.get(1));

			List<Integer> tempNo = new ArrayList<Integer>();
			
			for (String s: duplicated) {
				tempNo.add(Integer.parseInt(s.substring(8)));//Get original data file No.
			
			}
			String outputString = templineArrayT1.get(Integer.parseInt(samllestID.substring(8))).substring(0);
			for (Integer i: tempNo) {
			String originalData = templineArrayT1.get(i).substring(0);
			int ssize = originalData.length();
			if(ssize>maxsize) {
				 maxsize = ssize;
				 maxString = originalData;
				 outputString = outputString+maxString.substring(outputString.length());
			 }
			}
			
			return outputString;
}



public static List<String> findRepeat(List<String> datas) {
    if (datas instanceof Set) {
        return new ArrayList<>();
    }
    HashSet<String> set = new HashSet<String>();
    List<String> repeatEles = new ArrayList<String>();
    List<String> outputList = new ArrayList<String>();
    for (String t : datas) {
        if (set.contains(t.substring(0,8))) {
            repeatEles.add(t);
        } else {
            set.add(t.substring(0,8));
        }
    }
    if (repeatEles.size()>0) {
    	for (String t : datas) {
        	if (t.contains(repeatEles.get(0).substring(0,8))) {
        		outputList.add(t);
        	}
        }
	}
    
    return outputList;
}




private static void writMergedBitmap(String combinedBitmapString, String outputfilename) throws IOException {
	// TODO Auto-generated method stub
	//System.out.println("CombinedBitmapString:"+combinedBitmapString);
	FileWriter outputWriter = new FileWriter(outputfilename,true);
	outputWriter.write(combinedBitmapString +"\n");
	outputWriter.close();
}

@SuppressWarnings("unused")
private static boolean loadOneLine(int index, BufferedReader[] readersT1, 
		LinkedList<String> TemplineArrayT1,BufferedReader oneReaderT1 ) throws IOException {
	Integer remove = null;
	TemplineArrayT1.remove(index);//release old writed index
    oneReaderT1 = readersT1[index];
    
    String tempString = index+oneReaderT1.readLine();
    	//System.out.println("File NO."+(index+1)+"Reading the line:"+tempString);
    	if (tempString!=null) {
    		TemplineArrayT1.add(index,tempString);
		}else {
			System.out.println("File no."+(index+1)+" finished!");
			finishednumber = (index+1);
		}
    if (TemplineArrayT1.size()==0) {
    	System.out.println("No lines to load now!");
    	return false;
	}
	return true;
}



public static void MergeOperation() throws IOException {
	BufferedReader brIDT1 = new BufferedReader(new FileReader("BitMapIDT1.txt"));
	BufferedReader brIDT2 = new BufferedReader(new FileReader("BitMapIDT2.txt"));
	FileWriter outputWriter = new FileWriter("BitMapMergeFinalOutputG03.txt");
	String TemplineT1;
	String TemplineT2;
	TemplineT1 = brIDT1.readLine();
	TemplineT2 = brIDT2.readLine();
	boolean IDT1Loaded = true;
	boolean IDT2Loaded = true;
	//Get IDT1 line ID, save it(memory cost here)
	do {long tempID1 = 0;
		if(TemplineT1 != null) {String tempID1String = TemplineT1.substring(0,8);tempID1 = Integer.parseInt(tempID1String);}
	//Get IDT2 line ID, save it(memory cost here)
		long tempID2 = 0;
		if(TemplineT2 != null) {String tempID2String = TemplineT2.substring(0,8);tempID2 = Integer.parseInt(tempID2String);}
		   	
		//When both file exist lines
			//When both file exist lines
		if( (TemplineT1 != null) && (TemplineT2 != null)) {
		    	if( tempID1 == tempID2) {//Compare if ID is same
		    		//Extract the line from original file T1
		    		String tempT1Data = ExtractTuple(TemplineT1,"T1");
		    		//Extract the line from original file T2
		    		String tempT2Data = ExtractTuple(TemplineT2,"T2");
	    	        String tempT1DateString = tempT1Data.substring(8,18);
	    	        String tempT2DateString = tempT2Data.substring(8,18);
	    	        //Converter for Date format
	    	        Date tempT1Date = StringDateToSimpleDate(tempT1DateString);
	    	        Date tempT2Date = StringDateToSimpleDate(tempT2DateString);
	    	        //Find newest tuple here, write the tuple to output.txt file.
	    	        if(tempT1Date.compareTo(tempT2Date) > 0) {outputWriter.write(tempT1Data +"\n");}
	    	        else {outputWriter.write(tempT2Data +"\n");}
	    	        //Sensor for detect finished or not.
	    	        TemplineT1 = brIDT1.readLine();
	    	        if( TemplineT1 == null) IDT1Loaded = false;
	    			TemplineT2 = brIDT2.readLine();
	    	        if( TemplineT2 == null) IDT2Loaded = false;
		    	}
		    	else if( tempID1 < tempID2) {//ID1 less than ID2, output ID1 first.
		    		String outputRecord = ExtractTuple(TemplineT1,"T1");
		    		outputWriter.write(outputRecord +"\n");
	    	        TemplineT1 = brIDT1.readLine();
	    	        //Sensor for finished or not
	    	        if( TemplineT1 == null) IDT1Loaded = false;
		    	}
		    	else if( tempID1 > tempID2) {
		    		String outputRecord = ExtractTuple(TemplineT2,"T2");
		    		outputWriter.write(outputRecord +"\n");
	    			TemplineT2 = brIDT2.readLine();
	    			//Sensor for finished or not
	    	        if( TemplineT2 == null) IDT2Loaded = false;
		    	}
	    	}//When only one file has record:
	    	else if( (TemplineT1 != null) && (TemplineT2 == null)) {//Only IDT1 has record:
	    		String outputRecord = ExtractTuple(TemplineT1,"T1");
	    		outputWriter.write(outputRecord +"\n");
    	        TemplineT1 = brIDT1.readLine();
    	        //Sensor for finished or not
    	        if( TemplineT1 == null) IDT1Loaded = false;
	    	}
	    	else if( (TemplineT2 != null) && (TemplineT1 == null) ) {//Only IDT2 has record:
	    		String outputRecord = ExtractTuple(TemplineT2,"T2");
	    		outputWriter.write(outputRecord +"\n");
    			TemplineT2 = brIDT2.readLine();
    			//Sensor for finished or not
    	        if( TemplineT2 == null) IDT2Loaded = false;
	    	}
	    }while( IDT1Loaded || IDT2Loaded );
        outputWriter.close();
	}
	
	private static Date StringDateToSimpleDate(String tempT1DateString) {
		 Date tempT1Date = null; 
			try {
				tempT1Date = new SimpleDateFormat("yyyy-MM-dd").parse(tempT1DateString);
			} catch (ParseException e) {
				e.printStackTrace();
			}
	return tempT1Date;
}



	public static String ExtractTuple(String BitMapLine, String fileName) {
		
    	String bitmapID = BitMapLine.substring(8,BitMapLine.length());
    	ArrayList<Integer> tuplesLineNumberList = new ArrayList<Integer>();
    	//Find bitmap line position of number '1'---which as handle to original tuple data
    	for(int i=0; i< bitmapID.length(); i++) {
    		if( bitmapID.charAt(i) == '1') {//if "0", add position, if "1" save the line number to list.
    			tuplesLineNumberList.add(i+1);
    		}
    	}
    	
		String tempTuple="";//temp for save the output
    	for(int j=0; j<tuplesLineNumberList.size(); j++) {//Seek all of duplicates tuples.
    		try{
    			RandomAccessFile randReader = new RandomAccessFile(fileName +".txt", "rw");
    			String readRecord;			
    			try (Stream<String> lines = Files.lines(Paths.get(fileName +".txt"))) {
    				readRecord = lines.skip(tuplesLineNumberList.get(j)-1).findFirst().get();
    			}
    	        if(!tempTuple.isEmpty()){
	    	        String CurrentDateString = readRecord.substring(8,18);
	    	        String tempDateString = tempTuple.substring(8,18);
	    	        Date cDate = new SimpleDateFormat("yyyy-MM-dd").parse(CurrentDateString);
	    	        Date tempcDate = new SimpleDateFormat("yyyy-MM-dd").parse(tempDateString);
	    	        
	    	        if(cDate.compareTo(tempcDate) > 0) {//Update to the most recently tuple.
	    	        	tempTuple = readRecord;
	    	        }
	    	        
    	        }
    	        else {
	    	        tempTuple = readRecord;
    	        }
    	        
    	    } 
    	    catch(IOException e){
    	    	System.out.println(e);
    	    } 
    		catch (ParseException e) {
				e.printStackTrace();
			}
    	
    	}
		return tempTuple;
		
	}
	
	public static void IDBitMapToTXT( Map<Integer, BitSet> idBitset, String inpuFile1Name ){
		PrintWriter printBitmapWriter = null;
		try {
			printBitmapWriter = new PrintWriter(new FileWriter("ID" + inpuFile1Name+".txt",true ));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Iterator<Map.Entry<Integer, BitSet>> itr = idBitset.entrySet().iterator(); 
        
        while(itr.hasNext()) 
        { 
//			String f = new String();
			
            Map.Entry<Integer, BitSet> entry = itr.next();
            BitSet iTemp = entry.getValue();
            //Print ID at first 8 chars.
			printBitmapWriter.print(String.format("%1$08d", entry.getKey()));
			//Get the bitmap 0000001 like this.
			StringBuilder tempBitmap = new StringBuilder();
		    for(int i = 0; i< iTemp.length(); i++) {
				tempBitmap.append(iTemp.get(i)?'1':'0');
			}
		    //Compression the bitmap-------------------------------------------------------
		    //RunLengthEncoding compression = new RunLengthEncoding();
		    //String outputString  = compression.encode(tempBitmap.toString());
		    
		    //Get the final result
		    printBitmapWriter.print(tempBitmap.toString());
			printBitmapWriter.print("\n");
			itr.remove();
        } 

		printBitmapWriter.close(); 
	}
	
	
	
	public void writeData(ArrayList<String> mergedTuples, int n, String fileName)
            throws IOException {

    	FileWriter writer = new FileWriter(fileName,true); 
		for(String str: mergedTuples) {
		  writer.write(str + System.lineSeparator());
		}
		writer.close();
    	
    }

	public static void DeptBitMapToTXT( Map<Integer, BitSet> deptBitset, String inpuFile1Name ){
		PrintWriter printBitmapWriter = null;
		try {
			printBitmapWriter = new PrintWriter(new FileWriter("Dept" + inpuFile1Name,true ));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Iterator<Map.Entry<Integer, BitSet>> itr = deptBitset.entrySet().iterator(); 
        
        while(itr.hasNext()) //Ignore rest of 0 for length match, inorder save I/O
        { 
			
            Map.Entry<Integer, BitSet> entry = itr.next();
            BitSet dTemp = entry.getValue();
			printBitmapWriter.print(String.format("%1$03d", entry.getKey()));

		    for(int i = 0; i< dTemp.length(); i++) {
				printBitmapWriter.print(dTemp.get(i)?'1':'0');
			}
			printBitmapWriter.print("\n");
			itr.remove();
        } 

		printBitmapWriter.close(); 
	}
	
	public static void GenderToTXT(BitSet female , String inpuFile1Name ){
		PrintWriter printBitmapWriter = null;
		String f = new String();
		try {
			printBitmapWriter = new PrintWriter(new FileWriter("Gender" + inpuFile1Name,true ));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0; i<female.length(); i++) {
			f += female.get(i)?'1':'0';
		}
		printBitmapWriter.println(f);
		printBitmapWriter.close(); 
	}
	
	

}
