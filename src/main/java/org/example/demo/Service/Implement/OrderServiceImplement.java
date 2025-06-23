package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.OrderMapper;
import org.example.demo.Modal.DTO.Orders.OrderDTO;
import org.example.demo.Modal.DTO.Orders.OrderItemDTO;
import org.example.demo.Modal.Entity.Finance.Wallet;
import org.example.demo.Modal.Entity.Orders.Order;
import org.example.demo.Modal.Entity.Orders.OrderItem;
import org.example.demo.Modal.Entity.Products.ProductPrice;
import org.example.demo.Modal.Entity.Users.User;
import org.example.demo.Repository.*;
import org.example.demo.Service.Interface.IOrderService;
import org.example.demo.Service.Interface.IWalletService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private final IWalletService walletService;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        log.info("Creating order for userId: {}", orderDTO.getUserId());
        User user = validateUser(orderDTO.getUserId());
        Wallet wallet = validateWallet(orderDTO.getUserId());

        Order order = initNewOrder(user, wallet);
        BigDecimal totalAmount = calculateAndSaveOrderItems(order, orderDTO.getOrderItems(), user);
        order.setTotalAmount(totalAmount);
        orderRepository.save(order);

        validateWalletBalance(wallet, totalAmount, order);
        return toOrderDTOWithItems(order);
    }

    @Override
    @Transactional
    public OrderDTO updateOrder(Long id, OrderDTO orderDTO) {
        log.info("Updating order ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Order not found"));

        String currentStatus = order.getStatus();
        String newStatus = orderDTO.getStatus();

        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new UserFriendlyException("Invalid status transition");
        }

        // Xử lý tài chính theo trạng thái mới
        if ("order".equals(newStatus)) {
            deductBalanceFromSale(order); // Trừ tiền của Sale
            creditAdmin(order);           // Cộng tiền cho Admin
        } else if ("shipping".equals(newStatus)) {
            creditPrinthouse(order);      // Cộng tiền cho Nhà in
        }

        // Cập nhật thông tin đơn hàng
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        if (orderDTO.getPrintPrice() != null) order.setPrintPrice(orderDTO.getPrintPrice());
        if (orderDTO.getShipPrice() != null) order.setShipPrice(orderDTO.getShipPrice());
        if (orderDTO.getPreShipPrice() != null) order.setPreShipPrice(orderDTO.getPreShipPrice());

        order = orderRepository.save(order);
        return toOrderDTOWithItems(order);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long id, String newStatus) {
        log.info("Updating status of order ID {}: {}", id, newStatus);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Order not found"));

        String currentStatus = order.getStatus();

        if (!isValidStatusTransition(currentStatus, newStatus)) {
            throw new UserFriendlyException("Invalid status transition");
        }

        if ("order".equals(newStatus)) {
            deductBalanceFromSale(order); // Trừ tiền của Sale
            creditAdmin(order);           // Cộng tiền cho Admin
        } else if ("shipping".equals(newStatus)) {
            creditPrinthouse(order);      // Cộng tiền cho Nhà in
        }

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        order = orderRepository.save(order);

        return toOrderDTOWithItems(order);
    }

    @Override
    @Transactional
    public void cancelOrder(Long id) {
        log.info("Cancelling order ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Order not found"));

        if (!"pending_payment".equals(order.getStatus()) && !"order".equals(order.getStatus())) {
            throw new UserFriendlyException("Chỉ được hủy đơn ở trạng thái chờ thanh toán hoặc order");
        }

        // Hoàn tiền nếu đã thanh toán
        if ("order".equals(order.getStatus())) {
            walletService.refundOnCancel(order.getUser().getId(), order.getTotalAmount());
        }

        order.setStatus("cancelled");
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    @Override
    public OrderDTO getOrderById(Long id) {
        log.info("Retrieving order ID: {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Order not found"));
        return toOrderDTOWithItems(order);
    }

    @Override
    public List<OrderDTO> getOrdersForUser(Long userId) {
        log.info("Retrieving orders for userId: {}", userId);
        return orderRepository.findByUserId(userId)
                .stream().map(this::toOrderDTOWithItems).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersForAdmin() {
        log.info("Retrieving orders for admin");
        return orderRepository.findAllOrdersForAdmin()
                .stream().map(this::toOrderDTOWithItems).collect(Collectors.toList());
    }

    @Override
    public List<OrderDTO> getOrdersForPrintHouse() {
        log.info("Retrieving orders for print house");
        return orderRepository.findOrdersForPrintHouse()
                .stream().map(this::toOrderDTOWithItems).collect(Collectors.toList());
    }

    //Các hàm xử lý tài chính
    private void deductBalanceFromSale(Order order) {
        Wallet wallet = order.getWallet();
        BigDecimal amount = order.getTotalAmount();

        if (wallet == null || wallet.getBalance().compareTo(amount) < 0) {
            throw new UserFriendlyException("Số dư không đủ để thực hiện giao dịch");
        }

        walletService.deductBalance(wallet.getUser().getId(), amount);
    }

    private void creditAdmin(Order order) {
        BigDecimal amount = order.getTotalAmount();
        walletService.creditAdmin(amount);
    }

    private void creditPrinthouse(Order order) {
        BigDecimal amount = order.getPrintPrice();
        walletService.creditPrinthouse(order.getUser().getId(), amount);
    }

    // --- Các hàm hỗ trợ ---

    private User validateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserFriendlyException("User not found"));
        /*if (!"sale".equals(user.getTypeAccount())) {
            throw new UserFriendlyException("Only sale users can create orders");
        }*/
        return user;
    }

    private Wallet validateWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new UserFriendlyException("Wallet not found"));
    }

    private Order initNewOrder(User user, Wallet wallet) {
        Order order = new Order();
        order.setUser(user);
        order.setWallet(wallet);
        order.setStatus("pending_payment");
        order.setTotalAmount(BigDecimal.ZERO);
        order.setPrintPrice(BigDecimal.ZERO);
        order.setShipPrice(BigDecimal.ZERO);
        order.setPreShipPrice(BigDecimal.ZERO);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }

    private BigDecimal calculateAndSaveOrderItems(Order order, List<OrderItemDTO> items, User user) {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemDTO dto : items) {
            ProductPrice price = productPriceRepository.findById(dto.getProductPriceId())
                    .orElseThrow(() -> new UserFriendlyException("Product price not found"));

            if (!price.getRank().equals(user.getRank())) {
                throw new UserFriendlyException("Invalid product price for user rank");
            }

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(price.getProduct());
            item.setProductPrice(price);
            item.setQuantity(dto.getQuantity());
            item.setOriginalPrice(price.getPrice());
            orderItemRepository.save(item);

            total = total.add(price.getPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        }
        return total;
    }

    private void validateWalletBalance(Wallet wallet, BigDecimal required, Order order) {
        if (wallet.getBalance().compareTo(required) < 0) {
            order.setStatus("cancelled");
            orderRepository.save(order);
            throw new UserFriendlyException("Số dư không đủ để tạo đơn");
        }
    }

    private OrderDTO toOrderDTOWithItems(Order order) {
        OrderDTO dto = orderMapper.toDTO(order);
        dto.setOrderItems(mapOrderItems(orderItemRepository.findByOrderId(order.getId())));
        return dto;
    }

    private List<OrderItemDTO> mapOrderItems(List<OrderItem> items) {
        return items.stream().map(item -> {
            OrderItemDTO dto = new OrderItemDTO();
            dto.setId(item.getId());
            dto.setOrderId(item.getOrder().getId());
            dto.setProductId(item.getProduct().getId());
            dto.setProductPriceId(item.getProductPrice().getId());
            dto.setQuantity(item.getQuantity());
            dto.setOriginalPrice(item.getOriginalPrice());
            return dto;
        }).collect(Collectors.toList());
    }

    private boolean isValidStatusTransition(String current, String next) {
        switch (current) {
            case "pending_payment": return next.equals("order") || next.equals("cancelled");
            case "order": return next.equals("processing") || next.equals("cancelled");
            case "processing": return next.equals("shipping");
            case "shipping": return next.equals("done");
            default: return false;
        }
    }
}
