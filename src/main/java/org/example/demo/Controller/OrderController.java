package org.example.demo.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Config.PageResponseDTO;
import org.example.demo.Config.PageUtil;
import org.example.demo.Modal.DTO.Orders.OrderDTO;
import org.example.demo.Service.Interface.IOrderService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    @PostMapping
    @PreAuthorize("hasPermission(null, 'create_order')")
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(ApiResponse.success("Đơn hàng đã được tạo thành công!", orderService.createOrder(orderDTO)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'update_order')")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(ApiResponse.success("Đơn hàng đã được cập nhật thành công!", orderService.updateOrder(id, orderDTO)));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasPermission(null, 'change_order_status')")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái thành công!",
                orderService.updateOrderStatus(id, status)));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasPermission(null, 'cancel_order')")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Đơn hàng đã được hủy thành công!", null));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'view_order')")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Truy vấn tới đơn hàng thành công!", orderService.getOrderById(id)));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasPermission(null, 'view_user_orders')")
    public ResponseEntity<ApiResponse<PageResponseDTO<OrderDTO>>> getOrdersForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<OrderDTO> ordersPage = orderService.getOrdersForUser(userId, page, size);
        PageResponseDTO<OrderDTO> response = new PageUtil().toPageResponse(ordersPage);
        return ResponseEntity.ok(ApiResponse.success("Đơn hàng đã được truy vấn thành công tới người dùng!", response));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasPermission(null, 'view_admin_orders')")
    public ResponseEntity<ApiResponse<PageResponseDTO<OrderDTO>>> getOrdersForAdmin(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<OrderDTO> ordersPage = orderService.getOrdersForAdmin(page, size);
        PageResponseDTO<OrderDTO> response = new PageUtil().toPageResponse(ordersPage);
        return ResponseEntity.ok(ApiResponse.success("Đơn hàng đã được truy vấn thành công tới admin!", response));
    }

    @GetMapping("/print-house")
    @PreAuthorize("hasPermission(null, 'view_printhouse_orders')")
    public ResponseEntity<ApiResponse<PageResponseDTO<OrderDTO>>> getOrdersForPrintHouse(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<OrderDTO> ordersPage = orderService.getOrdersForPrintHouse(page, size);
        PageResponseDTO<OrderDTO> response = new PageUtil().toPageResponse(ordersPage);
        return ResponseEntity.ok(ApiResponse.success("Đơn hàng đã được truy vấn thành công tới nhà in!", response));
    }
}