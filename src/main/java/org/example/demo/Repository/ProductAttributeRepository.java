package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Products.ProductAttribute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {
    Page<ProductAttribute> findByProductId(Long productId, Pageable pageable);

    Page<ProductAttribute> findAll(Pageable pageable);
}

