package org.example.demo.Modal.DTO.Products;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;
    private String sku;
    private String name;
    private BigDecimal basePrice;
    private String description;
    private List<ProductPriceDTO> productPrices;
    private List<ProductAttributeDTO> productAttributes;
}
