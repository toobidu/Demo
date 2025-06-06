package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.ProductPriceDTO;
import org.example.demo.Modal.Entity.ProductPrice;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductPriceMapper {
    @Mapping(source = "product.id", target = "productId")
    ProductPriceDTO toDTO(ProductPrice productPrice);

    @Mapping(target = "product", ignore = true)
    ProductPrice toEntity(ProductPriceDTO productPriceDTO);
}