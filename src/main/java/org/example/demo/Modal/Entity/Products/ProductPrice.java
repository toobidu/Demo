package org.example.demo.Modal.Entity.Products;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "product_prices")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "rank")
    private String rank;

    @Column(name = "size")
    private String size;

    @Column(name = "price", columnDefinition = "NUMERIC(18,2)")
    private BigDecimal price;
}
