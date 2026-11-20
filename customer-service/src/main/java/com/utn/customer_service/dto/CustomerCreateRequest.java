package com.utn.customer_service.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CustomerCreateRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Document number is required")
        @Pattern(regexp = "\\d{7,8}", message = "Document number must have 7 or 8 digits")
        String documentNumber,

        @NotBlank(message = "Email is required")
        @Email(message = "Email format is invalid")
        String email,

        @PositiveOrZero(message = "Initial balance cannot be negative")
        BigDecimal initialBalance
) {}