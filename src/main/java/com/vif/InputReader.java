package com.vif;

import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

public class InputReader {

    private final BufferedReader reader;
    private FileOutputStream out = null;
    private final FileInputStream fileInputStream;

    public InputReader(String fileIn, String fileOut) throws IOException {
        this.fileInputStream = new FileInputStream(new File(fileIn));
        this.reader = new BufferedReader(new InputStreamReader(fileInputStream), 1000 * 8192);
        this.out = new FileOutputStream(new File(fileOut));
    }

    public void proccessPages(Integer pageCount) throws IOException {
        int i = 0;
        // read certain number of lines or till end
        while (pageCount == -1 || i < pageCount) {
            i++;
            String page = readRawPage();

            Parser p = new Parser(page);
            String title = p.getTitle();
            ArrayList<String> anchors = p.getAnchors();

            IOUtils.write(title + "\t" + StringUtils.join(anchors, "\t") + "\n", out, "UTF-8");
        }
        reader.close();
        fileInputStream.close();
    }

    public void readRecordsFromOutput(Integer recordCount) throws IOException {
        Hashmap hm = new Hashmap(); // Hashmaps for statistics
        Parser p = new Parser();
        String line;
        int i = 0;

        // read certain number of lines or till end
        while (recordCount == -1 && (line = reader.readLine()) != null || i < recordCount && (line = reader.readLine()) != null) {
            i++;
            p.parseLineToHashMap(line, hm);
        }

        writeLinkFreq(hm.getAnchorLinkMM(), hm.getAnchorLinkSMM());
    }

    // write link document and collection frequency to file + calculate top 10
    public void writeLinkFreq(Multimap<String, String> multimap, SetMultimap<String, String> setMultiMap) throws IOException {
        int[] maxValuesDoc = new int[10];
        int[] maxValuesColl = new int[10];
        String[] maxKeysDoc = new String[10];
        String[] maxKeysColl = new String[10];

        int docFreq_timesUsed = 0;
        int collFreq_timesUsed = 0;

        for (String key : multimap.keySet()) {
            docFreq_timesUsed = multimap.get(key).toArray().length;
            collFreq_timesUsed = setMultiMap.get(key).toArray().length;

            // top 10 links for doc. freq.
            for (int i = 0; i < maxValuesDoc.length; i++) {
                if (docFreq_timesUsed > maxValuesDoc[i]) {
                    maxValuesDoc[i] = docFreq_timesUsed;
                    maxKeysDoc[i] = key;
                    break;
                }
            }

            // top 10 links for coll. freq.
            for (int i = 0; i < maxValuesColl.length; i++) {
                if (collFreq_timesUsed > maxValuesColl[i]) {
                    maxValuesColl[i] = collFreq_timesUsed;
                    maxKeysColl[i] = key;
                    break;
                }
            }
            IOUtils.write(key + "\t" + docFreq_timesUsed + "\t" + collFreq_timesUsed + "\n", out, "UTF-8");
        }

        System.out.println("\n=== Top 10 (Link Document Freq.) ===");
        for (int i = 0; i < maxValuesDoc.length; i++) {
            System.out.println(maxKeysDoc[i] + " - " + maxValuesDoc[i] + " (" + setMultiMap.get(maxKeysDoc[i]).toArray().length + ")");
        }

        System.out.println("\n=== Top 10 (Link Collection Freq.) ===");
        for (int i = 0; i < maxValuesColl.length; i++) {
            System.out.println(maxKeysColl[i] + " - " + maxValuesColl[i] + " (" + multimap.get(maxKeysColl[i]).toArray().length + ")");
        }
    }

    public String readRawPage() throws IOException {

        StringBuilder sb = new StringBuilder();
        String line = reader.readLine().trim();

        while (!line.startsWith("</page>")) {
            sb.append(line).append("\n");
            line = reader.readLine().trim();
        }

        sb.append(line).append("\n");
        return sb.toString();
    }


}
