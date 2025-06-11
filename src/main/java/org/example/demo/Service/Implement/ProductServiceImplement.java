package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.ProductMapper;
import org.example.demo.Modal.DTO.Products.ProductDTO;
import org.example.demo.Modal.Entity.Products.Product;
import org.example.demo.Repository.ProductRepository;
import org.example.demo.Service.Interface.IProductService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImplement implements IProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        log.info("Creating product: {}", productDTO.getName());
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
    public List<ProductDTO> getAllProducts(String name) {
        log.info("Retrieving products with name: {}", name);
        List<Product> products = name != null && !name.isEmpty()
                ? productRepository.findByNameContainingIgnoreCase(name)
                : productRepository.findAll();
        return products.stream().map(productMapper::toDTO).collect(Collectors.toList());
    }

    // Chia nhỏ logic

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found: ID {}", id);
                    return new UserFriendlyException("Product not found");
                });
    }

    private Product buildProductFromDTO(ProductDTO dto) {
        Product product = productMapper.toEntity(dto);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return product;
    }

    private void updateProductFields(Product product, ProductDTO dto) {
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setBasePrice(dto.getBasePrice());
    }
}
