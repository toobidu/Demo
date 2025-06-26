package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.OrderItemMapper;
import org.example.demo.Modal.DTO.Orders.OrderItemDTO;
import org.example.demo.Modal.Entity.Orders.Order;
import org.example.demo.Modal.Entity.Orders.OrderItem;
import org.example.demo.Modal.Entity.Products.ProductPrice;
import org.example.demo.Modal.Entity.Users.User;
import org.example.demo.Repository.OrderItemRepository;
import org.example.demo.Repository.OrderRepository;
import org.example.demo.Repository.ProductPriceRepository;
import org.example.demo.Repository.UserRepository;
import org.example.demo.Service.Interface.IOrderItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderItemServiceImplement implements IOrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductPriceRepository productPriceRepository;
    private final OrderItemMapper orderItemMapper;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrderItemDTO createOrderItem(OrderItemDTO dto) {
        log.info("Creating order item for orderId: {}", dto.getOrderId());

        Long currentUserId = getCurrentUserIdFromSecurityContext();
        Order order = getOrderById(dto.getOrderId());

        // Verify order belongs to current user
        if (!order.getUserId().equals(currentUserId)) {
            throw new UserFriendlyException("Order does not belong to current user");
        }

        // Verify order status
        if (!"pending_payment".equals(order.getStatus())) {
            throw new UserFriendlyException("Can only modify order in pending payment status");
        }

        ProductPrice price = getProductPriceById(dto.getProductPriceId());
        String userRank = getUserRank(currentUserId);
        if (!price.getRank().equals(userRank)) {
            throw new UserFriendlyException("Invalid product price for user rank");
        }

        OrderItem item = new OrderItem();
        item.setOrderId(order.getId());
        item.setProductId(price.getProductId());
        item.setProductPriceId(price.getId());
        item.setQuantity(dto.getQuantity());
        item.setOriginalPrice(price.getPrice());

        item = orderItemRepository.save(item);
        updateOrderTotal(order);

        log.info("Order item created with ID: {}", item.getId());
        return orderItemMapper.toDTO(item);
    }

    @Override
    @Transactional
    public OrderItemDTO updateOrderItem(Long id, OrderItemDTO dto) {
        log.info("Updating order item ID: {}", id);

        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Order item not found"));

        Order order = getOrderById(item.getOrderId());
        Long currentUserId = getCurrentUserIdFromSecurityContext();

        if (!order.getUserId().equals(currentUserId)) {
            throw new UserFriendlyException("Order does not belong to current user");
        }

        if (!"pending_payment".equals(order.getStatus())) {
            throw new UserFriendlyException("Can only modify order in pending payment status");
        }

        ProductPrice price = getProductPriceById(dto.getProductPriceId());
        String userRank = getUserRank(currentUserId);
        if (!price.getRank().equals(userRank)) {
            throw new UserFriendlyException("Invalid product price for user rank");
        }

        item.setProductPriceId(price.getId());
        item.setProductId(price.getProductId());
        item.setQuantity(dto.getQuantity());
        item.setOriginalPrice(price.getPrice());

        item = orderItemRepository.save(item);
        updateOrderTotal(order);

        log.info("Order item updated: ID {}", id);
        return orderItemMapper.toDTO(item);
    }

    @Override
    @Transactional
    public void deleteOrderItem(Long id) {
        log.info("Deleting order item ID: {}", id);

        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Order item not found"));

        Order order = getOrderById(item.getOrderId());
        Long currentUserId = getCurrentUserIdFromSecurityContext();

        if (!order.getUserId().equals(currentUserId)) {
            throw new UserFriendlyException("Order does not belong to current user");
        }

        if (!"pending_payment".equals(order.getStatus())) {
            throw new UserFriendlyException("Can only modify order in pending payment status");
        }

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
    public Page<OrderItemDTO> getOrderItems(Long orderId, int page, int size) {
        log.info("Retrieving order items for orderId: {} with paging", orderId);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<OrderItem> items = (orderId != null)
                ? orderItemRepository.findByOrderId(orderId, pageable)
                : orderItemRepository.findAll(pageable);
        return items.map(orderItemMapper::toDTO);
    }

    private Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new UserFriendlyException("Order not found"));
    }

    private ProductPrice getProductPriceById(Long priceId) {
        return productPriceRepository.findById(priceId)
                .orElseThrow(() -> new UserFriendlyException("Product price not found"));
    }

    private String getUserRank(Long userId) {
        return userRepository.findById(userId)
                .map(User::getRank)
                .orElseThrow(() -> new UserFriendlyException("User not found"));
    }

    private void updateOrderTotal(Order order) {
        Page<OrderItem> itemsPage = orderItemRepository.findByOrderId(order.getId(), PageRequest.of(0, Integer.MAX_VALUE));
        BigDecimal totalAmount = itemsPage.getContent().stream()
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
                try {
                    return Long.parseLong(userDetails.getUsername());
                } catch (NumberFormatException e) {
                    throw new UserFriendlyException("Invalid user ID in token");
                }
            }

            if (principal instanceof Map<?, ?> claims) {
                Object userIdObj = claims.get("sub");
                if (userIdObj instanceof String str) {
                    try {
                        return Long.valueOf(str);
                    } catch (NumberFormatException e) {
                        throw new UserFriendlyException("Invalid user ID in token");
                    }
                } else if (userIdObj instanceof Number number) {
                    return number.longValue();
                }
            }

            if (principal instanceof String str) {
                try {
                    return Long.valueOf(str);
                } catch (NumberFormatException e) {
                    throw new UserFriendlyException("Invalid user ID in token");
                }
            }
        }

        throw new UserFriendlyException("User not authenticated");
    }
}
