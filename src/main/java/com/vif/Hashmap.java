package com.vif;

import net.intelie.tinymap.TinyMap;
import net.intelie.tinymap.TinyMapBuilder;

/**
 * Contains data-structures (low footprint hashmaps) for storing and manipulating with data
 */
public class Hashmap {

    private TinyMapBuilder<String, Boolean> redirectTinyHM;
    private TinyMapBuilder<String, Freqs> anchorLinkTinyHM;
    private TinyMapBuilder<String, Freqs> anchorTextTinyHM;


    public Hashmap() {
        this.redirectTinyHM =  TinyMap.builder();
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

    public TinyMapBuilder<String, Boolean> getRedirectTinyHM() {
        return redirectTinyHM;
    }

    public void setRedirectTinyHM(TinyMapBuilder<String, Boolean> redirectTinyHM) {
        this.redirectTinyHM = redirectTinyHM;
    }
}
