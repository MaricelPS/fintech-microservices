package com.utn.customer_service.exception;

public class ProductServiceException extends RuntimeException {
    public ProductServiceException(Long customerId, Throwable cause) {
        super("Products for customer " + customerId + " could not be retrieved: product-service is unavailable", cause);
    }
}