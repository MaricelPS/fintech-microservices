package com.utn.product_service.controller;

import com.utn.product_service.dto.ErrorResponse;
import com.utn.product_service.dto.ProductRequest;
import com.utn.product_service.dto.ProductResponse;
import com.utn.product_service.service.ProductService;
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
@RequestMapping("/products")
@Tag(name = "Products", description = "Financial products: accounts, cards, loans and investments")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "List all products")
    @ApiResponse(responseCode = "200", description = "Products retrieved")
    @GetMapping
    public List<ProductResponse> findAll() {
        return productService.findAll();
    }

    @Operation(summary = "Get a product by id")
    @ApiResponse(responseCode = "200", description = "Product found")
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @GetMapping("/{id}")
    public ProductResponse findById(
            @Parameter(description = "Product id", example = "1") @PathVariable Long id) {
        return productService.findById(id);
    }

    @Operation(summary = "List the products of a customer",
            description = "Consumed by customer-service through Feign. Returns an empty list "
                    + "if the customer has no products: this service does not own customer data.")
    @ApiResponse(responseCode = "200", description = "Products of the customer (possibly empty)")
    @GetMapping("/customer/{customerId}")
    public List<ProductResponse> findByCustomerId(
            @Parameter(description = "Customer id", example = "1") @PathVariable Long customerId) {
        return productService.findByCustomerId(customerId);
    }

    @Operation(summary = "Create a product",
            description = "Required fields depend on the type: ACCOUNT needs currency; CARD needs creditLimit; "
                    + "LOAN needs amount, installments and annualRate; INVESTMENT needs amount, termDays and annualRate.")
    @ApiResponse(responseCode = "201", description = "Product created")
    @ApiResponse(responseCode = "400", description = "Invalid data or missing type-specific fields",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "A product with that number already exists",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.create(request);
        return ResponseEntity.created(URI.create("/products/" + created.id())).body(created);
    }

    @Operation(summary = "Update a product",
            description = "The product type and its owner cannot be changed.")
    @ApiResponse(responseCode = "200", description = "Product updated")
    @ApiResponse(responseCode = "400", description = "Invalid data, or attempt to change type or owner",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Another product already uses that number",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @PutMapping("/{id}")
    public ProductResponse update(
            @Parameter(description = "Product id", example = "1") @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @Operation(summary = "Delete a product")
    @ApiResponse(responseCode = "204", description = "Product deleted")
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "Product id", example = "1") @PathVariable Long id) {
        productService.delete(id);
    }
}