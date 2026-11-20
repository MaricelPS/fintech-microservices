package com.utn.customer_service.service;

import com.utn.customer_service.dto.CustomerCreateRequest;
import com.utn.customer_service.dto.CustomerResponse;
import com.utn.customer_service.dto.CustomerUpdateRequest;
import com.utn.customer_service.exception.CustomerNotFoundException;
import com.utn.customer_service.exception.DuplicateResourceException;
import com.utn.customer_service.mapper.CustomerMapper;
import com.utn.customer_service.model.Customer;
import com.utn.customer_service.repository.CustomerRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        return customerRepository.findAll().stream()
                .map(CustomerMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        return CustomerMapper.toResponse(getCustomer(id));
    }

    @Override
    public CustomerResponse create(CustomerCreateRequest request) {
        if (customerRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException("A customer with email " + request.email() + " already exists");
        }
        if (customerRepository.existsByDocumentNumber(request.documentNumber())) {
            throw new DuplicateResourceException(
                    "A customer with document number " + request.documentNumber() + " already exists");
        }

        Customer customer = CustomerMapper.toEntity(request);
        return CustomerMapper.toResponse(save(customer));
    }

    @Override
    public CustomerResponse update(Long id, CustomerUpdateRequest request) {
        Customer customer = getCustomer(id);

        if (customerRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new DuplicateResourceException("A customer with email " + request.email() + " already exists");
        }

        CustomerMapper.updateEntity(customer, request);
        return CustomerMapper.toResponse(save(customer));
    }

    @Override
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException(id);
        }
        customerRepository.deleteById(id);
    }

    @Override
    public CustomerResponse deposit(Long id, BigDecimal amount) {
        Customer customer = getCustomer(id);
        customer.deposit(amount);
        return CustomerMapper.toResponse(customerRepository.save(customer));
    }

    @Override
    public CustomerResponse withdraw(Long id, BigDecimal amount) {
        Customer customer = getCustomer(id);
        customer.withdraw(amount); // the business rule lives in the entity
        return CustomerMapper.toResponse(customerRepository.save(customer));
    }

    // ---------- Helpers ----------

    private Customer getCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    private Customer save(Customer customer) {
        try {
            return customerRepository.saveAndFlush(customer);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("Email or document number is already registered");
        }
    }
}