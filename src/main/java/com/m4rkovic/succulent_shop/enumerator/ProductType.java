package com.m4rkovic.succulent_shop.enumerator;

public enum ProductType {
    DECOR("DECOR"),
    SAPLING("SAPLING"),
    PLANT("PLANT"),
    ARRANGEMENT("ARRANGEMENT");

    private String code;

    private ProductType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
