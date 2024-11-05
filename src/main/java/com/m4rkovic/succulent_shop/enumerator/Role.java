package com.m4rkovic.succulent_shop.enumerator;

public enum Role {

    ROLE_USER("ROLE_USER"),
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_EMPLOYEE("ROLE_ROLE_EMPLOYEE");

    private String code;

    private Role(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
