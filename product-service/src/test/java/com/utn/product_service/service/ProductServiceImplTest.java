package com.utn.product_service.service;

import com.utn.product_service.dto.ProductRequest;
import com.utn.product_service.dto.ProductResponse;
import com.utn.product_service.exception.DuplicateResourceException;
import com.utn.product_service.exception.InvalidProductDataException;
import com.utn.product_service.exception.ProductNotFoundException;
import com.utn.product_service.model.*;
import com.utn.product_service.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductRequest loanRequest(Integer installments, BigDecimal annualRate) {
        return new ProductRequest("LN-0001", ProductType.LOAN, 1L, null, null,
                new BigDecimal("1000000"), installments, annualRate, null);
    }

    // ---------- create ----------

    @Test
    @DisplayName("Creating a valid loan saves it and returns the calculated installment")
    void create_validLoan_savesAndReturnsInstallment() {
        when(productRepository.existsByNumber("LN-0001")).thenReturn(false);
        when(productRepository.saveAndFlush(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = productService.create(loanRequest(12, new BigDecimal("60")));

        assertEquals("LOAN", response.type());
        assertEquals(1L, response.customerId());
        assertEquals(0, new BigDecimal("112825.41").compareTo(response.monthlyInstallment()));
        verify(productRepository).saveAndFlush(any(Loan.class));
    }

    @Test
    @DisplayName("A loan with missing fields reports all of them and is not saved")
    void create_loanWithMissingFields_reportsAllAndDoesNotSave() {
        InvalidProductDataException ex = assertThrows(InvalidProductDataException.class,
                () -> productService.create(loanRequest(null, null)));

        assertEquals(List.of("installments: is required", "annualRate: is required"), ex.getDetails());
        verify(productRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("A duplicate product number is rejected and not saved")
    void create_duplicateNumber_throwsAndDoesNotSave() {
        when(productRepository.existsByNumber("LN-0001")).thenReturn(true);

        assertThrows(DuplicateResourceException.class,
                () -> productService.create(loanRequest(12, new BigDecimal("60"))));
        verify(productRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("A unique-constraint violation in the database becomes DuplicateResourceException")
    void create_databaseRejectsDuplicate_translatesException() {
        when(productRepository.existsByNumber("LN-0001")).thenReturn(false);
        when(productRepository.saveAndFlush(any(Product.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThrows(DuplicateResourceException.class,
                () -> productService.create(loanRequest(12, new BigDecimal("60"))));
    }

    // ---------- update ----------

    @Test
    @DisplayName("The type of an existing product cannot be changed")
    void update_changingType_throwsAndDoesNotSave() {
        Loan existing = new Loan();
        existing.setNumber("LN-0001");
        existing.setCustomerId(1L);
        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));

        ProductRequest request = new ProductRequest("LN-0001", ProductType.ACCOUNT, 1L,
                "ARS", null, null, null, null, null);

        assertThrows(InvalidProductDataException.class, () -> productService.update(1L, request));
        verify(productRepository, never()).saveAndFlush(any());
    }

    // ---------- queries ----------

    @Test
    @DisplayName("Products of a customer are mapped with their type-specific fields")
    void findByCustomerId_mapsEachProductType() {
        Account account = new Account();
        account.setNumber("AC-0001");
        account.setCustomerId(1L);
        account.setCurrency("ARS");

        Card card = new Card();
        card.setNumber("CC-0001");
        card.setCustomerId(1L);
        card.setCreditLimit(new BigDecimal("500000"));

        when(productRepository.findByCustomerId(1L)).thenReturn(List.of(account, card));

        List<ProductResponse> result = productService.findByCustomerId(1L);

        assertEquals(2, result.size());
        assertEquals("ACCOUNT", result.get(0).type());
        assertEquals("ARS", result.get(0).currency());
        assertEquals("CARD", result.get(1).type());
        assertNull(result.get(1).currency());
    }

    @Test
    @DisplayName("Finding a non-existent product throws ProductNotFoundException")
    void findById_notFound_throws() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.findById(99L));
    }

    @Test
    @DisplayName("Deleting a non-existent product throws and deletes nothing")
    void delete_notFound_throwsAndDoesNotDelete() {
        when(productRepository.existsById(99L)).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> productService.delete(99L));
        verify(productRepository, never()).deleteById(any());
    }
}
