package com.utn.product_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Entity
@DiscriminatorValue("INVERSION")
public class Inversion extends Producto {

    @Column(name = "monto_inversion")
    private BigDecimal monto;

    @Column(name = "plazo_dias")
    private Integer plazoDias;

    @Column(name = "tasa_inversion")
    private BigDecimal tasaAnual; // porcentaje: TNA

    @Override
    public TipoProducto getTipo() {
        return TipoProducto.INVERSION;
    }

    /**
     * Rendimiento estimado con interés simple (como un plazo fijo):
     * rendimiento = monto * (TNA / 100) * (plazoDias / 365)
     */
    public BigDecimal calcularRendimientoEstimado() {
        MathContext mc = MathContext.DECIMAL64;
        return monto
                .multiply(tasaAnual, mc)
                .divide(BigDecimal.valueOf(100), mc)
                .multiply(BigDecimal.valueOf(plazoDias), mc)
                .divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public Integer getPlazoDias() { return plazoDias; }
    public void setPlazoDias(Integer plazoDias) { this.plazoDias = plazoDias; }
    public BigDecimal getTasaAnual() { return tasaAnual; }
    public void setTasaAnual(BigDecimal tasaAnual) { this.tasaAnual = tasaAnual; }
}