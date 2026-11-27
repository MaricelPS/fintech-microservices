package com.utn.customer_service.controller;

import com.utn.customer_service.dto.*;
import com.utn.customer_service.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/customers")
@Tag(name = "Customers", description = "Customer management, balance operations and products")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "List all customers", description = "Roles: USER, ADMIN")
    @ApiResponse(responseCode = "200", description = "Customers retrieved")
    @GetMapping
    public List<CustomerResponse> findAll() {
        return customerService.findAll();
    }

    @Operation(summary = "Get a customer by id", description = "Roles: USER, ADMIN")
    @ApiResponse(responseCode = "200", description = "Customer found")
    @ApiResponse(responseCode = "404", description = "Customer not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public CustomerResponse findById(
            @Parameter(description = "Customer id", example = "1") @PathVariable Long id) {
        return customerService.findById(id);
    }

    @Operation(summary = "Create a customer",
            description = "Roles: ADMIN. The initial balance is optional and goes through the deposit rule.")
    @ApiResponse(responseCode = "201", description = "Customer created")
    @ApiResponse(responseCode = "400", description = "Invalid data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Email or document number already registered",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerCreateRequest request) {
        CustomerResponse created = customerService.create(request);
        return ResponseEntity.created(URI.create("/customers/" + created.id())).body(created);
    }

    @Operation(summary = "Update a customer",
            description = "Roles: ADMIN. Only name and email can be changed; the balance changes only "
                    + "through deposits and withdrawals.")
    @ApiResponse(responseCode = "200", description = "Customer updated")
    @ApiResponse(responseCode = "400", description = "Invalid data",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Customer not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Email used by another customer",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    public CustomerResponse update(
            @Parameter(description = "Customer id", example = "1") @PathVariable Long id,
            @Valid @RequestBody CustomerUpdateRequest request) {
        return customerService.update(id, request);
    }

    @Operation(summary = "Delete a customer", description = "Roles: ADMIN")
    @ApiResponse(responseCode = "204", description = "Customer deleted")
    @ApiResponse(responseCode = "404", description = "Customer not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "Customer id", example = "1") @PathVariable Long id) {
        customerService.delete(id);
    }

    @Operation(summary = "Deposit money", description = "Roles: ADMIN")
    @ApiResponse(responseCode = "200", description = "Deposit completed")
    @ApiResponse(responseCode = "400", description = "Invalid amount",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Customer not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{id}/deposit")
    public CustomerResponse deposit(
            @Parameter(description = "Customer id", example = "1") @PathVariable Long id,
            @Valid @RequestBody AmountRequest request) {
        return customerService.deposit(id, request.amount());
    }

    @Operation(summary = "Withdraw money", description = "Roles: ADMIN. The balance cannot go below zero.")
    @ApiResponse(responseCode = "200", description = "Withdrawal completed")
    @ApiResponse(responseCode = "400", description = "Invalid amount",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Customer not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "422", description = "Insufficient balance",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping("/{id}/withdraw")
    public CustomerResponse withdraw(
            @Parameter(description = "Customer id", example = "1") @PathVariable Long id,
            @Valid @RequestBody AmountRequest request) {
        return customerService.withdraw(id, request.amount());
    }

    @Operation(summary = "Get the products of a customer",
            description = "Roles: USER, ADMIN. Retrieved from product-service through Feign, "
                    + "located via Eureka.")
    @ApiResponse(responseCode = "200", description = "Products of the customer (possibly empty)")
    @ApiResponse(responseCode = "404", description = "Customer not found (product-service is not called)",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "503", description = "product-service is unavailable",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}/products")
    public List<ProductDto> findProducts(
            @Parameter(description = "Customer id", example = "1") @PathVariable Long id) {
        return customerService.findProducts(id);
    }
}