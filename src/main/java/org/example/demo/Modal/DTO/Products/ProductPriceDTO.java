package org.example.demo.Modal.DTO.Products;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductPriceDTO {
    Long id;
    Long productId;
    String typeCode;
    BigDecimal salePrice;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
