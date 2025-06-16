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
    private final ProductRepository productRepository;
    private final ProductPriceMapper productPriceMapper;

    @Override
    public ProductPriceDTO createProductPrice(ProductPriceDTO dto) {
        log.info("Creating product price - Product ID: {}, Rank: {}, Size: {}",
                dto.getProductId(), dto.getRank(), dto.getSize());

        Product product = getProductById(dto.getProductId());
        ProductPrice price = productPriceMapper.toEntity(dto);
        price.setProduct(product);

        price = productPriceRepository.save(price);
        log.info("Product price created - ID: {}", price.getId());

        return productPriceMapper.toDTO(price);
    }

    @Override
    public ProductPriceDTO updateProductPrice(Long id, ProductPriceDTO dto) {
        log.info("Updating product price - ID: {}", id);

        ProductPrice price = getProductPriceById(id);
        price.setRank(dto.getRank());
        price.setSize(dto.getSize());
        price.setPrice(dto.getPrice());

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
    public List<ProductPriceDTO> getProductPrices(Long productId, String rank) {
        log.info("Retrieving product prices - Product ID: {}, Rank: {}", productId, rank);
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

    @Override
    public List<ProductPriceDTO> getAllProductPrices() {
        return getProductPrices(null, null); // gọi lại hàm đã có để tránh lặp code
    }


    // Tách nhỏ logic

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

