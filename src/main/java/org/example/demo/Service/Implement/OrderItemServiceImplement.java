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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderItemServiceImplement implements IOrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductPriceRepository productPriceRepository;
    private final OrderItemMapper orderItemMapper;

    @Override
    public OrderItemDTO createOrderItem(OrderItemDTO dto) {
        log.info("Creating order item for orderId: {}", dto.getOrderId());

        // 1. Lấy userId từ JWT (từ sub)
        Long currentUserId = getCurrentUserIdFromSecurityContext();

        // 2. Tìm đơn hàng
        Order order = getOrderById(dto.getOrderId());

        // 3. Kiểm tra giá sản phẩm (nếu có yêu cầu theo hạng người dùng)
        ProductPrice price = getProductPriceById(dto.getProductPriceId());
        if (!price.getRank().equals(order.getUser().getRank())) {
            throw new UserFriendlyException("Invalid product price for user rank");
        }

        // 4. Tạo mới OrderItem
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(price.getProduct());
        item.setProductPrice(price);
        item.setQuantity(dto.getQuantity());
        item.setOriginalPrice(price.getPrice());

        item = orderItemRepository.save(item);

        // 5. Cập nhật tổng tiền đơn hàng
        updateOrderTotal(order);

        log.info("Order item created with ID: {}", item.getId());
        return orderItemMapper.toDTO(item);
    }

    @Override
    public OrderItemDTO updateOrderItem(Long id, OrderItemDTO dto) {
        log.info("Updating order item ID: {}", id);

        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Order item not found"));

        ProductPrice price = getProductPriceById(dto.getProductPriceId());
        if (!price.getRank().equals(item.getOrder().getUser().getRank())) {
            throw new UserFriendlyException("Invalid product price for user rank");
        }

        item.setProductPrice(price);
        item.setProduct(price.getProduct());
        item.setQuantity(dto.getQuantity());
        item.setOriginalPrice(price.getPrice());

        item = orderItemRepository.save(item);
        updateOrderTotal(item.getOrder());

        log.info("Order item updated: ID {}", id);
        return orderItemMapper.toDTO(item);
    }

    @Override
    public void deleteOrderItem(Long id) {
        log.info("Deleting order item ID: {}", id);

        OrderItem item = orderItemRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Order item not found"));

        orderItemRepository.deleteById(id);
        updateOrderTotal(item.getOrder());

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
        return items.stream()
                .map(orderItemMapper::toDTO)
                .collect(Collectors.toList());
    }

    // --- Private methods ---

    private Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new UserFriendlyException("Order not found"));
    }

    private ProductPrice getProductPriceById(Long priceId) {
        return productPriceRepository.findById(priceId)
                .orElseThrow(() -> new UserFriendlyException("Product price not found"));
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

            // Trường hợp principal là UserDetails
            if (principal instanceof UserDetails userDetails) {
                String username = userDetails.getUsername(); // Đây chính là userId dạng chuỗi
                try {
                    return Long.parseLong(username); // Chuyển về Long
                } catch (NumberFormatException e) {
                    throw new UserFriendlyException("Invalid user ID in token");
                }
            }

            // Trường hợp principal là Map chứa claims (JWT)
            if (principal instanceof Map<?, ?> claims) {
                Object userIdObj = claims.get("sub"); // sub là userId
                if (userIdObj instanceof String str) {
                    try {
                        return Long.valueOf(str);
                    } catch (NumberFormatException e) {
                        throw new UserFriendlyException("Invalid user ID in token");
                    }
                } else if (userIdObj instanceof Number number) {
                    return number.longValue();
                }
                throw new UserFriendlyException("User ID not found in token");
            }

            // Trường hợp principal là String -> userId dưới dạng chuỗi (trong JWT)
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
