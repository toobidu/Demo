package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.Products.ProductAttributeDTO;
import org.example.demo.Modal.Entity.Products.ProductAttribute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductAttributeMapper {
    ProductAttributeDTO toDTO(ProductAttribute entity);

    ProductAttribute toEntity(ProductAttributeDTO dto);
}
