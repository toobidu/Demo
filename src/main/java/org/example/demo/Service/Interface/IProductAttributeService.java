package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.ProductAttributeDTO;

import java.util.List;
import java.util.Set;

public interface IProductAttributeService {
    ProductAttributeDTO addProductAttribute(ProductAttributeDTO attributeDTO);
    List<ProductAttributeDTO> getProductAttributes(Long productId);
    List<ProductAttributeDTO> getProductAttributesByKey(Long productId, String key);
    Set<String> getProductAttributeKeys(Long productId);
    ProductAttributeDTO updateProductAttribute(Long attributeId, ProductAttributeDTO attributeDTO);
    void deleteProductAttribute(Long attributeId);
    void deleteProductAttributes(Long productId);
}