package com.vif;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;

public class Hashmap {

    private SetMultimap<String, String> anchorLinkSMM;
    private SetMultimap<String, String> anchorTextSMM;
    private SetMultimap<String, Boolean> redirectSMM;
    private Multimap<String, String> anchorTextMM;
    private Multimap<String, String> anchorLinkMM;

    public Hashmap() {
        this.anchorLinkSMM = HashMultimap.create();
        this.anchorTextSMM = HashMultimap.create();
        this.redirectSMM =  HashMultimap.create();
        this.anchorTextMM = ArrayListMultimap.create();
        this.anchorLinkMM = ArrayListMultimap.create();
    }

    public SetMultimap<String, String> getAnchorLinkSMM() {
        return anchorLinkSMM;
    }

    public void setAnchorLinkSMM(SetMultimap<String, String> anchorLinkSMM) {
        this.anchorLinkSMM = anchorLinkSMM;
    }

    public SetMultimap<String, String> getAnchorTextSMM() {
        return anchorTextSMM;
    }

    public void setAnchorTextSMM(SetMultimap<String, String> anchorTextSMM) {
        this.anchorTextSMM = anchorTextSMM;
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

    public SetMultimap<String, Boolean> getRedirectSMM() {
        return redirectSMM;
    }

    public void setRedirectSMM(SetMultimap<String, Boolean> redirectSMM) {
        this.redirectSMM = redirectSMM;
    }

    public void setAnchorLinkMM(Multimap<String, String> anchorLinkMM) {
        this.anchorLinkMM = anchorLinkMM;
    }
}
