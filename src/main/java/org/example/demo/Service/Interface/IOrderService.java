package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.OrderDTO;

import java.util.List;

public interface IOrderService {
    OrderDTO createOrder(OrderDTO orderDTO, String username);
    OrderDTO getOrderById(Long id, String username);
    List<OrderDTO> getAllOrders(String username);
    OrderDTO updateOrderStatus(Long id, String status, String username);
    void cancelOrder(Long id, String username);
    List<OrderDTO> getOrdersByStatus(List<String> statuses, String username);
}