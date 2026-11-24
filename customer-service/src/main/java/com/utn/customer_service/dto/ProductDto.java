package com.utn.customer_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Product as returned by product-service.
 * Field names must match the JSON produced by that service.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductDto(
        Long id,
        String number,
        String type,
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
