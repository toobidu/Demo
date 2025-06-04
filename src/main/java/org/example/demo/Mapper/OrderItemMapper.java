package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.Orders.OrderItemCreateDTO;
import org.example.demo.Modal.DTO.Orders.OrderItemDTO;
import org.example.demo.Modal.Entity.Orders.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemDTO toDTO(OrderItem orderItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "product", ignore = true)
    OrderItem toEntity(OrderItemCreateDTO orderItemCreateDTO);

    List<OrderItemDTO> toDTOList(List<OrderItem> orderItems);

}
