package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.ProductPriceDTO;

import java.math.BigDecimal;
import java.util.List;

public interface IProductPriceService {
    ProductPriceDTO addProductPrice(ProductPriceDTO priceDTO);
    List<ProductPriceDTO> getProductPrices(Long productId);
    ProductPriceDTO getProductPrice(Long priceId);
    ProductPriceDTO getProductPriceByRankAndSize(Long productId, String rank, String size);
    BigDecimal calculatePrice(Long productId, String rank, String size);
    ProductPriceDTO updateProductPrice(Long priceId, ProductPriceDTO priceDTO);
    void deleteProductPrice(Long priceId);
    void deleteProductPrices(Long productId);
}