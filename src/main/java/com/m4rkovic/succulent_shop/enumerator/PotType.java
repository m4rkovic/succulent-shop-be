package com.m4rkovic.succulent_shop.enumerator;

public enum PotType {
    PLASTIC("PLASTIC"),
    CERAMIC("CERAMIC"),
    TERRACOTTA("TERRACOTTA"),
    CLAY("CLAY");

    private String code;

    private PotType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static PotType fromCode(String code) {
        for (PotType type : PotType.values()) {
            if (type.getCode().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No enum constant with code: " + code);
    }
}
