package com.utn.customer_service.dto;

import java.math.BigDecimal;

public record CustomerResponse(
        Long id,
        String name,
        String documentNumber,
        String email,
        BigDecimal balance
) {}
