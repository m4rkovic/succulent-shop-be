package com.m4rkovic.succulent_shop.enumerator;

public enum Color {
    RED("RED"),
    GREEN("GREEN/LIME"),
    PURPLE("PURPLE"),
    WHITE("WHITE"),
    ORANGE("ORANGE/COPPER"),
    BLUE("BLUE-GREEN"),
    YELLOW("YELLOW/GOLD"),
    GREY("GREY/SILVER"),
    PINK("PINK"),
    NONE("NONE");

    private String code;

    private Color(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
