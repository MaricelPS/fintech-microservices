package com.utn.customer_service.mapper;

import com.utn.customer_service.dto.CustomerCreateRequest;
import com.utn.customer_service.dto.CustomerResponse;
import com.utn.customer_service.dto.CustomerUpdateRequest;
import com.utn.customer_service.model.Customer;

public final class CustomerMapper {

    private CustomerMapper() {
    }

    // ---------- DTO -> entity ----------

    public static Customer toEntity(CustomerCreateRequest request) {
        Customer customer = new Customer();
        customer.setName(request.name());
        customer.setDocumentNumber(request.documentNumber());
        customer.setEmail(request.email());

        // The initial balance goes through the business rule, not a setter
        if (request.initialBalance() != null && request.initialBalance().signum() > 0) {
            customer.deposit(request.initialBalance());
        }
        return customer;
    }

    public static void updateEntity(Customer customer, CustomerUpdateRequest request) {
        customer.setName(request.name());
        customer.setEmail(request.email());
    }

    // ---------- entity -> DTO ----------

    public static CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getDocumentNumber(),
                customer.getEmail(),
                customer.getBalance()
        );
    }
}
