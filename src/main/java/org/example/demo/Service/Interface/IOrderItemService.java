package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.OrderItemDTO;

import java.util.List;

public interface IOrderItemService {
    OrderItemDTO addOrderItem(OrderItemDTO orderItemDTO);
    OrderItemDTO getOrderItemById(Long id);
    List<OrderItemDTO> getOrderItemsByOrderId(Long orderId);
    OrderItemDTO updateOrderItem(Long id, OrderItemDTO orderItemDTO);
    void deleteOrderItem(Long id);
    void deleteOrderItemsByOrderId(Long orderId);
}