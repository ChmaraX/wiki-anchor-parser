package com.vif;

import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
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

    public void processOutputFile(Integer recordCount) throws IOException {
        Hashmap hm = new Hashmap(); // Hashmaps for statistics
        Parser p = new Parser();
        String line;
        int i = 0;

        // read certain number of lines or till end
        while (recordCount == -1 && (line = reader.readLine()) != null || i < recordCount && (line = reader.readLine()) != null) {
            i++;
            p.parseLineToHashMap(line, hm);
        }

        // write anchor link frequencies
        writeLinkFrequencies(hm.getAnchorLinkMM(), hm.getAnchorLinkSMM(), hm.getRedirectSMM(), out1);

        // write anchor text frequencies
        writeTextFrequencies(hm.getAnchorTextMM(), hm.getAnchorTextSMM(), out2);
    }

    // write link document and collection frequency (+ if its a redirect) to file
    public void writeLinkFrequencies(Multimap<String, String> multimap,
                                     SetMultimap<String, String> setMultiMap,
                                     SetMultimap<String, Boolean> redirectMultiMap,
                                     FileOutputStream output) throws IOException {
        int docFreq_timesUsed = 0;
        int collFreq_timesUsed = 0;

        for (String key : multimap.keySet()) {
            collFreq_timesUsed = multimap.get(key).toArray().length;
            docFreq_timesUsed = setMultiMap.get(key).toArray().length;
            String isRedirect = "";

            if (key.equals("null")) {
                System.out.println("here");
                System.out.println(Arrays.toString(setMultiMap.get(key).toArray()));
            }

            if (redirectMultiMap.containsKey(key)) {
                isRedirect = String.valueOf(redirectMultiMap.get(key).toArray()[0]);
            }

            IOUtils.write(key + "\t" + docFreq_timesUsed + "\t" + collFreq_timesUsed + "\t" + isRedirect + "\n", output, "UTF-8");
        }
    }

    // write link document and collection frequency to file
    public void writeTextFrequencies(Multimap<String, String> multimap,
                                     SetMultimap<String, String> setMultiMap,
                                     FileOutputStream output) throws IOException {
        int docFreq_timesUsed = 0;
        int collFreq_timesUsed = 0;

        for (String key : multimap.keySet()) {
            collFreq_timesUsed = multimap.get(key).toArray().length;
            docFreq_timesUsed = setMultiMap.get(key).toArray().length;

            IOUtils.write(key + "\t" + docFreq_timesUsed + "\t" + collFreq_timesUsed + "\n", output, "UTF-8");
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
