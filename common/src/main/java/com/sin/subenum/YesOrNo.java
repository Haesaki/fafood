package com.sin.subenum;

public enum YesOrNo {
    NO(0, "NO"),
    YES(1, "Yes");
    public final Integer type;
    public final String value;

    YesOrNo(int type, String value) {
        this.type = type;
        this.value = value;
    }
}
