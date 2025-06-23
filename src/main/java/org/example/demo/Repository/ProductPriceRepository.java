package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Products.ProductPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    Page<ProductPrice> findByProductId(Long productId, Pageable pageable);

    Page<ProductPrice> findByProductIdAndRank(Long productId, String rank, Pageable pageable);

    Page<ProductPrice> findAll(Pageable pageable);

    List<ProductPrice> findByProductIdAndIsBaseTrue(Long productId);
}
