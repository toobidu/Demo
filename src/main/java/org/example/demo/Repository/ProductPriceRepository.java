package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Product;
import org.example.demo.Modal.Entity.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    List<ProductPrice> findByProduct(Product product);
    
    @Query("SELECT p FROM ProductPrice p WHERE p.product.id = :productId AND p.rank = :rank AND p.size = :size")
    Optional<ProductPrice> findByProductAndRankAndSize(
            @Param("productId") Long productId, 
            @Param("rank") String rank, 
            @Param("size") String size);
    
    void deleteByProduct(Product product);
}
