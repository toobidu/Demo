package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT p FROM Product p JOIN p.attributes a WHERE a.attributeKey = :key AND a.attributeValue = :value")
    List<Product> findByAttribute(String key, String value);
}