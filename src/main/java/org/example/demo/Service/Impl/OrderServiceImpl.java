package org.example.demo.Service.Impl;

import lombok.RequiredArgsConstructor;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.OrderMapper;
import org.example.demo.Modal.DTO.OrderDTO;
import org.example.demo.Modal.Entity.Order;
import org.example.demo.Modal.Entity.Transaction;
import org.example.demo.Modal.Entity.User;
import org.example.demo.Modal.Entity.Wallet;
import org.example.demo.Repository.OrderRepository;
import org.example.demo.Repository.TransactionRepository;
import org.example.demo.Repository.UserRepository;
import org.example.demo.Repository.WalletRepository;
import org.example.demo.Service.Interface.IOrderService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final OrderMapper orderMapper;
    private final UserDetailsService userDetailsService;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO, String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UserFriendlyException("User not found"));
        
        // Set initial status to pending_payment
        Order order = orderMapper.toEntity(orderDTO);
        order.setOrderStatus("pending_payment");
        order.setUser(user);
        
        // Validate wallet belongs to user
        Wallet wallet = walletRepository.findById(orderDTO.getWallet().getId())
                .orElseThrow(() -> new UserFriendlyException("Wallet not found"));
        
        if (!wallet.getUser().getId().equals(user.getId())) {
            throw new UserFriendlyException("Wallet does not belong to user");
        }
        
        order.setWallet(wallet);
        
        Order savedOrder = orderRepository.save(order);
        return orderMapper.toDTO(savedOrder);
    }

    @Override
    public OrderDTO getOrderById(Long id, String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Order not found"));
        
        // Check permissions
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            // Admin can view all non-cancelled orders
            if (order.getOrderStatus().equals("cancelled")) {
                throw new UserFriendlyException("Order not found");
            }
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SALE"))) {
            // Sales can only view their own orders
            User user = userRepository.findByUserName(username)
                    .orElseThrow(() -> new UserFriendlyException("User not found"));
            
            if (!order.getUser().getId().equals(user.getId())) {
                throw new UserFriendlyException("Order not found");
            }
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PRINT_HOUSE"))) {
            // Print house can only view orders with specific statuses
            List<String> allowedStatuses = Arrays.asList("order", "processing", "shipping", "done");
            if (!allowedStatuses.contains(order.getOrderStatus())) {
                throw new UserFriendlyException("Order not found");
            }
        }
        
        return orderMapper.toDTO(order);
    }

    @Override
    public List<OrderDTO> getAllOrders(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        List<Order> orders;
        
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            // Admin can view all non-cancelled orders
            orders = orderRepository.findAllNonCancelled();
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SALE"))) {
            // Sales can only view their own orders
            User user = userRepository.findByUserName(username)
                    .orElseThrow(() -> new UserFriendlyException("User not found"));
            orders = orderRepository.findByUser(user);
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PRINT_HOUSE"))) {
            // Print house can only view orders with specific statuses
            List<String> allowedStatuses = Arrays.asList("order", "processing", "shipping", "done");
            orders = orderRepository.findByOrderStatusIn(allowedStatuses);
        } else {
            throw new UserFriendlyException("Unauthorized");
        }
        
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long id, String status, String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Order not found"));
        
        // Validate status transition
        validateStatusTransition(order.getOrderStatus(), status);
        
        // Check permissions
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SALE"))) {
            User user = userRepository.findByUserName(username)
                    .orElseThrow(() -> new UserFriendlyException("User not found"));
            
            if (!order.getUser().getId().equals(user.getId())) {
                throw new UserFriendlyException("Unauthorized");
            }
        } else if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new UserFriendlyException("Unauthorized");
        }
        
        // Handle financial transactions based on status change
        handleFinancialTransactions(order, status);
        
        order.setOrderStatus(status);
        Order updatedOrder = orderRepository.save(order);
        return orderMapper.toDTO(updatedOrder);
    }

    @Override
    @Transactional
    public void cancelOrder(Long id, String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Order not found"));
        
        // Only pending_payment or order status can be cancelled
        if (!order.getOrderStatus().equals("pending_payment") && !order.getOrderStatus().equals("order")) {
            throw new UserFriendlyException("Order cannot be cancelled in current status");
        }
        
        // Check permissions
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SALE"))) {
            User user = userRepository.findByUserName(username)
                    .orElseThrow(() -> new UserFriendlyException("User not found"));
            
            if (!order.getUser().getId().equals(user.getId())) {
                throw new UserFriendlyException("Unauthorized");
            }
        } else if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new UserFriendlyException("Unauthorized");
        }
        
        // If order status is "order", refund the money
        if (order.getOrderStatus().equals("order")) {
            refundOrderPayment(order);
        }
        
        order.setOrderStatus("cancelled");
        orderRepository.save(order);
    }

    @Override
    public List<OrderDTO> getOrdersByStatus(List<String> statuses, String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        List<Order> orders;
        
        if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            // Admin can view all orders with specified statuses
            orders = orderRepository.findByOrderStatusIn(statuses);
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_SALE"))) {
            // Sales can only view their own orders
            User user = userRepository.findByUserName(username)
                    .orElseThrow(() -> new UserFriendlyException("User not found"));
            
            orders = orderRepository.findByUser(user).stream()
                    .filter(order -> statuses.contains(order.getOrderStatus()))
                    .collect(Collectors.toList());
        } else if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_PRINT_HOUSE"))) {
            // Print house can only view orders with specific statuses
            List<String> allowedStatuses = Arrays.asList("order", "processing", "shipping", "done");
            statuses.retainAll(allowedStatuses);
            orders = orderRepository.findByOrderStatusIn(statuses);
        } else {
            throw new UserFriendlyException("Unauthorized");
        }
        
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    private void validateStatusTransition(String currentStatus, String newStatus) {
        // Implement status transition validation logic
        if (currentStatus.equals("done") || currentStatus.equals("cancelled")) {
            throw new UserFriendlyException("Cannot change status of completed or cancelled orders");
        }
        
        // Add more validation rules as needed
        if (currentStatus.equals("pending_payment") && !newStatus.equals("order") && !newStatus.equals("cancelled")) {
            throw new UserFriendlyException("Invalid status transition");
        }
        
        if (currentStatus.equals("order") && !newStatus.equals("processing") && !newStatus.equals("cancelled")) {
            throw new UserFriendlyException("Invalid status transition");
        }
        
        if (currentStatus.equals("processing") && !newStatus.equals("shipping")) {
            throw new UserFriendlyException("Invalid status transition");
        }
        
        if (currentStatus.equals("shipping") && !newStatus.equals("done")) {
            throw new UserFriendlyException("Invalid status transition");
        }
    }

    private void handleFinancialTransactions(Order order, String newStatus) {
        // Handle financial transactions based on status change
        if (order.getOrderStatus().equals("pending_payment") && newStatus.equals("order")) {
            // Deduct money from sales wallet and add to admin wallet
            processOrderPayment(order);
        } else if (order.getOrderStatus().equals("processing") && newStatus.equals("shipping")) {
            // Transfer money to print house wallet
            processPrintHousePayment(order);
        }
    }

    private void processOrderPayment(Order order) {
        Wallet salesWallet = order.getWallet();

        // Tìm ví của admin
        Wallet adminWallet = walletRepository.findByUserRoleName("admin")
                .orElseThrow(() -> new UserFriendlyException("Admin wallet not found"));

        BigDecimal amount = order.getTotalAmount();

        // Kiểm tra số dư
        if (salesWallet.getBalance().compareTo(amount) < 0) {
            throw new UserFriendlyException("Insufficient balance");
        }

        // Tạo giao dịch
        Transaction transaction = new Transaction();
        transaction.setFromWallet(salesWallet);
        transaction.setToWallet(adminWallet);
        transaction.setAmount(amount);
        transaction.setTransactionType("order_payment");

        transactionRepository.save(transaction);
    }

    private void processPrintHousePayment(Order order) {
        // Tìm ví của admin
        Wallet adminWallet = walletRepository.findByUserRoleName("admin")
                .orElseThrow(() -> new UserFriendlyException("Admin wallet not found"));

        // Tìm ví của nhà in
        Wallet printHouseWallet = walletRepository.findByUserRoleName("print_house")
                .orElseThrow(() -> new UserFriendlyException("Print house wallet not found"));

        BigDecimal amount = order.getTotalAmount();

        // Tạo giao dịch
        Transaction transaction = new Transaction();
        transaction.setFromWallet(adminWallet);
        transaction.setToWallet(printHouseWallet);
        transaction.setAmount(amount);
        transaction.setTransactionType("payment_to_print_house");

        transactionRepository.save(transaction);
    }

    private void refundOrderPayment(Order order) {
        // Tìm ví của admin
        Wallet adminWallet = walletRepository.findByUserRoleName("admin")
                .orElseThrow(() -> new UserFriendlyException("Admin wallet not found"));

        Wallet salesWallet = order.getWallet();
        BigDecimal amount = order.getTotalAmount();

        // Tạo giao dịch hoàn tiền
        Transaction transaction = new Transaction();
        transaction.setFromWallet(adminWallet);
        transaction.setToWallet(salesWallet);
        transaction.setAmount(amount);
        transaction.setTransactionType("order_refund");

        transactionRepository.save(transaction);
    }
}