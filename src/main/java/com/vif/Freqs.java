package com.vif;

/**
 * Object that holds page number, document and collection frequency that is stored in Hashmap<String,Freqs>
 */
public class Freqs {

    private int colFreq;
    private int docFreq;
    private int pageNum;

    public int getColFreq() {
        return colFreq;
    }

    public void setColFreq(int colFreq) {
        this.colFreq = colFreq;
    }

    public int getDocFreq() {
        return docFreq;
    }

    public void setDocFreq(int docFreq) {
        this.docFreq = docFreq;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }
}
