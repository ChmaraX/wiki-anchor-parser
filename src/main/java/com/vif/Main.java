package com.vif;

import org.apache.commons.compress.compressors.CompressorException;
import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException, CompressorException {
        String inputFilePath = "metawiki-latest-pages-articles.xml";
        String outputFilePath = "sample.txt";

        /*
          current stats:
          10 000 000 pages ~= 2.7h

          1000 pages:
           compressed: ~4.4sec
                Reading XML: 3.894994 seconds
                Parsing: 0.349999 seconds
                Writing: 0.060999963 seconds

           decompressed: ~0.8sec
                Reading XML: 0.23700008 seconds
                Parsing: 0.35499892 seconds
                Writing: 0.07399998 seconds
         */

        InputReader inputReader = new InputReader(inputFilePath, outputFilePath);
        inputReader.parseAndWritePages(1000);

        InputReader outputReader = new InputReader(outputFilePath);
        outputReader.readRecordsFromOutput(-1);


        System.out.println("done");

    }
}
