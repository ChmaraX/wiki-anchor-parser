package com.vif;

public class Page {
     private String title;
     private String text;
     private Anchor[] anchors;

    public Page(String title, String text, Anchor[] anchors) {
        this.title = title;
        this.text = text;
        this.anchors = anchors;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Anchor[] getAnchors() {
        return anchors;
    }

    public void setAnchors(Anchor[] anchors) {
        this.anchors = anchors;
    }
}
