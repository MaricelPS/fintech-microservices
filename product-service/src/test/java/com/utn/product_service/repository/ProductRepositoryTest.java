package com.utn.product_service.repository;

import com.utn.product_service.model.Account;
import com.utn.product_service.model.Loan;
import com.utn.product_service.model.Product;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    private Account account(String number, Long customerId) {
        Account account = new Account();
        account.setNumber(number);
        account.setCustomerId(customerId);
        account.setCurrency("ARS");
        return account;
    }

    private Loan loan(String number, Long customerId) {
        Loan loan = new Loan();
        loan.setNumber(number);
        loan.setCustomerId(customerId);
        loan.setAmount(new BigDecimal("1000000"));
        loan.setInstallments(12);
        loan.setAnnualRate(new BigDecimal("60"));
        return loan;
    }

    @Test
    @DisplayName("findByCustomerId returns only the products of that customer")
    void findByCustomerId_returnsOnlyThatCustomersProducts() {
        productRepository.saveAndFlush(account("AC-0001", 1L));
        productRepository.saveAndFlush(loan("LN-0001", 1L));
        productRepository.saveAndFlush(account("AC-0002", 2L));

        List<Product> result = productRepository.findByCustomerId(1L);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(product -> product.getCustomerId().equals(1L)));
    }

    @Test
    @DisplayName("findByCustomerId returns an empty list for a customer without products")
    void findByCustomerId_unknownCustomer_returnsEmptyList() {
        productRepository.saveAndFlush(account("AC-0001", 1L));

        assertTrue(productRepository.findByCustomerId(99L).isEmpty());
    }

    @Test
    @DisplayName("A loan is read back from the single table as a Loan with its own fields")
    void save_loan_isReadBackAsLoan() {
        Long id = productRepository.saveAndFlush(loan("LN-0001", 1L)).getId();
        entityManager.clear(); // forces a real read from the database instead of the cache

        Product found = productRepository.findById(id).orElseThrow();

        Loan loan = assertInstanceOf(Loan.class, found);
        assertEquals(12, loan.getInstallments());
        assertEquals(0, new BigDecimal("1000000").compareTo(loan.getAmount()));
    }

    @Test
    @DisplayName("existsByNumber detects an existing product number")
    void existsByNumber_existingAndMissing() {
        productRepository.saveAndFlush(account("AC-0001", 1L));

        assertTrue(productRepository.existsByNumber("AC-0001"));
        assertFalse(productRepository.existsByNumber("AC-9999"));
    }

    @Test
    @DisplayName("The database rejects a duplicate product number")
    void saveAndFlush_duplicateNumber_throws() {
        productRepository.saveAndFlush(account("AC-0001", 1L));

        assertThrows(DataIntegrityViolationException.class,
                () -> productRepository.saveAndFlush(account("AC-0001", 2L)));
    }
}

