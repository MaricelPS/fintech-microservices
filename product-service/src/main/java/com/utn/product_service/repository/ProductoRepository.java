package com.utn.product_service.repository;


import com.utn.product_service.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByClienteId(Long clienteId);

    boolean existsByNumero(String numero);
}
