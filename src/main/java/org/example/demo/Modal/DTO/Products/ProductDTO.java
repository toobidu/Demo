package org.example.demo.Modal.DTO.Products;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ProductDTO {
    Long id;
    String name;
    BigDecimal originalPrice;
    String typeCode;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<ProductPriceDTO> productPrices;
    List<ProductAttributeDTO> productAttributes;
}
