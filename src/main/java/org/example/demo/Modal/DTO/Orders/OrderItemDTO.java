package org.example.demo.Modal.DTO.Orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.example.demo.Modal.DTO.Products.ProductDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OrderItemDTO {
    Long id;
    Long orderId;
    Long productId;
    Integer quantity;
    String sizeCode;
    BigDecimal salePrice;
    BigDecimal originalPrice;
    LocalDateTime createdDate;
    LocalDateTime updatedDate;
    ProductDTO product;
}
