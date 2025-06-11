package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.Orders.OrderItemDTO;

import java.util.List;

public interface IOrderItemService {
    OrderItemDTO createOrderItem(OrderItemDTO orderItemDTO);
    OrderItemDTO updateOrderItem(Long id, OrderItemDTO orderItemDTO);
    void deleteOrderItem(Long id);
    OrderItemDTO getOrderItem(Long id);
    List<OrderItemDTO> getOrderItems(Long orderId);
}
