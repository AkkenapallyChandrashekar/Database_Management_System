import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Chandrashekar Akkenapally
 *
 */

public class MergeSort {
    public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();
        System.out.println("Start program");
        int fileCount = splitAndSortData("input/dataset.csv");
        System.out.println("splitted data into files" + fileCount);
        mergeFiles(fileCount);
        splitDataIntoFiles("mergeSortOutput/output.csv");
        System.out.println("splitted into individual again");
        long endTime = System.nanoTime();
        System.out.println("Time to sort and split files"+ (endTime - startTime) / 1000000000.0);
        linearSearch(fileCount);
    }

    public static void linearSearch(int fileCount) throws Exception {
        System.out.println("linear search begin");
        long searchStart = System.nanoTime();
        outer: for (int i = 1; i <= fileCount; i++) {
            BufferedReader br = new BufferedReader(new FileReader("mergeSortOutput/File" + i + ".csv"));
            String line = "";
            while ((line = br.readLine()) != null) {
                String title = getListFromLine(line).get(25);
                if (title.equals("\"Sandman: Dream Hunters 30th Anniversary Edition\"")) {
                    long searchEnd = System.nanoTime();
                    System.out.println((searchEnd - searchStart) / 1000000000.0);
                    System.out.println("found title\n");
                    System.out.println(line);
                    break outer;
                }
            }
            br.close();
        }
        long searchEnd = System.nanoTime();
        System.out.println("Time with linear search"+ (searchEnd - searchStart) / 1000000000.0);
    }

    public static void mergeFiles(int fileCount) throws Exception {
        List<BufferedReader> list = new ArrayList<>();
        for (int i = 1; i <= fileCount; i++) {
            list.add(new BufferedReader(new FileReader("mergeSortOutput/File" + i + ".csv")));
        }
        System.out.println("starting merge\n\n");
        String line = "";
        List<List<String>> partitions = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        // preload first lines of all files into titles
        for (int i = 0; i < list.size(); i++) {
            line = list.get(i).readLine();
            List<String> parts = getListFromLine(line);
            titles.add(parts.get(25));
            partitions.add(i, parts);
        }
        FileWriter fileWriter = new FileWriter("mergeSortOutput/output.csv");
        // repeat untill all titles are merged into file
        while (titles.size() > 0) {
            int minIndex = findMin(titles);
            fileWriter.write(getWritableLine(partitions.get(minIndex)) + "\n");
            titles.remove(minIndex);
            partitions.remove(minIndex);
            String newLine;
            if ((newLine = list.get(minIndex).readLine()) != null) {
                List<String> parts = getListFromLine(newLine);
                titles.add(minIndex, parts.get(25));
                partitions.add(minIndex, parts);
            }
        }
        fileWriter.close();
        for (BufferedReader br : list) {
            br.close();
        }
    }

    public static int findMin(List<String> titles) {
        int minIndex = 0;
        String minElement = titles.get(0);
        for (int i = 0; i < titles.size(); i++) {
            if (titles.get(i).compareTo(minElement) < 0) {
                minElement = titles.get(i);
                minIndex = i;
            }
        }
        return minIndex;
    }

    public static List<String> getListFromLine(String input) {
        List<String> result = new ArrayList<String>();
        int start = 0;
        boolean inQuotes = false;
        for (int current = 0; current < input.length(); current++) {
            if (input.charAt(current) == '\"')
                inQuotes = !inQuotes; // toggle state
            else if (input.charAt(current) == ',' && !inQuotes) {
                result.add(input.substring(start, current));
                start = current + 1;
            }
        }
        result.add(input.substring(start));

        return result;
    }

    public static int splitAndSortData(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line, previousLine = null;
            int count = 0, limit = 100000, fileNumber = 0, linesRead = 0;
            List<List<String>> data = new ArrayList<>();

            while ((line = br.readLine()) != null) {
                linesRead++;
                if (linesRead == 1 && !fileName.equals("mergeSortOutput/output.csv"))
                    continue;
                if (count == limit) {
                    fileNumber++;
                    sortAndWrite(data, fileNumber);
                    count = 0;
                    data = new ArrayList<>();
                    System.out.println("sorted " + fileNumber + " files");
                }
                if (previousLine != null) {
                    line = previousLine + line;
                }
                List<String> result = getListFromLine(line);
                if (result.size() != 28) {
                    previousLine = line;
                } else {
                    previousLine = null;
                    data.add(result);
                }
                count++;
            }
            if (!data.isEmpty()) {
                sortAndWrite(data, ++fileNumber);
            }
            return fileNumber;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return 0;
    }

    private static void sortAndWrite(List<List<String>> data, int fileNumber) throws Exception {
        data.sort((l1, l2) -> l1.get(25).compareTo(l2.get(25)));
        FileWriter fileWriter = new FileWriter("mergeSortOutput/File" + fileNumber + ".csv");
        for (List<String> list : data) {
            fileWriter.write(getWritableLine(list) + '\n');
        }
        fileWriter.close();
    }

    private static int splitDataIntoFiles(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            int count = 0, limit = 100000, fileNumber = 1;
            FileWriter fileWriter = new FileWriter("splitDataIntoFiles/File" + fileNumber + ".csv");
            while ((line = br.readLine()) != null) {
                if (count == limit) {
                    fileWriter.close();
                    fileNumber++;
                    count = 0;
                    fileWriter = new FileWriter("splitDataIntoFiles/File" + fileNumber + ".csv");
                }
                fileWriter.write(line + "\n");
                count++;
            }
            fileWriter.close();
            return fileNumber;
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return 0;
    }

    public static String getWritableLine(List<String> list) {
        return list.stream().collect(Collectors.joining(","));
    }
}