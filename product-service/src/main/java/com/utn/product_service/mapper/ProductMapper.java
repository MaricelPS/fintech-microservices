package com.utn.product_service.mapper;

import com.utn.product_service.dto.ProductRequest;
import com.utn.product_service.dto.ProductResponse;
import com.utn.product_service.model.*;

import java.math.BigDecimal;

public final class ProductMapper {

    private ProductMapper() {
    }

    // ---------- DTO -> entity ----------

    public static Product toEntity(ProductRequest request) {
        Product product = switch (request.type()) {
            case ACCOUNT -> new Account();
            case CARD -> new Card();
            case LOAN -> new Loan();
            case INVESTMENT -> new Investment();
        };
        product.setCustomerId(request.customerId());
        updateEntity(product, request);
        return product;
    }

    public static void updateEntity(Product product, ProductRequest request) {
        product.setNumber(request.number());

        switch (product) {
            case Account account -> account.setCurrency(request.currency());
            case Card card -> card.setCreditLimit(request.creditLimit());
            case Loan loan -> {
                loan.setAmount(request.amount());
                loan.setInstallments(request.installments());
                loan.setAnnualRate(request.annualRate());
            }
            case Investment investment -> {
                investment.setAmount(request.amount());
                investment.setTermDays(request.termDays());
                investment.setAnnualRate(request.annualRate());
            }
            default -> throw new IllegalStateException("Unsupported product type");
        }
    }

    // ---------- entity -> DTO ----------

    public static ProductResponse toResponse(Product product) {
        String currency = null;
        BigDecimal creditLimit = null, amount = null, annualRate = null;
        BigDecimal monthlyInstallment = null, estimatedReturn = null;
        Integer installments = null, termDays = null;

        switch (product) {
            case Account account -> currency = account.getCurrency();
            case Card card -> creditLimit = card.getCreditLimit();
            case Loan loan -> {
                amount = loan.getAmount();
                installments = loan.getInstallments();
                annualRate = loan.getAnnualRate();
                monthlyInstallment = loan.calculateMonthlyInstallment();
            }
            case Investment investment -> {
                amount = investment.getAmount();
                termDays = investment.getTermDays();
                annualRate = investment.getAnnualRate();
                estimatedReturn = investment.calculateEstimatedReturn();
            }
            default -> throw new IllegalStateException("Unsupported product type");
        }

        return new ProductResponse(
                product.getId(),
                product.getNumber(),
                product.getType().name(),
                product.getCustomerId(),
                product.getStatus().name(),
                product.getOpeningDate(),
                currency, creditLimit, amount, installments, annualRate,
                monthlyInstallment, termDays, estimatedReturn
        );
    }
}