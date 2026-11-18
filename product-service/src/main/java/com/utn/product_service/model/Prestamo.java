package com.utn.product_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Entity
@DiscriminatorValue("PRESTAMO")
public class Prestamo extends Producto {

    @Column(name = "monto_prestamo")
    private BigDecimal monto;

    @Column(name = "cuotas")
    private Integer cuotas;

    @Column(name = "tasa_prestamo")
    private BigDecimal tasaAnual; // porcentaje: 60 significa 60 % anual

    @Override
    public TipoProducto getTipo() {
        return TipoProducto.PRESTAMO;
    }

    /**
     * Cuota mensual por sistema francés:
     * cuota = monto * i * (1 + i)^n / ((1 + i)^n - 1)
     * donde i = tasa mensual y n = cantidad de cuotas.
     */
    public BigDecimal calcularCuotaMensual() {
        MathContext mc = MathContext.DECIMAL64;

        BigDecimal tasaMensual = tasaAnual
                .divide(BigDecimal.valueOf(100), mc)
                .divide(BigDecimal.valueOf(12), mc);

        if (tasaMensual.signum() == 0) {
            // Sin interés: el monto se divide en partes iguales
            return monto.divide(BigDecimal.valueOf(cuotas), 2, RoundingMode.HALF_UP);
        }

        BigDecimal factor = BigDecimal.ONE.add(tasaMensual).pow(cuotas, mc);
        BigDecimal numerador = monto.multiply(tasaMensual, mc).multiply(factor, mc);
        BigDecimal denominador = factor.subtract(BigDecimal.ONE);

        return numerador.divide(denominador, 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    public Integer getCuotas() { return cuotas; }
    public void setCuotas(Integer cuotas) { this.cuotas = cuotas; }
    public BigDecimal getTasaAnual() { return tasaAnual; }
    public void setTasaAnual(BigDecimal tasaAnual) { this.tasaAnual = tasaAnual; }
}
