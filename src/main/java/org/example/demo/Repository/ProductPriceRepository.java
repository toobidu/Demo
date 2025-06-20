package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Products.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    List<ProductPrice> findByProductId(Long productId);

    List<ProductPrice> findByProductIdAndRank(Long productId, String rank);

    List<ProductPrice> findByProductIdAndIsBaseTrue(Long productId);
}
