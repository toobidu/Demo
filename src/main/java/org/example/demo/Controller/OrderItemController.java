package org.example.demo.Controller;

import lombok.RequiredArgsConstructor;
import org.example.demo.Modal.DTO.ApiResponse;
import org.example.demo.Modal.DTO.OrderItemDTO;
import org.example.demo.Service.Interface.IOrderItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders/{orderId}/items")
@RequiredArgsConstructor
public class OrderItemController {
    private final IOrderItemService orderItemService;
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALE')")
    public ResponseEntity<ApiResponse<OrderItemDTO>> addOrderItem(
            @PathVariable Long orderId, @RequestBody OrderItemDTO orderItemDTO) {
        orderItemDTO.setOrderId(orderId);
        OrderItemDTO createdItem = orderItemService.addOrderItem(orderItemDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Order item added successfully", createdItem));
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALE', 'PRINT_HOUSE')")
    public ResponseEntity<ApiResponse<List<OrderItemDTO>>> getOrderItems(@PathVariable Long orderId) {
        List<OrderItemDTO> items = orderItemService.getOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order items retrieved successfully", items));
    }
    
    @GetMapping("/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALE', 'PRINT_HOUSE')")
    public ResponseEntity<ApiResponse<OrderItemDTO>> getOrderItem(
            @PathVariable Long orderId, @PathVariable Long itemId) {
        OrderItemDTO item = orderItemService.getOrderItemById(itemId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order item retrieved successfully", item));
    }
    
    @PutMapping("/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALE')")
    public ResponseEntity<ApiResponse<OrderItemDTO>> updateOrderItem(
            @PathVariable Long orderId, 
            @PathVariable Long itemId, 
            @RequestBody OrderItemDTO orderItemDTO) {
        orderItemDTO.setOrderId(orderId);
        OrderItemDTO updatedItem = orderItemService.updateOrderItem(itemId, orderItemDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order item updated successfully", updatedItem));
    }
    
    @DeleteMapping("/{itemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SALE')")
    public ResponseEntity<ApiResponse<Void>> deleteOrderItem(
            @PathVariable Long orderId, @PathVariable Long itemId) {
        orderItemService.deleteOrderItem(itemId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Order item deleted successfully", null));
    }
    
    @DeleteMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SALE')")
    public ResponseEntity<ApiResponse<Void>> deleteAllOrderItems(@PathVariable Long orderId) {
        orderItemService.deleteOrderItemsByOrderId(orderId);
        return ResponseEntity.ok(new ApiResponse<>(true, "All order items deleted successfully", null));
    }
}