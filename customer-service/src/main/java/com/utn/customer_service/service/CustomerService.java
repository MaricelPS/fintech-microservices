package com.utn.customer_service.service;

import com.utn.customer_service.dto.CustomerCreateRequest;
import com.utn.customer_service.dto.CustomerResponse;
import com.utn.customer_service.dto.CustomerUpdateRequest;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerService {

    List<CustomerResponse> findAll();

    CustomerResponse findById(Long id);

    CustomerResponse create(CustomerCreateRequest request);

    CustomerResponse update(Long id, CustomerUpdateRequest request);

    void delete(Long id);

    CustomerResponse deposit(Long id, BigDecimal amount);

    CustomerResponse withdraw(Long id, BigDecimal amount);
}