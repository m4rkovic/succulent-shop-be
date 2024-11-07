package com.m4rkovic.succulent_shop.enumerator;

public enum ToolType {
    HOE("HOE"),
    BUCKET("BUCKET"),
    PRUNER("PRUNER"),
    SHOVEL("SHOVEL"),
    RAKE("RAKE"),
    WATERCAN("WATERCAN");

    private String code;

    private ToolType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
