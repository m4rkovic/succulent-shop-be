package com.m4rkovic.succulent_shop.enumerator;

public enum DeliveryMethod {
    STANDARD_DELIVERY("STANDARD DELIVERY"),
    EXPRESS_DELIVERY("EXPRESS DELIVERY");
    private String code;

    private DeliveryMethod(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}