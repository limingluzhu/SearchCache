package com.lt.demo.cache;

/**
 * Created by litao on 15/12/3.
 */
public enum  CacheTypeEnum {
    //联想
    SUGGEST(0),
    //搜索
    SEARCH(1);

    private int value;
    private CacheTypeEnum(int value){this.value=value;}

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
