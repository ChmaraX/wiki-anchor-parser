package com.vif;

import org.apache.commons.compress.compressors.CompressorException;
import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException, CompressorException {
        String filePath = "/home/adam/projects/ing/vif/data/enwiki-latest-pages-articles-multistream.xml.bz2";
        String filePathDecomp = "/home/adam/projects/vif-anchors/archive.xml";

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

        InputReader inputReader = new InputReader(filePathDecomp);
        inputReader.readPagesFromFile(1000);
        //System.out.println(pages.get(1));
        System.out.println("done");

    }
}
