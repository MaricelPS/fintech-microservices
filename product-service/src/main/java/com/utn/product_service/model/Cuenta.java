package com.utn.product_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CUENTA")
public class Cuenta extends Producto {

    @Column(length = 3)
    private String moneda; // ARS, USD

    @Override
    public TipoProducto getTipo() {
        return TipoProducto.CUENTA;
    }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
}