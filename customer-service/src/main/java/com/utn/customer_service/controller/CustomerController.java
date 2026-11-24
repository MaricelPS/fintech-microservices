package com.utn.customer_service.controller;

import com.utn.customer_service.dto.AmountRequest;
import com.utn.customer_service.dto.CustomerCreateRequest;
import com.utn.customer_service.dto.CustomerResponse;
import com.utn.customer_service.dto.CustomerUpdateRequest;
import com.utn.customer_service.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.utn.customer_service.dto.ProductDto;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<CustomerResponse> findAll() {
        return customerService.findAll();
    }

    @GetMapping("/{id}")
    public CustomerResponse findById(@PathVariable Long id) {
        return customerService.findById(id);
    }

    @GetMapping("/{id}/products")
    public List<ProductDto> findProducts(@PathVariable Long id) {
        return customerService.findProducts(id);
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerCreateRequest request) {
        CustomerResponse created = customerService.create(request);
        return ResponseEntity.created(URI.create("/customers/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id,
                                   @Valid @RequestBody CustomerUpdateRequest request) {
        return customerService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        customerService.delete(id);
    }

    @PostMapping("/{id}/deposit")
    public CustomerResponse deposit(@PathVariable Long id,
                                    @Valid @RequestBody AmountRequest request) {
        return customerService.deposit(id, request.amount());
    }

    @PostMapping("/{id}/withdraw")
    public CustomerResponse withdraw(@PathVariable Long id,
                                     @Valid @RequestBody AmountRequest request) {
        return customerService.withdraw(id, request.amount());
    }
}