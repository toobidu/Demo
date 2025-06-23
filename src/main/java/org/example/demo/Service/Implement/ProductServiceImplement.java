package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.ProductMapper;
import org.example.demo.Modal.DTO.Products.ProductDTO;
import org.example.demo.Modal.Entity.Products.Product;
import org.example.demo.Repository.ProductRepository;
import org.example.demo.Service.Interface.IProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImplement implements IProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        log.info("Creating product: {}", productDTO.getProductName());
        Product product = buildProductFromDTO(productDTO);
        product = productRepository.save(product);
        log.info("Product created with ID: {}", product.getId());
        return productMapper.toDTO(product);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        log.info("Updating product ID: {}", id);
        Product product = findProductById(id);
        updateProductFields(product, productDTO);
        product.setUpdatedAt(LocalDateTime.now());
        product = productRepository.save(product);
        log.info("Product updated: ID {}", id);
        return productMapper.toDTO(product);
    }

    @Override
    public void deleteProduct(Long id) {
        log.info("Deleting product ID: {}", id);
        findProductById(id); // để throw nếu không tồn tại
        productRepository.deleteById(id);
        log.info("Product deleted: ID {}", id);
    }

    @Override
    public ProductDTO getProduct(Long id) {
        log.info("Retrieving product ID: {}", id);
        Product product = findProductById(id);
        return productMapper.toDTO(product);
    }

    @Override
    public Page<ProductDTO> getAllProducts(String productName, int page, int size) {
        log.info("Retrieving products with name: {}", productName);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Product> productPage;

        if (productName != null && !productName.isEmpty()) {
            productPage = productRepository.searchByProductName(productName, pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        return productPage.map(productMapper::toDTO);
    }

    // Chia nhỏ logic

    private Product findProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> {
            log.error("Product not found: ID {}", id);
            return new UserFriendlyException("Product not found");
        });
    }

    private Product buildProductFromDTO(ProductDTO dto) {
        Product product = productMapper.toEntity(dto);
        product.setSku(dto.getSku());
        product.setProductName(dto.getProductName());
        product.setBasePrice(dto.getBasePrice());
        product.setDescription(dto.getDescription());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }

    private void updateProductFields(Product product, ProductDTO dto) {
        product.setProductName(dto.getProductName());
        product.setDescription(dto.getDescription());
        product.setBasePrice(dto.getBasePrice());
    }
}
