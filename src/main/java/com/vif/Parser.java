package com.vif;

import net.intelie.tinymap.TinyMapBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses data from input/output files to desired form using regexes,
 * transforms and stores data into data-structures
 */
public class Parser {
    private String pageString;

    Pattern titlePattern = Pattern.compile("<title>(.*?)</title>");
    Pattern textPattern = Pattern.compile("<text.*>(.*?)</text>", Pattern.DOTALL);
    Pattern anchorPattern = Pattern.compile("(\\[\\[([^]\\[:#&;{$]+)\\|([^]\\[:]+)]]|\\[\\[([^]\\[:#&;{$]+)]])(\\p{L}*)(?![^&lt;]*&lt;/pre&gt;)", Pattern.DOTALL);
    Pattern redirectPattern = Pattern.compile("^#REDIRECT", Pattern.CASE_INSENSITIVE);
    // regex explained: https://regex101.com/r/o5kqCH/2/

    public Parser() {
    }

    /**
     * Initializes Parser with raw page text (from <page> to </page>)
     * @param pageString
     */
    public Parser(String pageString) {
        this.pageString = pageString;
    }

    public String getPageString() {
        return pageString;
    }

    /**
     * Extracts title from page using regex
     * @return title of the page
     */
    public String getTitle() {
        Matcher matcher = titlePattern.matcher(this.pageString);

        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    /**
     * Extracts text from page using regex
     * @return text of the page
     */
    public String getText() {
        Matcher matcher = textPattern.matcher(this.pageString);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Checks if page is redirect (starts with redirect anchor) using regex
     * @return boolean if page is redirect or not
     */
    public boolean isRedirect() {
        if (getText() == null) {
            return false;
        }
        Matcher matcher = redirectPattern.matcher(getText());

        return matcher.find();
    }

    /**
     * Extracts all anchor texts and links from page text (using regex)
     * and adds them to ArrayList
     * @return ArrayList of strings in format "<link>|||<text>"
     */
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

    /**
     * Parses line containg title and anchor links/texts to Hashmap<String,Freqs>
     * @param line in format "<title>\tab<link>|||<text>\tab ... <is_redirect>"
     * @param hashmap to store entries where anchor link/text is key and Freqs object is value
     * @param lineNum unique line (page) identifier
     * @param isLinkFreq boolean to determine if link or text frequencies should be processed
     */
    public void parseLineToHashMap(String line, Hashmap hashmap, int lineNum, boolean isLinkFreq) {
        String title = line.split("\t")[0];
        String[] a = line.split("\t");

        // must have atleast title + 1 anchor
        if (a.length < 2) {
            return;
        }

        // remove first (title) element from delimited array, if link freq - remove also last (redirect)
        String[] anchors = Arrays.copyOfRange(a, 1, isLinkFreq ? a.length - 1 : a.length);

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

    /**
     * Sets document and collection frequency in Freqs object
     * @param hashmap to store entries where anchor link/text is key and Freqs object is value
     * @param anchor anchor text or link to be stored as key
     * @param pageNum unique line (page) identifier
     */
    public void setFreqs(TinyMapBuilder<String, Freqs> hashmap, String anchor, int pageNum) {
        Freqs freqs = new Freqs();
        freqs.setPageNum(pageNum);

        // if anchor exists in hashmap
        if (hashmap.containsKey(anchor)) {
            // increase collection freq
            freqs.setColFreq(hashmap.get(anchor).getColFreq() + 1);

            // increase document frequency only if its not on the same page (document)
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
