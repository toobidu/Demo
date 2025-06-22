package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.Products.ProductDTO;
import org.example.demo.Modal.Entity.Products.Product;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {ProductPriceMapper.class, ProductAttributeMapper.class})
public interface ProductMapper {
    ProductDTO toDTO(Product product);

    Product toEntity(ProductDTO productDTO);
}
