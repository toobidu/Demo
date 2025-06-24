package org.example.demo.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Config.PageResponseDTO;
import org.example.demo.Config.PageUtil;
import org.example.demo.Modal.DTO.Orders.OrderItemDTO;
import org.example.demo.Service.Interface.IOrderItemService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final IOrderItemService orderItemService;

    @PostMapping
    @PreAuthorize("hasPermission(null, 'create_order_item')")
    public ResponseEntity<ApiResponse<OrderItemDTO>> createOrderItem(@Valid @RequestBody OrderItemDTO orderItemDTO) {
        OrderItemDTO created = orderItemService.createOrderItem(orderItemDTO);
        return ResponseEntity.ok(ApiResponse.success("Tạo order item thành công!", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'update_order_item')")
    public ResponseEntity<ApiResponse<OrderItemDTO>> updateOrderItem(@PathVariable Long id, @Valid @RequestBody OrderItemDTO orderItemDTO) {
        OrderItemDTO updated = orderItemService.updateOrderItem(id, orderItemDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật order item thành công!", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'delete_order_item')")
    public ResponseEntity<ApiResponse<Void>> deleteOrderItem(@PathVariable Long id) {
        orderItemService.deleteOrderItem(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa order item thành công!", null));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'view_order_item')")
    public ResponseEntity<ApiResponse<OrderItemDTO>> getOrderItem(@PathVariable Long id) {
        OrderItemDTO orderItem = orderItemService.getOrderItem(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra order item!", orderItem));
    }

    @GetMapping
    @PreAuthorize("hasPermission(null, 'view_order_items')")
    public ResponseEntity<ApiResponse<PageResponseDTO<OrderItemDTO>>> getOrderItems(
            @RequestParam(name = "orderId", required = false) Long orderId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Page<OrderItemDTO> orderItems = orderItemService.getOrderItems(orderId, page, size);
        PageResponseDTO<OrderItemDTO> response = new PageUtil().toPageResponse(orderItems);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra danh sách order item!", response));
    }
}