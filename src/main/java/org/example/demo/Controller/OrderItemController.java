package org.example.demo.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Modal.DTO.Orders.OrderItemDTO;
import org.example.demo.Service.Interface.IOrderItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final IOrderItemService orderItemService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrderItemDTO>> createOrderItem(@Valid @RequestBody OrderItemDTO orderItemDTO) {
        OrderItemDTO created = orderItemService.createOrderItem(orderItemDTO);
        return ResponseEntity.ok(ApiResponse.success("Tạo order item thành công!", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderItemDTO>> updateOrderItem(@PathVariable Long id, @Valid @RequestBody OrderItemDTO orderItemDTO) {
        OrderItemDTO updated = orderItemService.updateOrderItem(id, orderItemDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật order item thông!", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa order item thông!", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderItemDTO>> getOrderItem(@PathVariable Long id) {
        OrderItemDTO orderItem = orderItemService.getOrderItem(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra order item!", orderItem));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderItemDTO>>> getOrderItems(
            @RequestParam(name = "orderId", required = false) Long orderId) {
        List<OrderItemDTO> orderItems = orderItemService.getOrderItems(orderId);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra danh sách order item!", orderItems));
    }
}
