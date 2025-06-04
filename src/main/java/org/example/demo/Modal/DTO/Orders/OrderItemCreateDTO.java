package org.example.demo.Modal.DTO.Orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OrderItemCreateDTO {
    Long productId;
    Integer quantity;
    String sizeCode;
    BigDecimal salePrice;
    BigDecimal originalPrice;
}
