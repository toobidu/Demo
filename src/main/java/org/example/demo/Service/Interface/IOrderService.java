package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.Orders.OrderDTO;

import java.util.List;

public interface IOrderService {
    OrderDTO createOrder(OrderDTO orderDTO);

    OrderDTO updateOrder(Long id, OrderDTO orderDTO);

    OrderDTO updateOrderStatus(Long id, String newStatus);

    void cancelOrder(Long id);

    OrderDTO getOrderById(Long id);

    List<OrderDTO> getOrdersForUser(Long userId);

    List<OrderDTO> getOrdersForAdmin();

    List<OrderDTO> getOrdersForPrintHouse();
}