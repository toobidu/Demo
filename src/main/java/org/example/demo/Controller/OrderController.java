package org.example.demo.Controller;

import lombok.RequiredArgsConstructor;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Modal.DTO.OrderDTO;
import org.example.demo.Service.Interface.IOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService IOrderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(
            @RequestBody OrderDTO orderDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        OrderDTO createdOrder = IOrderService.createOrder(orderDTO, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order được tạo thành công!", createdOrder));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        OrderDTO order = IOrderService.getOrderById(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Order được lấy ra theo id thành công!", order));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getAllOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<OrderDTO> orders = IOrderService.getAllOrders(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Lấy ra tất cả order thành công!", orders));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @AuthenticationPrincipal UserDetails userDetails) {
        OrderDTO updatedOrder = IOrderService.updateOrderStatus(id, status, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái của order thành công!", updatedOrder));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        IOrderService.cancelOrder(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Order được hủy thành công!", null));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getOrdersByStatus(
            @RequestParam List<String> statuses,
            @AuthenticationPrincipal UserDetails userDetails) {
        List<OrderDTO> orders = IOrderService.getOrdersByStatus(statuses, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Lấy ra thành công!", orders));
    }
}