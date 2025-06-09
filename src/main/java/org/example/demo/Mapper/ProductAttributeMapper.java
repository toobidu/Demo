package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.Products.ProductAttributeDTO;
import org.example.demo.Modal.Entity.Products.ProductAttribute;
import org.mapstruct.Mapper;

@Mapper
public interface ProductAttributeMapper {
    ProductAttributeDTO toDTO(ProductAttribute productAttribute);
    ProductAttribute toEntity(ProductAttributeDTO productAttributeDTO);
}
