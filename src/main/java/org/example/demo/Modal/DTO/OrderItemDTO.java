package org.example.demo.Modal.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class OrderItemDTO {
    Long id;
    Integer quantity;
    BigDecimal originalPrice;
    ProductDTO product;
    OrderDTO order;
    String attributeKey;
    String attributeValue;
}
