package com.utn.product_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductResponse(
        Long id,
        String number,
        String type,
        Long customerId,
        String status,
        LocalDate openingDate,
        String currency,
        BigDecimal creditLimit,
        BigDecimal amount,
        Integer installments,
        BigDecimal annualRate,
        BigDecimal monthlyInstallment,
        Integer termDays,
        BigDecimal estimatedReturn
) {}