package com.github.justadeni.invite.utils;

public class Tread {

    public static void eep(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException ignored) {}
    }

}
