package org.example.demo.Modal.DTO.Products;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductPriceDTO {
    private Long id;
    private Long productId;
    private String rank;
    private String size;
    private BigDecimal price;
}
