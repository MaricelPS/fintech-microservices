package com.utn.product_service.service;

import com.utn.product_service.dto.ProductRequest;
import com.utn.product_service.dto.ProductResponse;
import com.utn.product_service.exception.DuplicateResourceException;
import com.utn.product_service.exception.InvalidProductDataException;
import com.utn.product_service.exception.ProductNotFoundException;
import com.utn.product_service.mapper.ProductMapper;
import com.utn.product_service.model.Product;
import com.utn.product_service.repository.ProductRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll().stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        return ProductMapper.toResponse(getProduct(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findByCustomerId(Long customerId) {
        return productRepository.findByCustomerId(customerId).stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponse create(ProductRequest request) {
        validateRequiredFieldsByType(request);

        if (productRepository.existsByNumber(request.number())) {
            throw new DuplicateResourceException("A product with number " + request.number() + " already exists");
        }

        Product product = ProductMapper.toEntity(request);
        return ProductMapper.toResponse(save(product));
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getProduct(id);

        if (product.getType() != request.type()) {
            throw new InvalidProductDataException("The type of an existing product cannot be changed");
        }
        if (!Objects.equals(product.getCustomerId(), request.customerId())) {
            throw new InvalidProductDataException("The owner of a product cannot be changed");
        }
        validateRequiredFieldsByType(request);

        boolean numberChanged = !product.getNumber().equals(request.number());
        if (numberChanged && productRepository.existsByNumber(request.number())) {
            throw new DuplicateResourceException("A product with number " + request.number() + " already exists");
        }

        ProductMapper.updateEntity(product, request);
        return ProductMapper.toResponse(save(product));
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    // ---------- Helpers ----------

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    private Product save(Product product) {
        try {
            return productRepository.saveAndFlush(product);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateResourceException("A product with number " + product.getNumber() + " already exists");
        }
    }

    /**
     * Each product type requires different fields.
     * All missing fields are reported together.
     */
    private void validateRequiredFieldsByType(ProductRequest request) {
        List<String> missing = new ArrayList<>();

        switch (request.type()) {
            case ACCOUNT -> {
                if (request.currency() == null) missing.add("currency");
            }
            case CARD -> {
                if (request.creditLimit() == null) missing.add("creditLimit");
            }
            case LOAN -> {
                if (request.amount() == null) missing.add("amount");
                if (request.installments() == null) missing.add("installments");
                if (request.annualRate() == null) missing.add("annualRate");
            }
            case INVESTMENT -> {
                if (request.amount() == null) missing.add("amount");
                if (request.termDays() == null) missing.add("termDays");
                if (request.annualRate() == null) missing.add("annualRate");
            }
        }

        if (!missing.isEmpty()) {
            throw new InvalidProductDataException(
                    "Missing required fields for product type " + request.type(),
                    missing.stream().map(field -> field + ": is required").toList());
        }
    }
}