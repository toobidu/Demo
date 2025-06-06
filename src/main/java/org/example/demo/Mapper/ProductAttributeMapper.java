package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.ProductAttributeDTO;
import org.example.demo.Modal.Entity.ProductAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductAttributeMapper {
    @Mapping(source = "product.id", target = "productId")
    ProductAttributeDTO toDTO(ProductAttribute productAttribute);
    
    @Mapping(target = "product", ignore = true)
    ProductAttribute toEntity(ProductAttributeDTO productAttributeDTO);
}