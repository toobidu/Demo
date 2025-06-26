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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductPriceServiceImplement implements IProductPriceService {

    private final ProductPriceRepository productPriceRepository;
    private final ProductRepository productRepository;
    private final ProductPriceMapper productPriceMapper;

    @Override
    public ProductPriceDTO createProductPrice(ProductPriceDTO dto) {
        log.info("Creating product price - Product ID: {}, Rank: {}, Size: {}",
                dto.getProductId(), dto.getRank(), dto.getSize());

        getProductById(dto.getProductId()); // Verify product exists
        ProductPrice price = productPriceMapper.toEntity(dto);
        price.setProductId(dto.getProductId());

        price = productPriceRepository.save(price);
        log.info("Product price created - ID: {}", price.getId());

        return productPriceMapper.toDTO(price);
    }

    @Override
    public ProductPriceDTO updateProductPrice(Long id, ProductPriceDTO dto) {
        log.info("Updating product price - ID: {}", id);

        ProductPrice price = getProductPriceById(id);
        if (!price.getProductId().equals(dto.getProductId())) {
            getProductById(dto.getProductId()); // Verify new product exists
            price.setProductId(dto.getProductId());
        }
        price.setRank(dto.getRank());
        price.setSize(dto.getSize());
        price.setPrice(dto.getPrice());
        price.setBase(dto.isBase());

        price = productPriceRepository.save(price);
        log.info("Product price updated - ID: {}", id);

        return productPriceMapper.toDTO(price);
    }

    @Override
    public void deleteProductPrice(Long id) {
        log.info("Deleting product price - ID: {}", id);
        getProductPriceById(id); // Ensure it exists
        productPriceRepository.deleteById(id);
        log.info("Product price deleted - ID: {}", id);
    }

    @Override
    public ProductPriceDTO getProductPrice(Long id) {
        log.info("Retrieving product price - ID: {}", id);
        return productPriceMapper.toDTO(getProductPriceById(id));
    }

    @Override
    public Page<ProductPriceDTO> getProductPrices(Long productId, String rank, int page, int size) {
        log.info("Retrieving product prices - Product ID: {}, Rank: {}, page: {}, size: {}",
                productId, rank, page, size);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ProductPrice> prices;

        if (productId != null && rank != null) {
            prices = productPriceRepository.findByProductIdAndRank(productId, rank, pageable);
        } else if (productId != null) {
            prices = productPriceRepository.findByProductId(productId, pageable);
        } else {
            prices = productPriceRepository.findAll(pageable);
        }

        return prices.map(productPriceMapper::toDTO);
    }

    @Override
    public Page<ProductPriceDTO> getAllProductPrices(int page, int size) {
        return getProductPrices(null, null, page, size);
    }

    private Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found - ID: {}", id);
                    return new UserFriendlyException("Product not found");
                });
    }

    private ProductPrice getProductPriceById(Long id) {
        return productPriceRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product price not found - ID: {}", id);
                    return new UserFriendlyException("Product price not found");
                });
    }
}
