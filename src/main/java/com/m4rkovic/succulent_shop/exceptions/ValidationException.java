package com.m4rkovic.succulent_shop.exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValidationException extends RuntimeException {
    private final List<String> violations;

    public ValidationException(String message) {
        super(message);
        this.violations = Collections.emptyList();
    }

    public ValidationException(String message, List<String> violations) {
        super(message);
        this.violations = Collections.unmodifiableList(new ArrayList<>(violations));
    }

    public List<String> getViolations() {
        return violations;
    }
}