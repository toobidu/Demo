package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.Products.ProductDTO;

import java.util.List;

public interface IProductService {
    ProductDTO createProduct(ProductDTO productDTO);

    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    void deleteProduct(Long id);

    ProductDTO getProduct(Long id);

    List<ProductDTO> getAllProducts(String name);
}
