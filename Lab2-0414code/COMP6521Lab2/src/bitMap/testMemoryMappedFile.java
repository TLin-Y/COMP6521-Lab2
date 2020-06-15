package bitMap;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class testMemoryMappedFile {
	
	static ArrayList<String> Searchedlist = new ArrayList<String>();
	
	public static void main(String[] args) throws IOException {
		ArrayList<String> oneLoadTemp = new ArrayList<String>();
		//load line 2-11.
		File f1 = new File("T1.txt");
		File f2 = new File("T2.txt");
		long startTime = System.currentTimeMillis();
		MapReading("T1.txt", oneLoadTemp, 0, f1.length());
		MapReading("T2.txt", oneLoadTemp, 0, f2.length());
		double T = (System.currentTimeMillis()-startTime)/1000.0;
		System.out.println("Total time: " + T+" s");
		System.out.println(oneLoadTemp.size());
//		while(!oneLoadTemp.isEmpty()) {
//			String tempString;
//            tempString = oneLoadTemp.remove(0);
//            System.out.println(tempString);
//            }
	}
     
    public static ArrayList<String> MapReading(String inputName,ArrayList<String> oneLoadTemp,long startposition,long buffersize) throws IOException{
        RandomAccessFile memoryMappedFile = new RandomAccessFile(inputName,"r");
        //int size =(int)memoryMappedFile.length();
        MappedByteBuffer out = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_ONLY,startposition,buffersize);
        long start = System.currentTimeMillis();
        //要根据文件行的平均字节大小来赋值
        final int extra = 200;
        int count = extra;
        byte[] buf = new byte[count];
        int j=0;
        char ch ='\0';
        boolean flag = false;
        while(out.remaining()>0){
        	
        	
            byte by = out.get();//read one line
            ch =(char)by;
            switch(ch){
                case '\n':
                    flag = true;
                    break;
                case '\r':
                    flag = true;
                    break;
                default:
                    buf[j] = by;
                    break;
            }
            j++;
            //读取的字符超过了buf 数组的大小，需要动态扩容
            if(flag ==false && j>=count){
                count = count + extra;
                buf = copyOf(buf,count);
            }
            if(flag==true){
                //这里的编码要看文件实际的编码
                String line = new String(buf,"utf-8");//Decode the 1st line
                String searchID = line.substring(0,8);
                if (Searchedlist.contains(searchID)==false) {
                	Searchedlist.add(searchID);
                	findIDduplicatesThanOutputBitmap(searchID);
				}
                
                //System.out.println(line);
                //oneLoadTemp.add(line);
                flag = false;
                buf = null;
                count = extra;
                buf = new byte[count];
                j =0;
            }
                 
        }
        //处理最后一次读取
        if(j>0){
            String line = new String(buf,"utf-8");
            String searchID = line.substring(0,8);
            if (Searchedlist.contains(searchID)==false) {
            	Searchedlist.add(searchID);
            	findIDduplicatesThanOutputBitmap(searchID);
			}
            //System.out.println(line);
            //oneLoadTemp.add(line);
        }
         
        long end = System.currentTimeMillis();
        //System.out.println("耗时:"+(end-start));
        memoryMappedFile.close();
        out.clear();
		return oneLoadTemp;
         
    }
     
    private static void findIDduplicatesThanOutputBitmap(String searchID) throws IOException {
		// TODO Auto-generated method stub
    	File f1 = new File("T1.txt");
    	File f2 = new File("T2.txt");
    	ArrayList<Integer>  linenumT1 = MapDupReading("T1.txt",searchID, 0, f1.length());
    	ArrayList<Integer>  linenumT2 = MapDupReading("T2.txt",searchID, 0, f2.length());
//		while(!linenumT1.isEmpty()) {
//		int tempString;
//        tempString = linenumT1.remove(0);
//        System.out.println("ID:"+searchID+" in T1:"+tempString);
//        }
//		while(!linenumT2.isEmpty()) {
//			int tempString;
//	        tempString = linenumT2.remove(0);
//	        System.out.println("ID:"+searchID+" in T2:"+tempString);
//	        }
	}

	public static ArrayList<Integer> MapDupReading(String inputName,String searchID,long startposition,long buffersize) throws IOException{
		RandomAccessFile memoryMappedFile = new RandomAccessFile(inputName,"r");
        //int size =(int)memoryMappedFile.length();
        MappedByteBuffer out = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_ONLY,startposition,buffersize);
        long start = System.currentTimeMillis();
        //要根据文件行的平均字节大小来赋值
        final int extra = 200;
        int count = extra;
        byte[] buf = new byte[count];
        int j=0;
        char ch ='\0';
        boolean flag = false;
        int LineNumber = 0;
        
        
        ArrayList<Integer> outputLinenum = new ArrayList<Integer>();
        
        while(out.remaining()>0){
        	
            byte by = out.get();//read one line
            ch =(char)by;
            switch(ch){
                case '\n':
                	LineNumber++;
                    flag = true;
                    break;
                case '\r':
                    flag = true;
                    break;
                default:
                    buf[j] = by;
                    break;
            }
            j++;
            //读取的字符超过了buf 数组的大小，需要动态扩容
            if(flag ==false && j>=count){
                count = count + extra;
                buf = copyOf(buf,count);
            }
            if(flag==true){
                //这里的编码要看文件实际的编码
                String line = new String(buf,"utf-8");//Decode the 1st line
                String tempID = line.substring(0,8);
                if (Integer.parseInt(tempID)==Integer.parseInt(searchID)) {//-----------------------------find duplicates--------------------------
                	outputLinenum.add(LineNumber);
//                	System.out.println("Duplicated line:"+LineNumber+" for ID:"+tempID);
				};
                
                //System.out.println(line);
                //oneLoadTemp.add(line);
                flag = false;
                buf = null;
                count = extra;
                buf = new byte[count];
                j =0;
            }
                 
        }
        //处理最后一次读取
        if(j>0){
        	LineNumber++;
            String line = new String(buf,"utf-8");
            String tempID = line.substring(0,8);
            if (Integer.parseInt(tempID)==Integer.parseInt(searchID)) {//-----------------------------find duplicates--------------------------
            	outputLinenum.add(LineNumber);
            	//System.out.println("Duplicated line:"+LineNumber+" for ID:"+tempID);
			};
            //System.out.println(line);
            //oneLoadTemp.add(line);
        }
         
        long end = System.currentTimeMillis();
        //System.out.println("耗时:"+(end-start));
        memoryMappedFile.close();
        out.clear();
		return outputLinenum;
         
    }
	//扩充数组的容量
    public static byte[] copyOf(byte[] original,int newLength){
        byte[] copy = new byte[newLength];
        System.arraycopy(original,0,copy,0,Math.min(original.length,newLength));
        return copy;
    }
 
}