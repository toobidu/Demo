package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.Orders.OrderItemDTO;
import org.springframework.data.domain.Page;

public interface IOrderItemService {
    OrderItemDTO createOrderItem(OrderItemDTO orderItemDTO);

    OrderItemDTO updateOrderItem(Long id, OrderItemDTO orderItemDTO);

    void deleteOrderItem(Long id);

    OrderItemDTO getOrderItem(Long id);

    Page<OrderItemDTO> getOrderItems(Long orderId, int page, int size);
}
