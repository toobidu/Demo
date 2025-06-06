package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.OrderDTO;
import org.example.demo.Modal.Entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, WalletMapper.class, OrderItemMapper.class})
public interface OrderMapper {
    OrderDTO toDTO(Order order);
    Order toEntity(OrderDTO orderDTO);
}