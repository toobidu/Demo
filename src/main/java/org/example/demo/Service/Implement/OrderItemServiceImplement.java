package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.OrderItemMapper;
import org.example.demo.Modal.DTO.Orders.OrderItemDTO;
import org.example.demo.Modal.Entity.Orders.Order;
import org.example.demo.Modal.Entity.Orders.OrderItem;
import org.example.demo.Modal.Entity.Products.ProductPrice;
import org.example.demo.Repository.OrderItemRepository;
import org.example.demo.Repository.OrderRepository;
import org.example.demo.Repository.ProductPriceRepository;
import org.example.demo.Repository.UserRepository;
import org.example.demo.Service.Interface.IOrderItemService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderItemServiceImplement implements IOrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductPriceRepository productPriceRepository;
    private final UserRepository userRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderItemDTO createOrderItem(OrderItemDTO dto) {
        log.info("Creating order item for orderId: {}", dto.getOrderId());

        Long currentUserId = getCurrentUserIdFromSecurityContext();
        Order order = getOrderIfPendingAndBelongsToUser(dto.getOrderId(), currentUserId);
        ProductPrice price = getProductPriceIfValid(dto.getProductPriceId(), order.getUser().getRank());

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(price.getProduct());
        item.setProductPrice(price);
        item.setQuantity(dto.getQuantity());
        item.setOriginalPrice(price.getPrice());

        item = orderItemRepository.save(item);
        updateOrderTotal(order);

        log.info("Order item created with ID: {}", item.getId());
        return orderItemMapper.toDTO(item);
    }

    @Override
    public OrderItemDTO updateOrderItem(Long id, OrderItemDTO dto) {
        log.info("Updating order item ID: {}", id);

        Long currentUserId = getCurrentUserIdFromSecurityContext();

        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Order item not found"));

        Order order = getOrderIfPendingAndBelongsToUser(item.getOrder().getId(), currentUserId);
        ProductPrice price = getProductPriceIfValid(dto.getProductPriceId(), order.getUser().getRank());

        item.setProductPrice(price);
        item.setProduct(price.getProduct());
        item.setQuantity(dto.getQuantity());
        item.setOriginalPrice(price.getPrice());

        item = orderItemRepository.save(item);
        updateOrderTotal(order);

        log.info("Order item updated: ID {}", id);
        return orderItemMapper.toDTO(item);
    }

    @Override
    public void deleteOrderItem(Long id) {
        log.info("Deleting order item ID: {}", id);

        Long currentUserId = getCurrentUserIdFromSecurityContext();

        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Order item not found"));

        Order order = getOrderIfPendingAndBelongsToUser(item.getOrder().getId(), currentUserId);
        orderItemRepository.deleteById(id);
        updateOrderTotal(order);

        log.info("Order item deleted: ID {}", id);
    }

    @Override
    public OrderItemDTO getOrderItem(Long id) {
        log.info("Retrieving order item ID: {}", id);
        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Order item not found"));
        return orderItemMapper.toDTO(item);
    }

    @Override
    public List<OrderItemDTO> getOrderItems(Long orderId) {
        log.info("Retrieving order items for orderId: {}", orderId);
        List<OrderItem> items = (orderId != null)
                ? orderItemRepository.findByOrderId(orderId)
                : orderItemRepository.findAll();
        return items.stream().map(orderItemMapper::toDTO).collect(Collectors.toList());
    }

    private Order getOrderIfPendingAndBelongsToUser(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new UserFriendlyException("Order not found"));

        if (!"pending_payment".equals(order.getStatus())) {
            throw new UserFriendlyException("Chỉ được sửa đơn ở trạng thái chờ thanh toán");
        }

        if (!order.getUser().getId().equals(userId)) {
            throw new UserFriendlyException("Bạn không có quyền sửa đơn hàng này");
        }

        return order;
    }

    private ProductPrice getProductPriceIfValid(Long priceId, String userRank) {
        ProductPrice price = productPriceRepository.findById(priceId)
                .orElseThrow(() -> new UserFriendlyException("Product price not found"));

        if (!price.getRank().equals(userRank)) {
            throw new UserFriendlyException("Invalid product price for user rank");
        }

        return price;
    }

    private void updateOrderTotal(Order order) {
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        BigDecimal totalAmount = items.stream()
                .map(item -> item.getOriginalPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);
        orderRepository.save(order);
        log.info("Updated total for order {}: {}", order.getId(), totalAmount);
    }

    private Long getCurrentUserIdFromSecurityContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                String username = userDetails.getUsername();
                return userRepository.findByUsername(username)
                        .orElseThrow(() -> new UserFriendlyException("User not found")).getId();
            } else if (principal instanceof String) {
                String username = (String) principal;
                return userRepository.findByUsername(username)
                        .orElseThrow(() -> new UserFriendlyException("User not found")).getId();
            }
        }
        throw new UserFriendlyException("User not authenticated");
    }
}
