package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Products.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProductName(String productName);
}

