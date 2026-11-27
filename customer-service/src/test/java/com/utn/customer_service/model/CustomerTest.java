package com.utn.customer_service.model;

import com.utn.customer_service.exception.InsufficientBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
    }

    @Test
    @DisplayName("Depositing a positive amount increases the balance")
    void deposit_positiveAmount_increasesBalance() {
        customer.deposit(new BigDecimal("1000"));

        assertBalance("1000");
    }

    @Test
    @DisplayName("Depositing zero or a negative amount is rejected")
    void deposit_nonPositiveAmount_throws() {
        assertThrows(IllegalArgumentException.class, () -> customer.deposit(BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> customer.deposit(new BigDecimal("-50")));
    }

    @Test
    @DisplayName("Withdrawing with enough balance decreases it")
    void withdraw_sufficientBalance_decreasesBalance() {
        customer.deposit(new BigDecimal("1000"));

        customer.withdraw(new BigDecimal("400"));

        assertBalance("600");
    }

    @Test
    @DisplayName("Withdrawing more than the balance is rejected and the balance is unchanged")
    void withdraw_insufficientBalance_throwsAndKeepsBalance() {
        customer.deposit(new BigDecimal("100"));

        assertThrows(InsufficientBalanceException.class,
                () -> customer.withdraw(new BigDecimal("101")));
        assertBalance("100");
    }

    private void assertBalance(String expected) {
        assertEquals(0, new BigDecimal(expected).compareTo(customer.getBalance()),
                "Expected balance " + expected + " but was " + customer.getBalance());
    }
}
