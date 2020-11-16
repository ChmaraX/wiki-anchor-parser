package com.vif;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import net.intelie.tinymap.TinyMap;
import net.intelie.tinymap.TinyMapBuilder;

public class Hashmap {

    private TinyMapBuilder<String, Boolean> redirectSMM;
    private TinyMapBuilder<String, Freqs> anchorLinkTinyHM;
    private TinyMapBuilder<String, Freqs> anchorTextTinyHM;


    public Hashmap() {
        this.redirectSMM =  TinyMap.builder();
        this.anchorLinkTinyHM = TinyMap.builder();
        this.anchorTextTinyHM = TinyMap.builder();
    }

    public TinyMapBuilder<String, Freqs> getAnchorTextTinyHM() {
        return anchorTextTinyHM;
    }

    public void setAnchorTextTinyHM(TinyMapBuilder<String, Freqs> anchorTextTinyHM) {
        this.anchorTextTinyHM = anchorTextTinyHM;
    }

    public TinyMapBuilder<String, Freqs> getAnchorLinkTinyHM() {
        return anchorLinkTinyHM;
    }

    public void setAnchorLinkTinyHM(TinyMapBuilder<String, Freqs> anchorLinkTinyHM) {
        this.anchorLinkTinyHM = anchorLinkTinyHM;
    }

    public TinyMapBuilder<String, Boolean> getRedirectSMM() {
        return redirectSMM;
    }

    public void setRedirectSMM(TinyMapBuilder<String, Boolean> redirectSMM) {
        this.redirectSMM = redirectSMM;
    }
}
