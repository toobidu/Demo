package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.Orders.OrderDTO;
import org.springframework.data.domain.Page;

public interface IOrderService {
    OrderDTO createOrder(OrderDTO orderDTO);

    OrderDTO updateOrder(Long id, OrderDTO orderDTO);

    OrderDTO updateOrderStatus(Long id, String newStatus);

    void cancelOrder(Long id);

    OrderDTO getOrderById(Long id);

    Page<OrderDTO> getOrdersForUser(Long userId, int page, int size);

    Page<OrderDTO> getOrdersForAdmin(int page, int size);

    Page<OrderDTO> getOrdersForPrintHouse(int page, int size);
}