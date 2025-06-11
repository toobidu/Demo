package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.OrderMapper;
import org.example.demo.Modal.DTO.Orders.OrderDTO;
import org.example.demo.Modal.Entity.Orders.Order;
import org.example.demo.Repository.*;
import org.example.demo.Service.Interface.IOrderService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImplement implements IOrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductPriceRepository productPriceRepository;
    private final WalletRepository walletRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {
        log.info("Tạo đơn đặt hàng cho người dùng: {}", orderDTO.getUserId());
        Order order = orderMapper.toEntity(orderDTO);
        orderDTO.setStatus("pending_payment");
        order = orderRepository.save(order);
        log.info("Đơn hàng được tạo với ID: {}", order.getId());
        return orderMapper.toDTO(order);
    }

    @Override
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        log.info("Cập nhật đơn hàng với ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Order not found: ID {}", id);
                    return new UserFriendlyException("Đơn hàng không tìm thấy!");
                });
        order.setStatus(orderDTO.getStatus());
        order.setTotalAmount(orderDTO.getTotalAmount());
        order = orderRepository.save(order);
        log.info("Order updated: ID {}", id);
        return orderMapper.toDTO(order);
    }

    @Override
    public void cancelOrder(Long id) {
        log.info("Hủy đơn hàng với ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Đơn hàng không tìm thấy: ID {}", id);
                    return new UserFriendlyException("Order not found!");
                });
        if (!order.getStatus().equals("pending_payment") && !order.getStatus().equals("order")) {
            log.error("Đơn hàng không thể hủy: {}", order.getStatus());
            throw new UserFriendlyException("Đơn hàng chỉ có thể hủy khi đang trong trạng thái chờ thanh toán!");
        }
        order.setStatus("cancelled");
        orderRepository.save(order);
        log.info("Đơn hàng đã được hủy: ID {}", id);
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        log.info("Truy vấn tới đơn hàng có ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Đơn hàng không tìm thấy: ID {}", id);
                    return new UserFriendlyException("Đơn hàng không tìm thấy!");
                });
        return orderMapper.toDTO(order);
    }

    @Override
    public List<OrderDTO> getOrdersForUser(Long userId) {
        log.info("Truy vấn tới đơn hàng có ID người dùng là: {}", userId);
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream().map(orderMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersForAdmin() {
        log.info("Truy vấn tới đơn hàng của admin");
        List<Order> orders = orderRepository.findAllOrdersForAdmin();
        return orders.stream().map(orderMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersForPrintHouse() {
        log.info("Truy vấn tới đơn hàng của nhà in");
        List<Order> orders = orderRepository.findOrdersForPrintHouse();
        return orders.stream().map(orderMapper::toDTO).collect(Collectors.toList());
    }
}
