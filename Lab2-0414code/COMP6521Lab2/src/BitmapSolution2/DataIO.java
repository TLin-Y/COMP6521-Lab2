package BitmapSolution2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DataIO {

    DataIO(){}

    public String readBlockFromFile(String fileName, int linePos, int numTuples, int lineSize) throws IOException {

        RandomAccessFile reader = new RandomAccessFile(fileName, "r");
        FileChannel channel = reader.getChannel();

        channel.position(linePos * lineSize);

        int bufferSize = lineSize * numTuples;
        if (bufferSize > channel.size()) {
            bufferSize = (int) channel.size();
        }

        ByteBuffer buff = ByteBuffer.allocate(bufferSize);
        int num = channel.read(buff);
        buff.flip();

        channel.close();
        reader.close();

        if(num == -1) return "";
        else return new String(buff.array());
    }


    public void writeBitmapToFile(String fileName, int linePos, List<String> bitmapList) throws IOException{

        RandomAccessFile writer = new RandomAccessFile(fileName, "rw");
        FileChannel channel = writer.getChannel();

        String bitmapListStr = String.join("\r\n", bitmapList);
        bitmapListStr = new String(bitmapListStr + "\r\n");

        ByteBuffer buff = ByteBuffer.wrap(bitmapListStr.getBytes(StandardCharsets.UTF_8));
        int pos = (bitmapList.get(0) + "\r\n").length() * linePos;
        channel.write(buff, pos);
    }

    public int writeCompressedBitmapToFile(String fileName, int pos, List<String> compressedBitmapList) throws IOException{

        RandomAccessFile writer = new RandomAccessFile(fileName, "rw");
        FileChannel channel = writer.getChannel();

        String bitmapListStr = String.join("\r\n", compressedBitmapList);
        bitmapListStr = new String(bitmapListStr + "\r\n");

        ByteBuffer buff = ByteBuffer.wrap(bitmapListStr.getBytes(StandardCharsets.UTF_8));

        channel.write(buff, pos);
        pos += bitmapListStr.length();
        return pos;
    }

}
