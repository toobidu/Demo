package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.Orders.OrderDTO;
import org.example.demo.Modal.Entity.Orders.Order;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderDTO toDTO(Order order);

    Order toEntity(OrderDTO orderDTO);
}
