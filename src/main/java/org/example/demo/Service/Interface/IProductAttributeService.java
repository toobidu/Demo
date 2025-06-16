package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.Products.ProductAttributeDTO;

import java.util.List;

public interface IProductAttributeService {
    ProductAttributeDTO createProductAttribute(ProductAttributeDTO productAttributeDTO);

    ProductAttributeDTO updateProductAttribute(Long id, ProductAttributeDTO productAttributeDTO);

    void deleteProductAttribute(Long id);

    ProductAttributeDTO getProductAttribute(Long id);

    List<ProductAttributeDTO> getProductAttributes(Long productId);

    List<ProductAttributeDTO> getAllProductAttributes();

}
