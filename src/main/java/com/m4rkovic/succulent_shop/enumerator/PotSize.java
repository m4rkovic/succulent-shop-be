package com.m4rkovic.succulent_shop.enumerator;

public enum PotSize {
    LARGE("LARGE"),
    MEDIUM("MEDIUM"),
    SMALL("SMALL");

    private String code;

    private PotSize(String code){
        this.code = code;
    }

    public String getCode(){
        return code;
    }
}
