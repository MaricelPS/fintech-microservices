package com.utn.product_service.dto;

import com.utn.product_service.model.ProductType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Product number is required")
        String number,

        @NotNull(message = "Product type is required")
        ProductType type,

        @NotNull(message = "Customer id is required")
        @Positive(message = "Customer id must be positive")
        Long customerId,

        // Type-specific fields: which ones are required depends on the product type
        @Pattern(regexp = "ARS|USD", message = "Currency must be ARS or USD")
        String currency,

        @Positive(message = "Credit limit must be greater than zero")
        BigDecimal creditLimit,

        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @Positive(message = "Installments must be greater than zero")
        Integer installments,

        @PositiveOrZero(message = "Annual rate cannot be negative")
        BigDecimal annualRate,

        @Positive(message = "Term must be greater than zero")
        Integer termDays
) {}