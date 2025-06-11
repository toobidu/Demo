package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.Products.ProductDTO;
import org.example.demo.Modal.Entity.Products.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductDTO toDTO(Product product);

    Product toEntity(ProductDTO productDTO);
}
