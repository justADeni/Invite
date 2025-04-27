package com.github.justadeni.invite.suggestions;

public class TstCache {

    public long timestamp;
    public Tst tst;

    public TstCache(Tst tst) {
        timestamp = System.currentTimeMillis();
        this.tst = tst;
    }

}
