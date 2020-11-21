package com.vif;

import net.intelie.tinymap.TinyMapBuilder;

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

    public void parseLineToHashMap(String line, Hashmap hashmap, int lineNum, boolean isLinkFreq) {
        String title = line.split("\t")[0];
        String[] a = line.split("\t");

        // must have atleast title + 1 anchor
        if (a.length < 2) {
            return;
        }

        // remove first and last el. (title and redirect) from delimited array
        String[] anchors = Arrays.copyOfRange(a, 1, a.length - 1);

        if (isLinkFreq) {
            boolean isRedirect = Boolean.parseBoolean(a[a.length - 1]);
            hashmap.getRedirectTinyHM().put(title, isRedirect);
        }

        TinyMapBuilder<String, Freqs> anchorHashmap = isLinkFreq ? hashmap.getAnchorLinkTinyHM() : hashmap.getAnchorTextTinyHM();

        if (anchors.length > 0) {
            for (int j = 0; j < anchors.length; j++) {
                String[] anchor = anchors[j].split("\\|\\|\\|");

                // if link is also text
                if (anchor.length == 1) {
                    setFreqs(anchorHashmap, anchor[0], lineNum);
                } else if (anchor.length > 1) {
                    setFreqs(anchorHashmap, anchor[isLinkFreq ? 0 : 1], lineNum);
                }
            }
        }
    }

    public void setFreqs(TinyMapBuilder<String, Freqs> hashmap, String anchor, int pageNum) {
        Freqs freqs = new Freqs();
        freqs.setPageNum(pageNum);

        if (hashmap.containsKey(anchor)) {
            freqs.setColFreq(hashmap.get(anchor).getColFreq() + 1);

            if (hashmap.get(anchor).getPageNum() != freqs.getPageNum()) {
                freqs.setDocFreq(hashmap.get(anchor).getDocFreq() + 1);
            } else {
                freqs.setDocFreq(hashmap.get(anchor).getDocFreq());
            }

        } else {
            freqs.setColFreq(1);
            freqs.setDocFreq(1);
        }
        hashmap.put(anchor, freqs);
    }

}
