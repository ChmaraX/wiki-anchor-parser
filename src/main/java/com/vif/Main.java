package com.vif;

import org.apache.commons.compress.compressors.CompressorException;
import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException, CompressorException {
        String inputFilePath = "metawiki-latest-pages-articles.xml";
        String outputFilePath = "sample.txt";
        String linkFrequenciesFile = "link_freq.csv";
        String textFrequenciesFile = "text_freq.csv";
        String statistics = "statistics.txt";

        InputReader inputReader = new InputReader(inputFilePath, outputFilePath);
        inputReader.processPages(100000);

        InputReader outputReader = new InputReader(outputFilePath, linkFrequenciesFile, textFrequenciesFile);
        outputReader.processOutputFile(-1);

        InputReader csvReader = new InputReader(linkFrequenciesFile, statistics);
        csvReader.createStatistics();

        System.out.println("done");

    }
}
