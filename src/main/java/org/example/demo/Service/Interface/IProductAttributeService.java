package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.Products.ProductAttributeDTO;
import org.springframework.data.domain.Page;

public interface IProductAttributeService {
    ProductAttributeDTO createProductAttribute(ProductAttributeDTO productAttributeDTO);

    ProductAttributeDTO updateProductAttribute(Long id, ProductAttributeDTO productAttributeDTO);

    void deleteProductAttribute(Long id);

    ProductAttributeDTO getProductAttribute(Long id);

    Page<ProductAttributeDTO> getProductAttributes(Long productId, int page, int size);

    Page<ProductAttributeDTO> getAllProductAttributes(int page, int size);
}
