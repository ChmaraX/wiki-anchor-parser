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
    Pattern anchorPattern1 = Pattern.compile("\\[\\[([^]\\[:#&;{$]+?)\\|(.+?)]](\\p{L}*)", Pattern.DOTALL);
    Pattern anchorPattern2 = Pattern.compile("\\[\\[([^]\\[:#&;|{$]+?)]](\\p{L}*)", Pattern.DOTALL);
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
        Matcher matcher1 = anchorPattern1.matcher(getText());
        Matcher matcher2 = anchorPattern2.matcher(getText());

        ArrayList<String> anchorsParsed = new ArrayList<>();

        while (matcher1.find()) {
            String link = "";
            String text = "";

            if (matcher1.group(1) != null && matcher1.group(2) != null) {
                link = matcher1.group(1).trim();
                text = matcher1.group(2).trim();
            }

            String trail = matcher1.group(3).trim();

            // "|||" is delimiter
            String anchor = link + "|||" + text + trail;
            anchorsParsed.add(anchor.trim());
        }

        while (matcher2.find()) {
            String link = "";
            String text = "";

            if (matcher2.group(1) != null && matcher2.group(2) != null) {
                link = matcher2.group(1).trim();
                text = matcher2.group(1).trim();
            }

            String trail = matcher2.group(2).trim();

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

    public void parseLineToHashMap(String line, Hashmap hashmap, int lineNum) {
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
                    setLinkFreqs(hashmap, anchor[0], lineNum);
                    setTextFreqs(hashmap, anchor[0], lineNum);
                } else if (anchor.length > 1) {
                    setLinkFreqs(hashmap, anchor[0], lineNum);
                    setTextFreqs(hashmap, anchor[1], lineNum);
                }
            }
        }
    }

    public void setLinkFreqs(Hashmap hashmap, String anchor, int pageNum) {
        Freqs freqs = new Freqs();
        TinyMapBuilder<String, Freqs> anchorLinkHM = hashmap.getAnchorLinkTinyHM();
        freqs.setPageNum(pageNum);

        if (anchorLinkHM.containsKey(anchor)) {
            freqs.setColFreq(anchorLinkHM.get(anchor).getColFreq() + 1);

            if (anchorLinkHM.get(anchor).getPageNum() != freqs.getPageNum()) {
                freqs.setDocFreq(anchorLinkHM.get(anchor).getDocFreq() + 1);
            } else {
                freqs.setDocFreq(anchorLinkHM.get(anchor).getDocFreq());
            }

        } else {
            freqs.setColFreq(1);
            freqs.setDocFreq(1);
        }
        anchorLinkHM.put(anchor, freqs);
    }

    public void setTextFreqs(Hashmap hashmap, String anchor, int pageNum) {
        Freqs freqs = new Freqs();
        TinyMapBuilder<String, Freqs> anchorTextHM = hashmap.getAnchorTextTinyHM();
        freqs.setPageNum(pageNum);

        if (anchorTextHM.containsKey(anchor)) {
            freqs.setColFreq(anchorTextHM.get(anchor).getColFreq() + 1);

            if (anchorTextHM.get(anchor).getPageNum() != freqs.getPageNum()) {
                freqs.setDocFreq(anchorTextHM.get(anchor).getDocFreq() + 1);
            } else {
                freqs.setDocFreq(anchorTextHM.get(anchor).getDocFreq());
            }

        } else {
            freqs.setColFreq(1);
            freqs.setDocFreq(1);
        }
        anchorTextHM.put(anchor, freqs);
    }

}
