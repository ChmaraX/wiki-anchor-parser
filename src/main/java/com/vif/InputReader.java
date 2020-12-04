package com.vif;

import net.intelie.tinymap.TinyMapBuilder;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

/**
 * Reads raw data from input files, uses Parser class for transformation
 * and then writes transformed data into output files
 */
public class InputReader {

    private final BufferedReader reader;
    private FileOutputStream out;
    private final File outputFile;

    private final FileInputStream fileInputStream;

    /**
     * Initializes InputReader with input and output files
     * @param fileIn input file to read from
     * @param fileOut output file to write into
     * @throws IOException
     */
    public InputReader(String fileIn, String fileOut) throws IOException {
        this.fileInputStream = new FileInputStream(new File(fileIn));
        this.reader = new BufferedReader(new InputStreamReader(fileInputStream), 1000 * 8192);
        this.outputFile = new File(fileOut);
        this.out = new FileOutputStream(outputFile, true);
    }

    /**
     * Parses titles and anchors from pages in XML file and writes into output file in
     * format: "<title>\tab<link>|||<text>\tab ... \tab<is_redirect>"
     * @param pageCount number of pages to process ("-1" for all)
     * @throws IOException
     */
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
                IOUtils.write(title + "\t" + StringUtils.join(anchors, "\t") + "\t" + isRedirect + "\n", out, "UTF-8");
            }

        }
        reader.close();
        fileInputStream.close();
    }

    /**
     * Reads parsed pages from input file and stores them to hashmap
     * @param recordCount number of records to parse ("-1" for all)
     * @param isLinkFreq boolean to determine if link or text frequencies should be processed
     * @throws IOException
     */
    public void processOutputFile(Integer recordCount, boolean isLinkFreq) throws IOException {
        Hashmap hm = new Hashmap(); // Hashmaps for statistics
        Parser p = new Parser();
        String line;
        int i = 0;

        // read certain number of lines or till end
        while (recordCount == -1 && (line = reader.readLine()) != null || i < recordCount && (line = reader.readLine()) != null) {
            i++;
            if (i % 100000 == 0) {
                System.out.println("Pages parsed: " + i);
            }

            p.parseLineToHashMap(line, hm, i, isLinkFreq);
        }

        TinyMapBuilder<String, Freqs> anchorHashmap = isLinkFreq ? hm.getAnchorLinkTinyHM() : hm.getAnchorTextTinyHM();
        writeLinkFrequencies(anchorHashmap, hm.getRedirectTinyHM(), out);

    }

    /**
     * Writes link or text frequencies to file
     * @param hashmap
     * @param redirectMultiMap
     * @param output
     * @throws IOException
     */
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

    /**
     * Reads one page from XML
     * @return
     * @throws IOException
     */
    public String readRawPage() throws IOException {

        StringBuilder sb = new StringBuilder();
        String line = reader.readLine().trim();

        if (line.startsWith("</mediawiki>")) {
            return null;
        }

        while (!line.startsWith("</page>")) {
            sb.append(line).append("\n");
            line = reader.readLine().trim();
        }

        sb.append(line).append("\n");
        return sb.toString();
    }

    /**
     * Creates basic statistics from output link and text frequency files
     * @param isLink boolean to determine if link or text frequencies should be processed
     * @throws IOException
     */
    public void createStatistics(boolean isLink) throws IOException {
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
                maxDocLine = Arrays.toString(ArrayUtils.remove(line.split("\t"), 2));
            }

            if (colCount >= maxColCount) {
                maxColCount = colCount;
                maxColLine = Arrays.toString(ArrayUtils.remove(line.split("\t"), 1));
            }

            docCountSum = docCountSum + docCount;
            colCountSum = colCountSum + colCount;
            totalCount++;
        }

        float avgDocFreq = (float) docCountSum / (float) totalCount;
        float avgColFreq = (float) colCountSum / (float) totalCount;

        String which = isLink ? "Link" : "Text";

        FileUtils.writeStringToFile(outputFile, "======= Anchor " + which + " Stats ========\n", "UTF-8", true);
        FileUtils.writeStringToFile(outputFile, "Total number of " + which + "\t" + totalCount + "\n\n", "UTF-8", true);
        FileUtils.writeStringToFile(outputFile, "Average " + which + " Document Freq." + "\t" + avgDocFreq + "\n", "UTF-8", true);
        FileUtils.writeStringToFile(outputFile, "Average " + which + " Collection Freq." + "\t" + avgColFreq + "\n\n", "UTF-8", true);
        FileUtils.writeStringToFile(outputFile, "Max. " + which + " Document Freq." + "\t" + maxDocLine + "\n", "UTF-8", true);
        FileUtils.writeStringToFile(outputFile, "Max. " + which + " Collection Freq." + "\t" + maxColLine + "\n\n", "UTF-8", true);

        if (isLink) {
            FileUtils.writeStringToFile(outputFile, "Redirect Count" + "\t" + redirectCount + "\n\n", "UTF-8", true);
        }

    }


}
