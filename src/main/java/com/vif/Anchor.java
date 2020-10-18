package com.vif;

public class Anchor {
    private final String text;
    private final String link;

    public Anchor(String text, String link) {
        this.text = text;
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public String getLink() {
        return link;
    }
}
