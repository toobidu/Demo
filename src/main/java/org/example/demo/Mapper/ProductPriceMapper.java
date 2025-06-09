package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.Products.ProductPriceDTO;
import org.example.demo.Modal.Entity.Products.ProductPrice;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductPriceMapper {
    ProductPriceDTO toDTO(ProductPrice productPrice);
    ProductPrice toEntity(ProductPriceDTO productPriceDTO);
}
