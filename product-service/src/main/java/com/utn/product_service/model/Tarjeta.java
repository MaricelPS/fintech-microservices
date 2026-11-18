package com.utn.product_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("TARJETA")
public class Tarjeta extends Producto {

    @Column(name = "limite_credito")
    private BigDecimal limite;

    @Override
    public TipoProducto getTipo() {
        return TipoProducto.TARJETA;
    }

    public BigDecimal getLimite() { return limite; }
    public void setLimite(BigDecimal limite) { this.limite = limite; }
}
