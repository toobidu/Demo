package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.ProductAttributeMapper;
import org.example.demo.Modal.DTO.Products.ProductAttributeDTO;
import org.example.demo.Modal.Entity.Products.Product;
import org.example.demo.Modal.Entity.Products.ProductAttribute;
import org.example.demo.Repository.ProductAttributeRepository;
import org.example.demo.Repository.ProductRepository;
import org.example.demo.Service.Interface.IProductAttributeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductAttributeImplement implements IProductAttributeService {

    private final ProductAttributeRepository productAttributeRepository;
    private final ProductAttributeMapper productAttributeMapper;
    private final ProductRepository productRepository;

    @Override
    public ProductAttributeDTO createProductAttribute(ProductAttributeDTO productAttributeDTO) {
        log.info("Creating product attribute for productId: {}, name: {}",
                productAttributeDTO.getProductId(), productAttributeDTO.getAttributeKey());

        Product product = getProductById(productAttributeDTO.getProductId());

        ProductAttribute attribute = productAttributeMapper.toEntity(productAttributeDTO);
        attribute.setProduct(product);
        attribute = productAttributeRepository.save(attribute);

        log.info("Product attribute created with ID: {}", attribute.getId());
        return productAttributeMapper.toDTO(attribute);
    }

    @Override
    public ProductAttributeDTO updateProductAttribute(Long id, ProductAttributeDTO productAttributeDTO) {
        log.info("Updating product attribute ID: {}", id);

        ProductAttribute attribute = getAttributeById(id);
        attribute.setAttributeKey(productAttributeDTO.getAttributeKey());
        attribute.setAttributeValue(productAttributeDTO.getAttributeValue());
        attribute = productAttributeRepository.save(attribute);

        log.info("Product attribute updated: ID {}", id);
        return productAttributeMapper.toDTO(attribute);
    }

    @Override
    public void deleteProductAttribute(Long id) {
        log.info("Deleting product attribute ID: {}", id);
        getAttributeById(id); // Check tồn tại
        productAttributeRepository.deleteById(id);
        log.info("Product attribute deleted: ID {}", id);
    }

    @Override
    public ProductAttributeDTO getProductAttribute(Long id) {
        log.info("Retrieving product attribute ID: {}", id);
        return productAttributeMapper.toDTO(getAttributeById(id));
    }

    @Override
    public List<ProductAttributeDTO> getProductAttributes(Long productId) {
        log.info("Retrieving product attributes for productId: {}", productId);

        List<ProductAttribute> attributes = (productId != null)
                ? productAttributeRepository.findByProductId(productId)
                : productAttributeRepository.findAll();

        return attributes.stream()
                .map(productAttributeMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Tách nhỏ logic

    private Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found: ID {}", id);
                    return new UserFriendlyException("Product not found");
                });
    }

    private ProductAttribute getAttributeById(Long id) {
        return productAttributeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product attribute not found: ID {}", id);
                    return new UserFriendlyException("Product attribute not found");
                });
    }
}

