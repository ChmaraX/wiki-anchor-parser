package com.vif;

import org.apache.commons.compress.compressors.CompressorException;
import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException, CompressorException {
        String inputFilePath = "metawiki-latest-pages-articles.xml";
        String outputFilePath = "sample.txt";
        String docFreqFilePath = "link_doc_freq.txt";

        InputReader inputReader = new InputReader(inputFilePath, outputFilePath);
        inputReader.proccessPages(1000);

        InputReader outputReader = new InputReader(outputFilePath, docFreqFilePath);
        outputReader.readRecordsFromOutput(-1);


        System.out.println("done");

    }
}
