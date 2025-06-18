package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.Products.ProductPriceDTO;

import java.util.List;

public interface IProductPriceService {
    ProductPriceDTO createProductPrice(ProductPriceDTO productPriceDTO);

    ProductPriceDTO updateProductPrice(Long id, ProductPriceDTO productPriceDTO);

    void deleteProductPrice(Long id);

    ProductPriceDTO getProductPrice(Long id);

    List<ProductPriceDTO> getProductPrices(Long productId, String rank);

    List<ProductPriceDTO> getAllProductPrices();
}
