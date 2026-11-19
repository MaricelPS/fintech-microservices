package com.utn.product_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Entity
@DiscriminatorValue("LOAN")
public class Loan extends Product {

    @Column(name = "loan_amount")
    private BigDecimal amount;

    @Column(name = "installments")
    private Integer installments;

    @Column(name = "loan_annual_rate")
    private BigDecimal annualRate; // percentage: 60 means 60% per year

    @Override
    public ProductType getType() {
        return ProductType.LOAN;
    }

    /**
     * Monthly installment using the French amortization system:
     * installment = amount * i * (1 + i)^n / ((1 + i)^n - 1)
     * where i = monthly rate and n = number of installments.
     */
    public BigDecimal calculateMonthlyInstallment() {
        MathContext mc = MathContext.DECIMAL64;

        BigDecimal monthlyRate = annualRate
                .divide(BigDecimal.valueOf(100), mc)
                .divide(BigDecimal.valueOf(12), mc);

        if (monthlyRate.signum() == 0) {
            // Interest-free: the amount is split into equal parts
            return amount.divide(BigDecimal.valueOf(installments), 2, RoundingMode.HALF_UP);
        }

        BigDecimal factor = BigDecimal.ONE.add(monthlyRate).pow(installments, mc);
        BigDecimal numerator = amount.multiply(monthlyRate, mc).multiply(factor, mc);
        BigDecimal denominator = factor.subtract(BigDecimal.ONE);

        return numerator.divide(denominator, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Integer getInstallments() { return installments; }
    public void setInstallments(Integer installments) { this.installments = installments; }
    public BigDecimal getAnnualRate() { return annualRate; }
    public void setAnnualRate(BigDecimal annualRate) { this.annualRate = annualRate; }
}