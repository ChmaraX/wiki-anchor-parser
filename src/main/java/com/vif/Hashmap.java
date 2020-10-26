package com.vif;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.HashMap;

public class Hashmap {

    private HashMap<String, String> anchorLinkHm;
    private HashMap<String, String> anchorTextHm;
    private Multimap<String, String> anchorTextMM;
    private Multimap<String, String> anchorLinkMM;

    public Hashmap() {
        this.anchorLinkHm = new HashMap<String, String>();
        this.anchorTextHm = new HashMap<String, String>();
        this.anchorTextMM = ArrayListMultimap.create();
        this.anchorLinkMM = ArrayListMultimap.create();
    }

    public HashMap<String, String> getAnchorLinkHm() {
        return anchorLinkHm;
    }

    public void setAnchorLinkHm(HashMap<String, String> anchorLinkHm) {
        this.anchorLinkHm = anchorLinkHm;
    }

    public HashMap<String, String> getAnchorTextHm() {
        return anchorTextHm;
    }

    public void setAnchorTextHm(HashMap<String, String> anchorTextHm) {
        this.anchorTextHm = anchorTextHm;
    }

    public Multimap<String, String> getAnchorTextMM() {
        return anchorTextMM;
    }

    public void setAnchorTextMM(Multimap<String, String> anchorTextMM) {
        this.anchorTextMM = anchorTextMM;
    }

    public Multimap<String, String> getAnchorLinkMM() {
        return anchorLinkMM;
    }

    public void setAnchorLinkMM(Multimap<String, String> anchorLinkMM) {
        this.anchorLinkMM = anchorLinkMM;
    }
}
