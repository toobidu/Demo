package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.ProductDTO;

import java.util.List;

public interface IProductService {
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO getProductById(Long id);
    ProductDTO getProductBySku(String sku);
    List<ProductDTO> getAllProducts();
    ProductDTO updateProduct(Long id, ProductDTO productDTO);
    void deleteProduct(Long id);
    List<ProductDTO> searchProducts(String keyword);
}