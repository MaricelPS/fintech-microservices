package com.utn.customer_service.repository;

import com.utn.customer_service.model.Customer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer(String name, String documentNumber, String email) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setDocumentNumber(documentNumber);
        customer.setEmail(email);
        return customer;
    }

    @Test
    @DisplayName("existsByEmail detects an existing email")
    void existsByEmail_existingAndMissing() {
        customerRepository.saveAndFlush(customer("Ana Gomez", "30123456", "ana@mail.com"));

        assertTrue(customerRepository.existsByEmail("ana@mail.com"));
        assertFalse(customerRepository.existsByEmail("nobody@mail.com"));
    }

    @Test
    @DisplayName("existsByEmailAndIdNot ignores the customer's own email")
    void existsByEmailAndIdNot_sameCustomer_returnsFalse() {
        Customer ana = customerRepository.saveAndFlush(customer("Ana Gomez", "30123456", "ana@mail.com"));

        assertFalse(customerRepository.existsByEmailAndIdNot("ana@mail.com", ana.getId()));
    }

    @Test
    @DisplayName("existsByEmailAndIdNot detects an email used by another customer")
    void existsByEmailAndIdNot_otherCustomer_returnsTrue() {
        customerRepository.saveAndFlush(customer("Ana Gomez", "30123456", "ana@mail.com"));
        Customer luis = customerRepository.saveAndFlush(customer("Luis Perez", "28999888", "luis@mail.com"));

        assertTrue(customerRepository.existsByEmailAndIdNot("ana@mail.com", luis.getId()));
    }

    @Test
    @DisplayName("The database rejects a duplicate document number")
    void saveAndFlush_duplicateDocument_throws() {
        customerRepository.saveAndFlush(customer("Ana Gomez", "30123456", "ana@mail.com"));

        assertThrows(DataIntegrityViolationException.class, () -> customerRepository.saveAndFlush(
                customer("Ana Lopez", "30123456", "other@mail.com")));
    }
}
