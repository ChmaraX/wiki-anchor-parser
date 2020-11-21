package com.vif;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        String inputFilePath = "metawiki-latest-pages-articles.xml";
        String outputFilePath = "sample123.txt";
        String linkFrequenciesFile = "data/link_freq.csv";
        String textFrequenciesFile = "data/text_freq.csv";
        String statistics = "data/statistics.txt";

//        InputReader inputReader = new InputReader(inputFilePath, outputFilePath);
//        inputReader.processPages(1000);

        InputReader linkFreqReader = new InputReader(outputFilePath, linkFrequenciesFile);
        linkFreqReader.processOutputFile(100000, true);

        InputReader linkTextReader = new InputReader(outputFilePath, textFrequenciesFile);
        linkTextReader.processOutputFile(100000, false);

        InputReader csvReaderLink = new InputReader(linkFrequenciesFile, statistics);
        csvReaderLink.createStatistics(true);

        InputReader csvReaderText = new InputReader(textFrequenciesFile, statistics);
        csvReaderText.createStatistics(false);

        System.out.println("done");

    }
}
