package org.example.demo.Service.Impl;

import lombok.RequiredArgsConstructor;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.ProductAttributeMapper;
import org.example.demo.Modal.DTO.ProductAttributeDTO;
import org.example.demo.Modal.Entity.Product;
import org.example.demo.Modal.Entity.ProductAttribute;
import org.example.demo.Repository.ProductAttributeRepository;
import org.example.demo.Repository.ProductRepository;
import org.example.demo.Service.Interface.IProductAttributeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductAttributeServiceImpl implements IProductAttributeService {
    private final ProductAttributeRepository attributeRepository;
    private final ProductRepository productRepository;
    private final ProductAttributeMapper attributeMapper;

    @Override
    @Transactional
    public ProductAttributeDTO addProductAttribute(ProductAttributeDTO attributeDTO) {
        Product product = productRepository.findById(attributeDTO.getProductId())
                .orElseThrow(() -> new UserFriendlyException("Product not found"));
        
        ProductAttribute attribute = attributeMapper.toEntity(attributeDTO);
        attribute.setProduct(product);
        
        ProductAttribute savedAttribute = attributeRepository.save(attribute);
        return attributeMapper.toDTO(savedAttribute);
    }

    @Override
    public List<ProductAttributeDTO> getProductAttributes(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new UserFriendlyException("Product not found"));
        
        return attributeRepository.findByProduct(product).stream()
                .map(attributeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductAttributeDTO> getProductAttributesByKey(Long productId, String key) {
        return attributeRepository.findByProductAndKey(productId, key).stream()
                .map(attributeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> getProductAttributeKeys(Long productId) {
        return attributeRepository.findDistinctKeysByProductId(productId);
    }

    @Override
    @Transactional
    public ProductAttributeDTO updateProductAttribute(Long attributeId, ProductAttributeDTO attributeDTO) {
        ProductAttribute attribute = attributeRepository.findById(attributeId)
                .orElseThrow(() -> new UserFriendlyException("Product attribute not found"));
        
        attribute.setAttributeKey(attributeDTO.getAttributeKey());
        attribute.setAttributeValue(attributeDTO.getAttributeValue());
        
        ProductAttribute updatedAttribute = attributeRepository.save(attribute);
        return attributeMapper.toDTO(updatedAttribute);
    }

    @Override
    @Transactional
    public void deleteProductAttribute(Long attributeId) {
        if (!attributeRepository.existsById(attributeId)) {
            throw new UserFriendlyException("Product attribute not found");
        }
        attributeRepository.deleteById(attributeId);
    }

    @Override
    @Transactional
    public void deleteProductAttributes(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new UserFriendlyException("Product not found"));
        
        attributeRepository.deleteByProduct(product);
    }
}