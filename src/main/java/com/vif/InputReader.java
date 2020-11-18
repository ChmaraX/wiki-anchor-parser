package com.vif;

import net.intelie.tinymap.TinyMapBuilder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

public class InputReader {

    private final BufferedReader reader;
    private FileOutputStream out1 = null;
    private FileOutputStream out2 = null;

    private final FileInputStream fileInputStream;

    public InputReader(String fileIn, String fileOut1, String fileOut2) throws IOException {
        this.fileInputStream = new FileInputStream(new File(fileIn));
        this.reader = new BufferedReader(new InputStreamReader(fileInputStream), 1000 * 8192);
        this.out1 = new FileOutputStream(new File(fileOut1));
        this.out2 = new FileOutputStream(new File(fileOut2));
    }

    public InputReader(String fileIn, String fileOut1) throws IOException {
        this.fileInputStream = new FileInputStream(new File(fileIn));
        this.reader = new BufferedReader(new InputStreamReader(fileInputStream), 1000 * 8192);
        this.out1 = new FileOutputStream(new File(fileOut1));
    }

    public void processPages(Integer pageCount) throws IOException {
        int i = 0;
        // read certain number of lines or till end
        while (pageCount == -1 || i < pageCount) {
            i++;
            String page = readRawPage();

            if (page == null) {
                return;
            }

            Parser p = new Parser(page);
            String title = p.getTitle();
            ArrayList<String> anchors = p.getAnchors();
            boolean isRedirect = p.isRedirect();

            // write only if anchors found
            if (anchors != null) {
                IOUtils.write(title + "\t" + StringUtils.join(anchors, "\t") + "\t" + isRedirect + "\n", out1, "UTF-8");
            }

        }
        reader.close();
        fileInputStream.close();
    }

    public static String formatSize(long v) {
        if (v < 1024) return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double)v / (1L << (z*10)), " KMGTPE".charAt(z));
    }

    public void processOutputFile(Integer recordCount) throws IOException {
        Hashmap hm = new Hashmap(); // Hashmaps for statistics
        Parser p = new Parser();
        String line;
        int i = 0;

        // read certain number of lines or till end
        while (recordCount == -1 && (line = reader.readLine()) != null || i < recordCount && (line = reader.readLine()) != null) {
            i++;
            if (i % 100000 == 0) {
                long heapSize = Runtime.getRuntime().totalMemory();
                long heapMaxSize = Runtime.getRuntime().maxMemory();
                long heapFreeSize = Runtime.getRuntime().freeMemory();

                System.out.println("heapFreesize " + formatSize(heapSize) + " / " + formatSize(heapMaxSize) + "free (" + formatSize(heapFreeSize) + ")");
                System.out.println(i);
            }
            p.parseLineToHashMap(line, hm, i);
        }

        writeLinkFrequencies(hm.getAnchorLinkTinyHM(), hm.getRedirectSMM(), out1);
        writeLinkFrequencies(hm.getAnchorTextTinyHM(), out2);

    }

    // write link document and collection frequency (+ if its a redirect) to file
    public void writeLinkFrequencies(TinyMapBuilder<String, Freqs> hashmap,
                                     FileOutputStream output) throws IOException {
        int docFreq_timesUsed = 0;
        int collFreq_timesUsed = 0;

        for (String key : hashmap.keySet()) {
            collFreq_timesUsed = hashmap.get(key).getColFreq();
            docFreq_timesUsed = hashmap.get(key).getDocFreq();

            IOUtils.write(key + "\t" + docFreq_timesUsed + "\t" + collFreq_timesUsed + "\n", output, "UTF-8");
        }
    }

    // write link document and collection frequency (+ if its a redirect) to file
    public void writeLinkFrequencies(TinyMapBuilder<String, Freqs> hashmap,
                                     TinyMapBuilder<String, Boolean> redirectMultiMap,
                                     FileOutputStream output) throws IOException {
        int docFreq_timesUsed = 0;
        int collFreq_timesUsed = 0;

        for (String key : hashmap.keySet()) {
            collFreq_timesUsed = hashmap.get(key).getColFreq();
            docFreq_timesUsed = hashmap.get(key).getDocFreq();
            String isRedirect = "";

            if (redirectMultiMap.containsKey(key)) {
                isRedirect = String.valueOf(redirectMultiMap.get(key));
            }

            IOUtils.write(key + "\t" + docFreq_timesUsed + "\t" + collFreq_timesUsed + "\t" + isRedirect + "\n", output, "UTF-8");
        }
    }

    public String readRawPage() throws IOException  {

        StringBuilder sb = new StringBuilder();
        String line = reader.readLine();

        if (line == null || line.startsWith("</mediawiki>")) {
            return null;
        }

        while (!line.startsWith("</page>")) {
            sb.append(line).append("\n");
            line = reader.readLine().trim();
        }

        sb.append(line).append("\n");
        return sb.toString();
    }

    public void createStatistics() throws IOException {
        int docCountSum = 0;
        int colCountSum = 0;
        int totalCount = 0;
        int redirectCount = 0;
        int maxDocCount = 0;
        int maxColCount = 0;
        String maxDocLine = "";
        String maxColLine = "";
        String line;

        while ((line = reader.readLine()) != null) {
            String[] cols = line.split("\t");
            int docCount = Integer.parseInt(cols[1]);
            int colCount = Integer.parseInt(cols[2]);
            boolean isRedirect;

            if (cols.length > 3) {
                isRedirect = Boolean.parseBoolean(cols[3]);
                if (isRedirect) {
                    redirectCount++;
                }
            }

            if (docCount >= maxDocCount) {
                maxDocCount = docCount;
                maxDocLine = line;
            }

            if (colCount >= maxColCount) {
                maxColCount = colCount;
                maxColLine = line;
            }

            docCountSum = docCountSum + docCount;
            colCountSum = colCountSum + colCount;
            totalCount++;
        }

        float avgDocFreq = (float) docCountSum / (float) totalCount;
        float avgColFreq = (float) colCountSum / (float) totalCount;

        IOUtils.write("Average Link Document Freq." + "\t" + avgDocFreq + "\n", out1, "UTF-8");
        IOUtils.write("Average Link Collection Freq." + "\t" + avgColFreq + "\n", out1, "UTF-8");
        IOUtils.write("Max. Link Document Freq." + "\t" + maxDocLine + "\n", out1, "UTF-8");
        IOUtils.write("Max. Link Collection Freq." + "\t" + maxColLine + "\n", out1, "UTF-8");
        IOUtils.write("Redirect Count" + "\t" + redirectCount + "\n", out1, "UTF-8");

    }


}
