package com.utn.product_service.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InvestmentTest {

    private Investment investment(String amount, int termDays, String annualRate) {
        Investment investment = new Investment();
        investment.setAmount(new BigDecimal(amount));
        investment.setTermDays(termDays);
        investment.setAnnualRate(new BigDecimal(annualRate));
        return investment;
    }

    @Test
    @DisplayName("Estimated return uses simple interest over the term")
    void calculateEstimatedReturn_thirtyDays_proportionalToTerm() {
        assertAmount("14794.52", investment("500000", 30, "36").calculateEstimatedReturn());
    }

    @Test
    @DisplayName("A full-year term returns the nominal annual rate")
    void calculateEstimatedReturn_fullYear_equalsAnnualRate() {
        assertAmount("40000.00", investment("100000", 365, "40").calculateEstimatedReturn());
    }

    private static void assertAmount(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                "Expected " + expected + " but was " + actual);
    }
}