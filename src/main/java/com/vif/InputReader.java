package com.vif;

import com.google.common.collect.SetMultimap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

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

        writeLinkFreq(hm.getAnchorLinkSMM());
    }

    public void writeLinkFreq(SetMultimap<String, String> linkSetMultiMap) throws IOException {
        int max = 0;
        String maxKey = null;
        for (String key : linkSetMultiMap.keySet()) {
            int timesUsed = linkSetMultiMap.get(key).toArray().length;
            if (timesUsed > max) {
                max = timesUsed;
                maxKey = key;
            }
            IOUtils.write(key + "\t" + timesUsed + "\n", out, "UTF-8");
        }
        System.out.println(maxKey + " " + max);
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
