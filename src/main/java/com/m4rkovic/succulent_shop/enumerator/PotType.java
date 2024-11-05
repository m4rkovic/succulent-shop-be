package com.m4rkovic.succulent_shop.enumerator;

public enum PotType {
    Plastic("PLASTIC"),
    Ceramic("CERAMIC"),
    Clay("Clay");

    private String code;

    private PotType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
