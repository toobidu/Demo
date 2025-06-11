package org.example.demo.Modal.DTO.Products;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeDTO {
    private Long id;
    private Long productId;
    private String attributeKey;
    private String attributeValue;
}
