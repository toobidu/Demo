package org.example.demo.Modal.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "orders")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "status_code", nullable = false)
    String statusCode;

    @Column(name = "total_price", precision = 12, scale = 2)
    BigDecimal totalPrice;

    @Column(name = "printing_price", precision = 12, scale = 2)
    BigDecimal printingPrice;

    @Column(name = "shipping_price", precision = 12, scale = 2)
    BigDecimal shippingPrice;

    @Column(name = "pre_shipping_price", precision = 12, scale = 2)
    BigDecimal preShippingPrice;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "status_code", nullable = false)
    DictionaryItem statusDictionaryItem;
}
