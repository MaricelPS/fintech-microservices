package com.utn.product_service.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoanTest {

    private Loan loan(String amount, int installments, String annualRate) {
        Loan loan = new Loan();
        loan.setAmount(new BigDecimal(amount));
        loan.setInstallments(installments);
        loan.setAnnualRate(new BigDecimal(annualRate));
        return loan;
    }

    @Test
    @DisplayName("Monthly installment follows the French amortization system")
    void calculateMonthlyInstallment_withInterest_usesFrenchSystem() {
        assertAmount("112825.41", loan("1000000", 12, "60").calculateMonthlyInstallment());
    }

    @Test
    @DisplayName("Without interest, the amount is split into equal installments")
    void calculateMonthlyInstallment_zeroRate_splitsEqually() {
        assertAmount("10000.00", loan("120000", 12, "0").calculateMonthlyInstallment());
    }

    @Test
    @DisplayName("Loan type is LOAN")
    void getType_returnsLoan() {
        assertEquals(ProductType.LOAN, new Loan().getType());
    }

    private static void assertAmount(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                "Expected " + expected + " but was " + actual);
    }
}
