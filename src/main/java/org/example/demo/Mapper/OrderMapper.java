package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.Orders.OrderCreateDTO;
import org.example.demo.Modal.DTO.Orders.OrderDTO;
import org.example.demo.Modal.Entity.Orders.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDTO toDTO(Order order);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "items", ignore = true)
    Order toEntity(OrderCreateDTO orderCreateDTO);

    List<OrderDTO> toDTOList(List<Order> orders);
}
