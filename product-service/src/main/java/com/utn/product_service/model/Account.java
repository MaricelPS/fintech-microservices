package com.utn.product_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ACCOUNT")
public class Account extends Product {

    @Column(length = 3)
    private String currency; // ARS, USD

    @Override
    public ProductType getType() {
        return ProductType.ACCOUNT;
    }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}