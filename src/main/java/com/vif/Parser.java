package com.vif;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private String pageString;

    Pattern titlePattern = Pattern.compile("<title>(.*?)</title>");
    Pattern textPattern = Pattern.compile("<text.*>(.*?)</text>", Pattern.DOTALL);
    Pattern anchorPattern = Pattern.compile("(\\[\\[([^]\\[:#&;{$]+)\\|([^]\\[:]+)]]|\\[\\[([^]\\[:#&;{$]+)]])(\\p{L}*)(?![^&lt;]*&lt;/pre&gt;)", Pattern.DOTALL);
    Pattern redirectPattern = Pattern.compile("^#REDIRECT", Pattern.CASE_INSENSITIVE);
    // regex explained: https://regex101.com/r/o5kqCH/2/

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

    public boolean isRedirect() {
        if (getText() == null) {
            return false;
        }
        Matcher matcher = redirectPattern.matcher(getText());

        return matcher.find();
    }

    public ArrayList<String> getAnchors() {
        if (getText() == null) {
            return null;
        }
        Matcher matcher = anchorPattern.matcher(getText());
        ArrayList<String> anchorsParsed = new ArrayList<>();

        while (matcher.find()) {
            String link = "";
            String text = "";

            if (matcher.group(2) != null && matcher.group(3) != null) {
                link = matcher.group(2).trim();
                text = matcher.group(3).trim();
            } else {
                text = matcher.group(4).trim();
                link = matcher.group(4).trim();
            }

            String trail = matcher.group(5).trim();

            // "|||" is delimiter
            String anchor = link + "|||" + text + trail;
            anchorsParsed.add(anchor.trim());
        }

        // if there are no anchors in text
        if (anchorsParsed.isEmpty()) {
            return null;
        }
        return anchorsParsed;
    }

    public void parseLineToHashMap(String line, Hashmap hashmap) {
        String title = line.split("\t")[0];
        String[] a = line.split("\t");

        // must have atleast title + 1 anchor
        if (a.length < 2) {
            return;
        }

        // remove first and last el. (title and redirect) from delimited array
        String[] anchors = Arrays.copyOfRange(a, 1, a.length - 1);
        boolean isRedirect = Boolean.parseBoolean(a[a.length - 1]);
        hashmap.getRedirectSMM().put(title, isRedirect);

        if (anchors.length > 0) {
            for (int j = 0; j < anchors.length; j++) {
                String[] anchor = anchors[j].split("\\|\\|\\|");

                // if link is also text
                if (anchor.length == 1) {
                    // Document Freq. HM
                    hashmap.getAnchorLinkSMM().put(anchor[0], title); // { key: anchor_link1; value: [page1, page2] }
                    hashmap.getAnchorTextSMM().put(anchor[0], title);
                    // Collection Freq. MM
                    hashmap.getAnchorLinkMM().put(anchor[0], title); // { key: anchor_link1; value: [page1, page2, page2] }
                    hashmap.getAnchorTextMM().put(anchor[0], title);
                } else {
                    // Document Freq. HM
                    hashmap.getAnchorLinkSMM().put(anchor[0], title);
                    hashmap.getAnchorTextSMM().put(anchor[1], title);
                    // Collection Freq. MM
                    hashmap.getAnchorLinkMM().put(anchor[0], title);
                    hashmap.getAnchorTextMM().put(anchor[1], title);
                }
            }
        }
    }

}
