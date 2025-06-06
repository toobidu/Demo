package org.example.demo.Service.Impl;

import lombok.RequiredArgsConstructor;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.OrderItemMapper;
import org.example.demo.Modal.DTO.OrderItemDTO;
import org.example.demo.Modal.Entity.Order;
import org.example.demo.Modal.Entity.OrderItem;
import org.example.demo.Modal.Entity.Product;
import org.example.demo.Repository.OrderItemRepository;
import org.example.demo.Repository.OrderRepository;
import org.example.demo.Repository.ProductRepository;
import org.example.demo.Service.Interface.IOrderItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements IOrderItemService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final IProductPriceService productPriceService;
    private final OrderItemMapper orderItemMapper;

    @Override
    @Transactional
    public OrderItemDTO addOrderItem(OrderItemDTO orderItemDTO) {
        Order order = orderRepository.findById(orderItemDTO.getOrderId())
                .orElseThrow(() -> new UserFriendlyException("Order not found"));
        
        Product product = productRepository.findById(orderItemDTO.getProductId())
                .orElseThrow(() -> new UserFriendlyException("Product not found"));
        
        // Chỉ cho phép thêm item khi đơn hàng đang ở trạng thái pending_payment
        if (!order.getOrderStatus().equals("pending_payment")) {
            throw new UserFriendlyException("Cannot add items to order in current status");
        }
        
        // Tính giá dựa trên rank và size
        BigDecimal unitPrice = productPriceService.calculatePrice(
                product.getId(), orderItemDTO.getRank(), orderItemDTO.getSize());
        
        OrderItem orderItem = orderItemMapper.toEntity(orderItemDTO);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setUnitPrice(unitPrice);
        orderItem.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        
        OrderItem savedItem = orderItemRepository.save(orderItem);
        
        // Cập nhật tổng tiền của đơn hàng
        updateOrderTotal(order);
        
        return orderItemMapper.toDTO(savedItem);
    }

    @Override
    public OrderItemDTO getOrderItemById(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Order item not found"));
        
        return orderItemMapper.toDTO(orderItem);
    }

    @Override
    public List<OrderItemDTO> getOrderItemsByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new UserFriendlyException("Order not found"));
        
        return orderItemRepository.findByOrder(order).stream()
                .map(orderItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderItemDTO updateOrderItem(Long id, OrderItemDTO orderItemDTO) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Order item not found"));
        
        Order order = orderItem.getOrder();
        
        // Chỉ cho phép cập nhật khi đơn hàng đang ở trạng thái pending_payment
        if (!order.getOrderStatus().equals("pending_payment")) {
            throw new UserFriendlyException("Cannot update items in current order status");
        }
        
        // Cập nhật thông tin
        orderItem.setQuantity(orderItemDTO.getQuantity());
        orderItem.setRank(orderItemDTO.getRank());
        orderItem.setSize(orderItemDTO.getSize());
        
        // Tính lại giá
        BigDecimal unitPrice = productPriceService.calculatePrice(
                orderItem.getProduct().getId(), orderItem.getRank(), orderItem.getSize());
        orderItem.setUnitPrice(unitPrice);
        orderItem.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        
        OrderItem updatedItem = orderItemRepository.save(orderItem);
        
        // Cập nhật tổng tiền của đơn hàng
        updateOrderTotal(order);
        
        return orderItemMapper.toDTO(updatedItem);
    }

    @Override
    @Transactional
    public void deleteOrderItem(Long id) {
        OrderItem orderItem = orderItemRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Order item not found"));
        
        Order order = orderItem.getOrder();
        
        // Chỉ cho phép xóa khi đơn hàng đang ở trạng thái pending_payment
        if (!order.getOrderStatus().equals("pending_payment")) {
            throw new UserFriendlyException("Cannot delete items in current order status");
        }
        
        orderItemRepository.deleteById(id);
        
        // Cập nhật tổng tiền của đơn hàng
        updateOrderTotal(order);
    }

    @Override
    @Transactional
    public void deleteOrderItemsByOrderId(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new UserFriendlyException("Order not found"));
        
        orderItemRepository.deleteByOrder(order);
        
        // Đặt tổng tiền về 0
        order.setTotalAmount(BigDecimal.ZERO);
        orderRepository.save(order);
    }
    
    private void updateOrderTotal(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrder(order);
        BigDecimal total = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        order.setTotalAmount(total);
        orderRepository.save(order);
    }
}