package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.ProductPriceMapper;
import org.example.demo.Modal.DTO.Products.ProductPriceDTO;
import org.example.demo.Modal.Entity.Products.Product;
import org.example.demo.Modal.Entity.Products.ProductPrice;
import org.example.demo.Repository.ProductPriceRepository;
import org.example.demo.Repository.ProductRepository;
import org.example.demo.Service.Interface.IProductPriceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductPriceServiceImplement implements IProductPriceService {
    private final ProductPriceRepository productPriceRepository;
    private ProductRepository productRepository;
    private ProductPriceMapper productPriceMapper;

    @Override
    public ProductPriceDTO createProductPrice(ProductPriceDTO productPriceDTO) {
        log.info("Creating product price for productId: {}, rank: {}, size: {}",
                productPriceDTO.getProductId(), productPriceDTO.getRank(), productPriceDTO.getSize());
        Product product = productRepository.findById(productPriceDTO.getProductId())
                .orElseThrow(() -> {
                    log.error("Product not found: ID {}", productPriceDTO.getProductId());
                    return new UserFriendlyException("Product not found");
                });
        ProductPrice productPrice = productPriceMapper.toEntity(productPriceDTO);
        productPrice.setProduct(product);
        productPrice = productPriceRepository.save(productPrice);
        log.info("Product price created with ID: {}", productPrice.getId());
        return productPriceMapper.toDTO(productPrice);
    }

    @Override
    public ProductPriceDTO updateProductPrice(Long id, ProductPriceDTO productPriceDTO) {
        log.info("Updating product price ID: {}", id);
        ProductPrice productPrice = productPriceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product price not found: ID {}", id);
                    return new UserFriendlyException("Product price not found");
                });
        productPrice.setRank(productPriceDTO.getRank());
        productPrice.setSize(productPriceDTO.getSize());
        productPrice.setPrice(productPriceDTO.getPrice());
        productPrice = productPriceRepository.save(productPrice);
        log.info("Product price updated: ID {}", id);
        return productPriceMapper.toDTO(productPrice);
    }

    @Override
    public void deleteProductPrice(Long id) {
        log.info("Deleting product price ID: {}", id);
        productPriceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product price not found: ID {}", id);
                    return new UserFriendlyException("Product price not found");
                });
        productPriceRepository.deleteById(id);
        log.info("Product price deleted: ID {}", id);
    }

    @Override
    public ProductPriceDTO getProductPrice(Long id) {
        log.info("Retrieving product price ID: {}", id);
        ProductPrice productPrice = productPriceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product price not found: ID {}", id);
                    return new UserFriendlyException("Product price not found");
                });
        return productPriceMapper.toDTO(productPrice);
    }

    @Override
    public List<ProductPriceDTO> getProductPrices(Long productId, String rank) {
        log.info("Retrieving product prices for productId: {}, rank: {}", productId, rank);
        List<ProductPrice> prices;
        if (productId != null && rank != null) {
            prices = productPriceRepository.findByProductIdAndRank(productId, rank);
        } else if (productId != null) {
            prices = productPriceRepository.findByProductId(productId);
        } else {
            prices = productPriceRepository.findAll();
        }
        return prices.stream().map(productPriceMapper::toDTO).collect(Collectors.toList());
    }
}
