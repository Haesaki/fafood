package com.sin.subenum;

/**
 * 0 woman
 * 1 man
 * 2 unknown
 */
public enum Sex {
    woman(0, "woman"),
    man(1, "man"),
    unknown(2, "unknow");

    public final Integer type;
    public final String value;

    Sex(int type, String value) {
        this.type = type;
        this.value = value;
    }
}
