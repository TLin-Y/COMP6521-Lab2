package BitmapSolution2;

import java.io.IOException;

public class TwoWayMergeDriver {
    public static void main(String [] args) throws IOException {

        TwoWayMerge twoWayMerge = new TwoWayMerge();
        String inputFileName1 = "T1.txt";
        String outputFileName1 = "sorted_runs_T1.txt";

        String inputFileName2 = "T2.txt";
        String outputFileName2 = "sorted_runs_T2.txt";

        String mergedFileName = "merged.txt";

        int runCount1 = twoWayMerge.loadSortAllBlocks(inputFileName1, outputFileName1);
        int runCount2 = twoWayMerge.loadSortAllBlocks(inputFileName2, outputFileName2);

//        int runCount1 = 20;
//        int runCount2 = 10;
        long start = System.currentTimeMillis();
        twoWayMerge.merge(outputFileName1, outputFileName2, runCount1, runCount2, mergedFileName);
        long finish = System.currentTimeMillis();

        System.out.println("Merge phase elapsed time:");
        System.out.println((finish - start) + " ms \n");
    }
}
