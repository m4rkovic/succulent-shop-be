package com.m4rkovic.succulent_shop.exceptions;

public class DeleteException extends RuntimeException {
    public DeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}