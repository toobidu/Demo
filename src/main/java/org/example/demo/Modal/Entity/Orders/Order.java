package org.example.demo.Modal.Entity.Orders;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "wallet_id")
    private Long walletId;

    @Column(name = "status")
    private String status;

    @Column(name = "total_amount", columnDefinition = "NUMERIC(18, 2)")
    private BigDecimal totalAmount;

    @Column(name = "print_price", columnDefinition = "NUMERIC(18, 2)")
    private BigDecimal printPrice;

    @Column(name = "ship_price", columnDefinition = "NUMERIC(18, 2)")
    private BigDecimal shipPrice;

    @Column(name = "pre_ship_price", columnDefinition = "NUMERIC(18, 2)")
    private BigDecimal preShipPrice;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
