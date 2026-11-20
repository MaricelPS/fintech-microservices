package com.utn.customer_service.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(Long customerId, BigDecimal amount) {
        super("Insufficient balance in customer " + customerId + " to withdraw " + amount);
    }
}
