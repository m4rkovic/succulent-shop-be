package com.m4rkovic.succulent_shop.exceptions;

import com.m4rkovic.succulent_shop.entity.Product;
import lombok.Getter;

import java.util.List;

@Getter
public class BulkImportException extends RuntimeException {
    private final List<Product> successfulImports;

    public BulkImportException(String message, List<Product> successfulImports) {
        super(message);
        this.successfulImports = successfulImports;
    }
}