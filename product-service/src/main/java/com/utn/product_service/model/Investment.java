package com.utn.product_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Entity
@DiscriminatorValue("INVESTMENT")
public class Investment extends Product {

    @Column(name = "investment_amount")
    private BigDecimal amount;

    @Column(name = "term_days")
    private Integer termDays;

    @Column(name = "investment_annual_rate")
    private BigDecimal annualRate; // nominal annual rate, as a percentage

    @Override
    public ProductType getType() {
        return ProductType.INVESTMENT;
    }

    /**
     * Estimated return using simple interest (like a fixed-term deposit):
     * return = amount * (annualRate / 100) * (termDays / 365)
     */
    public BigDecimal calculateEstimatedReturn() {
        MathContext mc = MathContext.DECIMAL64;
        return amount
                .multiply(annualRate, mc)
                .divide(BigDecimal.valueOf(100), mc)
                .multiply(BigDecimal.valueOf(termDays), mc)
                .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Integer getTermDays() { return termDays; }
    public void setTermDays(Integer termDays) { this.termDays = termDays; }
    public BigDecimal getAnnualRate() { return annualRate; }
    public void setAnnualRate(BigDecimal annualRate) { this.annualRate = annualRate; }
}