package com.vif;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Parser {
    private final String pageString;

    Pattern titlePattern = Pattern.compile("<title>(.*?)</title>");
    Pattern textPattern = Pattern.compile("<text.*>(.*?)</text>", Pattern.DOTALL);
    Pattern anchorPattern = Pattern.compile("\\[\\[(.*?)\\]\\]", Pattern.DOTALL);

    public Parser(String pageString) {
        this.pageString = pageString;
    }

    public String getPageString() {
        return pageString;
    }

    public String getTitle() {
        Matcher matcher = titlePattern.matcher(this.pageString);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    // takes too long
    public String getText() {
        Matcher matcher = textPattern.matcher(this.pageString);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public ArrayList<String> getAnchors() {
        Matcher matcher = anchorPattern.matcher(getText());
        ArrayList<String> anchorsRaw = new ArrayList<>();
        ArrayList<String> anchorsParsed = new ArrayList<>();

        while (matcher.find()) {
            anchorsRaw.add(matcher.group(1));
        }

        // filter anchors that are not inner links
        String[] resKeywords = {"File:", "Category:", ":Category:", "Wikipedia:"};
        anchorsRaw.removeIf(a -> Stream.of(resKeywords).anyMatch(a::startsWith));

        for (String a : anchorsRaw) {
            if (a.contains("|")) {
                String text = a.split("\\|")[1];
                String link = a.split("\\|")[0];

                String anchor = "{" + text + ":" + link + "}";
                anchorsParsed.add(anchor);
            }
            else {
                String anchor = "{" + a + ":" + a + "}";
                anchorsParsed.add(anchor);
            }
            // if not...
        }

        return anchorsParsed;
    }

}
