package BitmapSolution2;

import java.io.IOException;

public class BitmapCompressorDriver {
    public static void main(String [] args) throws IOException {
        BitmapCompressor bitmapCompressor = new BitmapCompressor();
        bitmapCompressor.compressBitmapFile("BitMapIDT1.txt");
        bitmapCompressor.compressBitmapFile("BitMapIDT2.txt");

        bitmapCompressor.compressBitmapFile("GenderT1.txt");
        bitmapCompressor.compressBitmapFile("GenderT2.txt");

        bitmapCompressor.compressBitmapFile("DeptT1.txt");
        bitmapCompressor.compressBitmapFile("DeptT2.txt");
    }
}
