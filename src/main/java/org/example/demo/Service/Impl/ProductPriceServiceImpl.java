package org.example.demo.Service.Impl;

import lombok.RequiredArgsConstructor;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.ProductPriceMapper;
import org.example.demo.Modal.DTO.ProductPriceDTO;
import org.example.demo.Modal.Entity.Product;
import org.example.demo.Modal.Entity.ProductPrice;
import org.example.demo.Repository.ProductPriceRepository;
import org.example.demo.Repository.ProductRepository;
import org.example.demo.Service.Interface.IProductPriceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductPriceServiceImpl implements IProductPriceService {
    private final ProductPriceRepository priceRepository;
    private final ProductRepository productRepository;
    private final ProductPriceMapper priceMapper;

    @Override
    @Transactional
    public ProductPriceDTO addProductPrice(ProductPriceDTO priceDTO) {
        Product product = productRepository.findById(priceDTO.getProductId())
                .orElseThrow(() -> new UserFriendlyException("Product not found"));
        
        // Kiểm tra xem đã có giá cho rank và size này chưa
        if (priceRepository.findByProductAndRankAndSize(
                priceDTO.getProductId(), priceDTO.getRank(), priceDTO.getSize()).isPresent()) {
            throw new UserFriendlyException("Price for this rank and size already exists");
        }
        
        ProductPrice price = priceMapper.toEntity(priceDTO);
        price.setProduct(product);
        
        ProductPrice savedPrice = priceRepository.save(price);
        return priceMapper.toDTO(savedPrice);
    }

    @Override
    public List<ProductPriceDTO> getProductPrices(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new UserFriendlyException("Product not found"));
        
        return priceRepository.findByProduct(product).stream()
                .map(priceMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductPriceDTO getProductPrice(Long priceId) {
        ProductPrice price = priceRepository.findById(priceId)
                .orElseThrow(() -> new UserFriendlyException("Product price not found"));
        
        return priceMapper.toDTO(price);
    }

    @Override
    public ProductPriceDTO getProductPriceByRankAndSize(Long productId, String rank, String size) {
        ProductPrice price = priceRepository.findByProductAndRankAndSize(productId, rank, size)
                .orElseThrow(() -> new UserFriendlyException("Product price not found for this rank and size"));
        
        return priceMapper.toDTO(price);
    }

    @Override
    public BigDecimal calculatePrice(Long productId, String rank, String size) {
        // Tìm giá theo rank và size
        try {
            ProductPrice price = priceRepository.findByProductAndRankAndSize(productId, rank, size)
                    .orElse(null);
            
            if (price != null) {
                return price.getPrice();
            }
            
            // Nếu không tìm thấy giá cụ thể, trả về giá cơ bản của sản phẩm
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new UserFriendlyException("Product not found"));
            
            return product.getBasePrice();
        } catch (Exception e) {
            throw new UserFriendlyException("Error calculating price: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public ProductPriceDTO updateProductPrice(Long priceId, ProductPriceDTO priceDTO) {
        ProductPrice price = priceRepository.findById(priceId)
                .orElseThrow(() -> new UserFriendlyException("Product price not found"));
        
        // Kiểm tra nếu rank hoặc size thay đổi và đã tồn tại
        if ((!price.getRank().equals(priceDTO.getRank()) || !price.getSize().equals(priceDTO.getSize())) &&
                priceRepository.findByProductAndRankAndSize(
                        price.getProduct().getId(), priceDTO.getRank(), priceDTO.getSize()).isPresent()) {
            throw new UserFriendlyException("Price for this rank and size already exists");
        }
        
        price.setRank(priceDTO.getRank());
        price.setSize(priceDTO.getSize());
        price.setPrice(priceDTO.getPrice());
        
        ProductPrice updatedPrice = priceRepository.save(price);
        return priceMapper.toDTO(updatedPrice);
    }

    @Override
    @Transactional
    public void deleteProductPrice(Long priceId) {
        if (!priceRepository.existsById(priceId)) {
            throw new UserFriendlyException("Product price not found");
        }
        priceRepository.deleteById(priceId);
    }

    @Override
    @Transactional
    public void deleteProductPrices(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new UserFriendlyException("Product not found"));
        
        priceRepository.deleteByProduct(product);
    }
}