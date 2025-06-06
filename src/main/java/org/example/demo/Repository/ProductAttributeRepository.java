package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Product;
import org.example.demo.Modal.Entity.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {
    List<ProductAttribute> findByProduct(Product product);
    
    List<ProductAttribute> findByProductAndKey(Product product, String key);
    
    @Query("SELECT DISTINCT a.key FROM ProductAttribute a WHERE a.product.id = :productId")
    Set<String> findDistinctKeysByProductId(@Param("productId") Long productId);
    
    void deleteByProduct(Product product);
}
