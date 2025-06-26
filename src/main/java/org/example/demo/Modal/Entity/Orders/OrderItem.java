package org.example.demo.Modal.Entity.Orders;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo.Modal.Entity.Products.Product;
import org.example.demo.Modal.Entity.Products.ProductPrice;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_price_id")
    private Long productPriceId;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "original_price", columnDefinition = "NUMERIC(18,2)")
    private BigDecimal originalPrice;
}
