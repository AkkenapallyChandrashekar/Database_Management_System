import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;

/**
 * @author Chandrashekar Akkenapally
 *
 */

public class HashIndex {
    static int fileCount = 12, limit = 100000, rowSize = 28, titleIndex = 25;;

    public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();
        BufferedReader br = new BufferedReader(new FileReader("input/dataset.csv"));
        String line;
        int linesRead = 0;
        List<FileWriter> fileWriters = new ArrayList<>();
        List<Map<Integer, String>> hashTable = new ArrayList<>();
        for (int i = 0; i < fileCount; i++) {
            fileWriters.add(new FileWriter("hashIndexOutput/File" + (i + 1) + ".csv"));
            hashTable.add(new HashMap<>());
        }
        while ((line = br.readLine()) != null) {
            linesRead++;
            List<String> list = MergeSort.getListFromLine(line);
            if (list.size() != rowSize) {
                continue;
            }
            String title = list.get(titleIndex);
            int fileNumber = Math.abs((title.hashCode() % fileCount));
            fileWriters.get(fileNumber).write(line + "\n");
            hashTable.get(fileNumber).put(title.hashCode(), line);
            if (linesRead % limit == 0) {
                System.out.println("processed " + (linesRead / limit) + " lakh lines");
            }
        }
        for (FileWriter fileWriter : fileWriters) {
            fileWriter.close();
        }
        br.close();
        long endTime = System.nanoTime();
        System.out.println("Time to build hash index " + (endTime - startTime) / (1000000000.0));
        String title = "\"Sandman: Dream Hunters 30th Anniversary Edition\"";
        searchTitle(title, hashTable);
    }

    private static void searchTitle(String title, List<Map<Integer, String>> hashTable) throws Exception {
        System.out.println("start search");
        long startTime = System.nanoTime();
        int fileNumber = Math.abs((title.hashCode() % fileCount));
        if (hashTable.get(fileNumber).containsKey(title.hashCode())) {
            System.out.println("found title\n" + hashTable.get(fileNumber).get(title.hashCode()));
        } else {
            System.out.println("title not found");
        }
        long endTime = System.nanoTime();
        System.out.println("Time to search " + (endTime - startTime) / (1000000000.0));
    }
}
