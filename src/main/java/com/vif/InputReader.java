package com.vif;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;

public class InputReader {

    private final BufferedReader reader;
    //private final MultiStreamBZip2InputStream bzMsIn;
    private final FileOutputStream out;
    private final FileInputStream fileInputStream;
    private float totalM = 0;
    private float totalP = 0;
    private float totalW = 0;

    public InputReader(String fileIn) throws IOException {
        this.fileInputStream = new FileInputStream(fileIn);
        //this.bzMsIn = new MultiStreamBZip2InputStream(fileInputStream);
        this.reader = new BufferedReader(new InputStreamReader(fileInputStream), 1000 * 8192);
        this.out = new FileOutputStream("sample.txt");
        //skipSiteInfo();
    }


    public void readPagesFromFile(Integer pageCount) throws IOException {
        ArrayList<String> pages = new ArrayList<>();

        long start = System.currentTimeMillis();


        for (int i = 0; i < pageCount; i++) {
            String page = readRawPage();

            long parsStart = System.currentTimeMillis();

            Parser p = new Parser(page);
            String title = p.getTitle();
            ArrayList<String> anchors = p.getAnchors();

            long parsEnd = System.currentTimeMillis();
            this.totalP += (parsEnd - parsStart) / 1000f;

            long writeStart = System.currentTimeMillis();
            IOUtils.write(title + StringUtils.join(anchors, "") + "\n", out, "UTF-8");
            long writeEnd = System.currentTimeMillis();
            this.totalW += (writeEnd - writeStart) / 1000f;
        }


        long end = System.currentTimeMillis();
        System.out.println("Total: " +  (end - start) / 1000f + " seconds");
        System.out.println("Reading XML: " + totalM + " seconds");
        System.out.println("Parsing: " + totalP + " seconds");
        System.out.println("Writing: " + totalW + " seconds");

        reader.close();
        //bzMsIn.close();
        fileInputStream.close();
    }

    public void skipSiteInfo() throws IOException {
        String line = reader.readLine().trim();

        while (!line.startsWith("</siteinfo>")) {
            line = reader.readLine().trim();
        }
    }

    public String readRawPage() throws IOException {

        StringBuilder sb = new StringBuilder();
        String line = reader.readLine().trim();

        long start = System.currentTimeMillis();

        while (!line.startsWith("</page>")) {
            sb.append(line).append("\n");
            line = reader.readLine().trim();
        }
        long end = System.currentTimeMillis();

        sb.append(line).append("\n");
        float total = (end - start) / 1000f;

        this.totalM += total;

        return sb.toString();
    }


}
