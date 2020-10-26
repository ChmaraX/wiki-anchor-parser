package com.vif;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private String pageString;

    Pattern titlePattern = Pattern.compile("<title>(.*?)</title>");
    Pattern textPattern = Pattern.compile("<text.*>(.*?)</text>", Pattern.DOTALL);
    Pattern anchorPattern = Pattern.compile("\\[\\[([^]\\[:]+)\\|([^]\\[:]+)]](\\p{L}*)", Pattern.DOTALL);

    public Parser() {
    }

    public Parser(String pageString) {
        this.pageString = pageString;
    }

    public String getPageString() {
        return pageString;
    }

    public String getTitle() {
        Matcher matcher = titlePattern.matcher(this.pageString);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    public String getText() {
        Matcher matcher = textPattern.matcher(this.pageString);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public ArrayList<String> getAnchors() {
        if (getText() == null) {
            return null;
        }
        Matcher matcher = anchorPattern.matcher(getText());
        ArrayList<String> anchorsParsed = new ArrayList<>();

        while (matcher.find()) {
            String link = matcher.group(1).trim();
            String text = matcher.group(2).trim();
            String trail = matcher.group(3).trim();

            // "|||" is delimiter
            String anchor = link + "|||" + text + trail;
            anchorsParsed.add(anchor.trim());
        }

        return anchorsParsed;
    }

    public void parseLineToHashMap(String line, Hashmap hashmap) {
        String title = line.split("\t")[0];
        String[] a = line.split("\t");
        String[] anchors = Arrays.copyOfRange(a, 1, a.length); // remove title (1st el.) from delimited array

        if (anchors.length > 0) {
            for (int j = 0; j < anchors.length; j++) {
                String[] anchor = anchors[j].split("\\|\\|\\|");

                // link is also text
                if (anchor.length == 1) {
                    hashmap.getAnchorLinkHm().put(anchor[0], title); // Document Freq. HM
                    hashmap.getAnchorTextHm().put(anchor[0], title);
                    hashmap.getAnchorLinkMM().put(anchor[0], title); // Collection Freq. MM
                    hashmap.getAnchorTextMM().put(anchor[0], title);
                } else {
                    hashmap.getAnchorLinkHm().put(anchor[0], title); // Document Freq. HM
                    hashmap.getAnchorTextHm().put(anchor[1], title);
                    hashmap.getAnchorLinkMM().put(anchor[0], title); // Collection Freq. MM
                    hashmap.getAnchorTextMM().put(anchor[1], title);
                }
            }
        }
    }

}
