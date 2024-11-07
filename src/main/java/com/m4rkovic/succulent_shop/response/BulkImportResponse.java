package com.m4rkovic.succulent_shop.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BulkImportResponse {
    private List<ProductResponse> importedProducts;
    private String message;
    private int totalRequested;
    private int successfulImports;
    private List<String> errors;
}