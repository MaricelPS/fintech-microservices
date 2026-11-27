package com.utn.customer_service.service;

import com.utn.customer_service.client.ProductClient;
import com.utn.customer_service.dto.CustomerCreateRequest;
import com.utn.customer_service.dto.CustomerResponse;
import com.utn.customer_service.dto.CustomerUpdateRequest;
import com.utn.customer_service.dto.ProductDto;
import com.utn.customer_service.exception.CustomerNotFoundException;
import com.utn.customer_service.exception.DuplicateResourceException;
import com.utn.customer_service.exception.InsufficientBalanceException;
import com.utn.customer_service.exception.ProductServiceException;
import com.utn.customer_service.model.Customer;
import com.utn.customer_service.repository.CustomerRepository;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customerWithBalance(String balance) {
        Customer customer = new Customer();
        customer.setName("Ana Gomez");
        customer.setDocumentNumber("30123456");
        customer.setEmail("ana@mail.com");

        BigDecimal amount = new BigDecimal(balance);
        if (amount.signum() > 0) {
            customer.deposit(amount);
        }
        return customer;
    }

    private ProductDto account(String number) {
        return new ProductDto(1L, number, "ACCOUNT", "ACTIVE", null, "ARS",
                null, null, null, null, null, null, null);
    }

    // ---------- create / update ----------

    @Test
    @DisplayName("Creating a customer applies the initial balance")
    void create_withInitialBalance_savesCustomer() {
        when(customerRepository.existsByEmail("ana@mail.com")).thenReturn(false);
        when(customerRepository.existsByDocumentNumber("30123456")).thenReturn(false);
        when(customerRepository.saveAndFlush(any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CustomerResponse response = customerService.create(
                new CustomerCreateRequest("Ana Gomez", "30123456", "ana@mail.com", new BigDecimal("1000")));

        assertEquals("Ana Gomez", response.name());
        assertEquals(0, new BigDecimal("1000").compareTo(response.balance()));
    }

    @Test
    @DisplayName("A duplicate email is rejected and nothing is saved")
    void create_duplicateEmail_throwsAndDoesNotSave() {
        when(customerRepository.existsByEmail("ana@mail.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> customerService.create(
                new CustomerCreateRequest("Ana Lopez", "33444555", "ana@mail.com", null)));
        verify(customerRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Updating to an email used by another customer is rejected")
    void update_emailOfAnotherCustomer_throws() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customerWithBalance("0")));
        when(customerRepository.existsByEmailAndIdNot("luis@mail.com", 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> customerService.update(
                1L, new CustomerUpdateRequest("Ana Gomez", "luis@mail.com")));
        verify(customerRepository, never()).saveAndFlush(any());
    }

    // ---------- balance operations ----------

    @Test
    @DisplayName("Depositing updates the balance and saves the customer")
    void deposit_existingCustomer_updatesBalance() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customerWithBalance("100")));
        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CustomerResponse response = customerService.deposit(1L, new BigDecimal("50"));

        assertEquals(0, new BigDecimal("150").compareTo(response.balance()));
    }

    @Test
    @DisplayName("Withdrawing more than the balance is rejected and nothing is saved")
    void withdraw_insufficientBalance_throwsAndDoesNotSave() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customerWithBalance("100")));

        assertThrows(InsufficientBalanceException.class,
                () -> customerService.withdraw(1L, new BigDecimal("500")));
        verify(customerRepository, never()).save(any());
    }

    // ---------- Feign integration ----------

    @Test
    @DisplayName("findProducts returns the products obtained from product-service")
    void findProducts_existingCustomer_returnsProductsFromClient() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customerWithBalance("0")));
        when(productClient.findByCustomerId(1L)).thenReturn(List.of(account("AC-0001")));

        List<ProductDto> products = customerService.findProducts(1L);

        assertEquals(1, products.size());
        assertEquals("AC-0001", products.get(0).number());
    }

    @Test
    @DisplayName("findProducts for an unknown customer fails without calling product-service")
    void findProducts_unknownCustomer_throwsWithoutRemoteCall() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> customerService.findProducts(99L));
        verifyNoInteractions(productClient);
    }

    @Test
    @DisplayName("A Feign failure is translated into ProductServiceException")
    void findProducts_productServiceDown_throwsProductServiceException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customerWithBalance("0")));
        when(productClient.findByCustomerId(1L)).thenThrow(FeignException.class);

        assertThrows(ProductServiceException.class, () -> customerService.findProducts(1L));
    }
}
