package org.example.demo.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Modal.DTO.Orders.OrderDTO;
import org.example.demo.Service.Interface.IOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final IOrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderDTO>> createOrder(@Valid @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(ApiResponse.success("Đơn hàng đã được tạo thành công!", orderService.createOrder(orderDTO)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.ok(ApiResponse.success("Đơn hàng đã được cập nhật thành công!", orderService.updateOrder(id, orderDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.ok(ApiResponse.success("Đơn hàng đã được hủy thành công!", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDTO>> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Truy vấn tới đơn hàng thành công!", orderService.getOrderById(id)));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getOrdersForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Đơn hàng đã được truy vấn thành công tới người dùng!", orderService.getOrdersForUser(userId)));
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getOrdersForAdmin() {
        return ResponseEntity.ok(ApiResponse.success("Đơn hàng đã được truy vấn thành công tới admin!", orderService.getOrdersForAdmin()));
    }

    @GetMapping("/print-house")
    public ResponseEntity<ApiResponse<List<OrderDTO>>> getOrdersForPrintHouse() {
        return ResponseEntity.ok(ApiResponse.success("Đơn hàng đã được truy vấn thành công tới nhà in!", orderService.getOrdersForPrintHouse()));
    }
}
