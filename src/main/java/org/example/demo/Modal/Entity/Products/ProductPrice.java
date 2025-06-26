package org.example.demo.Modal.Entity.Products;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "product_prices")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
public class ProductPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "rank")
    private String rank;

    @Column(name = "size")
    private String size;

    @Column(name = "price", columnDefinition = "NUMERIC(18,2)")
    private BigDecimal price;

    @Column(name = "is_base")
    private boolean isBase;
}
