package com.m4rkovic.succulent_shop.enumerator;

public enum DeliveryMethod {
    STANDARD_DELIVERY("STANDARD_DELIVERY"),
    EXPRESS_DELIVERY("EXPRESS_DELIVERY");
    private String code;

    private DeliveryMethod(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}