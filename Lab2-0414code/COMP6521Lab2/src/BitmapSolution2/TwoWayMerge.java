package BitmapSolution2;

import java.io.IOException;
import java.util.*;

public class TwoWayMerge {

    //private int runSize = 50000;
    private int runSize = 5000;

    public List<String> sortOneBlock(String oneBlock) throws IOException {
        //sort the block
        DataProcessor dataProcessor = new DataProcessor(oneBlock);
        List<String>blockLineList = dataProcessor.getBlockLineList();
        Collections.sort(blockLineList);
        return blockLineList;
    }

    public void writeSortedListToFile(String outputFileName, int linePos, List<String> sortedList) throws IOException{
        DataIO dataIO = new DataIO();
        dataIO.writeBitmapToFile(outputFileName, linePos, sortedList);
    }

    public int loadSortAllBlocks(String inputFileName, String outputFileName) throws IOException {
        int linePos = 0;
        int numTuples = runSize;
        int lineSize = 101;

        int runCount = 0;
        //load one block
        DataIO dataIO = new DataIO();
        String oneBlock = dataIO.readBlockFromFile(inputFileName, linePos, numTuples, lineSize);
        while(!oneBlock.equals("")){
            List<String> sortedList = sortOneBlock(oneBlock);
            writeSortedListToFile(outputFileName, linePos, sortedList);
            runCount++;
            linePos += numTuples;
            oneBlock = dataIO.readBlockFromFile(inputFileName, linePos, numTuples, lineSize);
        }
        return runCount;
    }

    /**
     * @param fileName  the name of the file storing the sorted lists
     * @param runId     which run to load
     * @param runSize   number of tuples in each sorted run
     * @param blockId   of each run, the index of the block to load
     * @param numTuples num of tuples in each block to load
     * @param lineSize  size of the tuple's line
     */
    public LinkedList<String> loadOneSortedBlock(String fileName, int runId, int runSize, int blockId, int numTuples, int lineSize) throws IOException{
        LinkedList<String> sortedList = new LinkedList<>();
        //there are still tuples in a sorted run can be loaded
        if(blockId * numTuples < runSize){
            int start = blockId * numTuples;    //inclusive
            int end = Math.min((blockId + 1) * numTuples, runSize); //exclusive
            numTuples = end - start;
            start = runId * runSize + start;

            DataIO dataIO = new DataIO();
            String blockData = dataIO.readBlockFromFile(fileName, start, numTuples, lineSize);

            DataProcessor dataProcessor = new DataProcessor(blockData);
            sortedList = new LinkedList<>(dataProcessor.getBlockLineList());
        }
        return sortedList;
    }

    public void merge(String sortedFileName1, String sortedFileName2, int runCount1, int runCount2, String outputFileName) throws IOException{
        ArrayList<LinkedList<String>> sortedRunsList = new ArrayList<>();
        ArrayList<Integer>blockIdList = new ArrayList<>();
        for(int i=0; i<(runCount1 + runCount2); ++i)
            blockIdList.add(0);
        int numTuples = 1000;
        int lineSize = 102;

        // load the header block from each run
        for(int i=0; i<runCount1; ++i){
            int blockId = blockIdList.get(i);
            LinkedList<String> oneBlock = loadOneSortedBlock(sortedFileName1, i, runSize, blockId, numTuples, lineSize);
            //blockIdList.set(i, blockId + 1);
            sortedRunsList.add(oneBlock);
        }

        for(int i=0; i<runCount2; ++i){
            int blockId = blockIdList.get(runCount1 + i);
            LinkedList<String> oneBlock = loadOneSortedBlock(sortedFileName2, i, runSize, blockId, numTuples, lineSize);
            //blockIdList.set(runCount1 + i, blockId + 1);
            sortedRunsList.add(oneBlock);
        }

        LinkedList<String> outputTuplesBuffer = new LinkedList<>();
        //String minEmployerId = sortedRunsList.get(0).peek().substring(0,8);
        String minEmployerId = "99999999";
        int minEmployerIdFromRunIndex = 0;
        int outputTuplesCount = 0;

        DataIO dataIO = new DataIO();

        boolean allRunsEmpty = false;
        while(!allRunsEmpty){

            allRunsEmpty = true;
            minEmployerId = "99999999";
            minEmployerIdFromRunIndex = -1;
            //loop through each runs
            for(int i=0; i<sortedRunsList.size(); ++i){

                //get the loaded block of one sorted run
                LinkedList<String> oneBlock = sortedRunsList.get(i);

                if(oneBlock.size() > 0){
                    allRunsEmpty = false;   //not all runs are empty
                    String line = oneBlock.peek();
                    String employerId = line.substring(0, 8);
                    if(employerId.compareTo(minEmployerId) < 0){
                        minEmployerId = new String(employerId);
                        minEmployerIdFromRunIndex = i;
                    }
                }else {
                    //load new block from file
                }
            }

            if(allRunsEmpty){
                // remove duplciates
                outputTuplesBuffer = removeDuplicates(outputTuplesBuffer);
                //TODO: write the rest of buffer to file
                if(outputTuplesBuffer.size() > 0)
                    dataIO.writeBitmapToFile(outputFileName, outputTuplesCount, outputTuplesBuffer);
                break;
            }

            //put the min to buffer to be written to file
            LinkedList<String> oneBlock = sortedRunsList.get(minEmployerIdFromRunIndex);
            String line = oneBlock.poll();

            //if the block is empty, reload from file
            if(oneBlock.isEmpty()){
                String fileName = sortedFileName1;
                if(minEmployerIdFromRunIndex >= runCount1) fileName = sortedFileName2;

                int runId = minEmployerIdFromRunIndex;
                if(minEmployerIdFromRunIndex >= runCount1) runId = minEmployerIdFromRunIndex - runCount1;

                int blockId = blockIdList.get(minEmployerIdFromRunIndex);
                blockId += 1;
                blockIdList.set(minEmployerIdFromRunIndex, blockId);

                //load the new block
                oneBlock = loadOneSortedBlock(fileName, runId, runSize, blockId, numTuples, lineSize);
            }

            sortedRunsList.set(minEmployerIdFromRunIndex, oneBlock);
            outputTuplesBuffer.add(line);

            if(outputTuplesBuffer.size() >= 10000){
                //remove duplicates
                outputTuplesBuffer = removeDuplicates(outputTuplesBuffer);
                //TODO: write to file
                dataIO.writeBitmapToFile(outputFileName, outputTuplesCount, outputTuplesBuffer);
                outputTuplesCount += outputTuplesBuffer.size();
                outputTuplesBuffer.clear();
            }
        }
    }

    public LinkedList<String> removeDuplicates(LinkedList<String> list){
        String tuple;
        BitmapGenerator bitmapGenerator = new BitmapGenerator();
        String lastBitmapIndex = "0";
        String curBitmapIndex;

        LinkedList<String> noDuplicateList = new LinkedList<>();

        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            tuple = it.next();
            String employerId = tuple.substring(0, 8);
            curBitmapIndex = bitmapGenerator.getBitmapIndexOfEmployerId(employerId);

            if(!curBitmapIndex.equals(lastBitmapIndex)){
                noDuplicateList.add(tuple);
                lastBitmapIndex = curBitmapIndex;
            }else {
                String lastTuple = noDuplicateList.getLast();
                if(tuple.compareTo(lastTuple) > 0){
                    noDuplicateList.removeLast();
                    noDuplicateList.add(tuple);
                }

            }
        }
        return noDuplicateList;
    }
}
