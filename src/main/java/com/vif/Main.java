package com.vif;

import java.io.*;

/**
 * Main class where input/output files are specified,
 * contains sequence of steps with configuration to process input data
 */
public class Main {

    public static void main(String[] args) throws IOException {
        String inputFilePath = "data/enwiki-pages-sample.xml";
        String outputFilePath = "data/pages-parsed.txt";
        String linkFrequenciesFile = "data/link_freq.csv";
        String textFrequenciesFile = "data/text_freq.csv";
        String statistics = "data/statistics.txt";

        // process initial XML Wiki Dump - result is .txt file
        InputReader inputReader = new InputReader(inputFilePath, outputFilePath);
        inputReader.processPages(-1);

        // process .txt file from step above, outputs are .csv files with link/text frequencies
        InputReader linkFreqReader = new InputReader(outputFilePath, linkFrequenciesFile);
        linkFreqReader.processOutputFile(-1, true);

        InputReader linkTextReader = new InputReader(outputFilePath, textFrequenciesFile);
        linkTextReader.processOutputFile(-1, false);

        // creates statistics from link and text frequencies .csv files
        InputReader csvReaderLink = new InputReader(linkFrequenciesFile, statistics);
        csvReaderLink.createStatistics(true);

        InputReader csvReaderText = new InputReader(textFrequenciesFile, statistics);
        csvReaderText.createStatistics(false);

        System.out.println("done");

    }
}
