package com.m4rkovic.succulent_shop.exceptions;

public class ProductUpdateException extends RuntimeException {
    public ProductUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}

