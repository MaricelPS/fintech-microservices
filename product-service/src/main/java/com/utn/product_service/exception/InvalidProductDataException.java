package com.utn.product_service.exception;

import java.util.List;

public class InvalidProductDataException extends RuntimeException {

    private final List<String> details;

    public InvalidProductDataException(String message, List<String> details) {
        super(message);
        this.details = details;
    }

    public InvalidProductDataException(String message) {
        this(message, List.of());
    }

    public List<String> getDetails() {
        return details;
    }
}