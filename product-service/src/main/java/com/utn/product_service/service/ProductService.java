package com.utn.product_service.service;

import com.utn.product_service.dto.ProductRequest;
import com.utn.product_service.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    List<ProductResponse> findAll();

    ProductResponse findById(Long id);

    List<ProductResponse> findByCustomerId(Long customerId);

    ProductResponse create(ProductRequest request);

    ProductResponse update(Long id, ProductRequest request);

    void delete(Long id);
}
