package com.utn.customer_service.client;

import com.utn.customer_service.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/products/customer/{customerId}")
    List<ProductDto> findByCustomerId(@PathVariable("customerId") Long customerId);
}
