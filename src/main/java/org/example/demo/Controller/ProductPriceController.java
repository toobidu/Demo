package org.example.demo.Controller;

import lombok.RequiredArgsConstructor;
import org.example.demo.Modal.DTO.ApiResponse;
import org.example.demo.Modal.DTO.ProductPriceDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/prices")
@RequiredArgsConstructor
public class ProductPriceController {
    private final IProductPriceService priceService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductPriceDTO>> addProductPrice(
            @PathVariable Long productId, @RequestBody ProductPriceDTO priceDTO) {
        priceDTO.setProductId(productId);
        ProductPriceDTO createdPrice = priceService.addProductPrice(priceDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Product price added successfully", createdPrice));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductPriceDTO>>> getProductPrices(@PathVariable Long productId) {
        List<ProductPriceDTO> prices = priceService.getProductPrices(productId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product prices retrieved successfully", prices));
    }
    
    @GetMapping("/{priceId}")
    public ResponseEntity<ApiResponse<ProductPriceDTO>> getProductPrice(
            @PathVariable Long productId, @PathVariable Long priceId) {
        ProductPriceDTO price = priceService.getProductPrice(priceId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product price retrieved successfully", price));
    }
    
    @GetMapping("/calculate")
    public ResponseEntity<ApiResponse<BigDecimal>> calculatePrice(
            @PathVariable Long productId, 
            @RequestParam String rank, 
            @RequestParam String size) {
        BigDecimal price = priceService.calculatePrice(productId, rank, size);
        return ResponseEntity.ok(new ApiResponse<>(true, "Price calculated successfully", price));
    }
    
    @PutMapping("/{priceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductPriceDTO>> updateProductPrice(
            @PathVariable Long productId, 
            @PathVariable Long priceId, 
            @RequestBody ProductPriceDTO priceDTO) {
        priceDTO.setProductId(productId);
        ProductPriceDTO updatedPrice = priceService.updateProductPrice(priceId, priceDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product price updated successfully", updatedPrice));
    }
    
    @DeleteMapping("/{priceId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProductPrice(
            @PathVariable Long productId, @PathVariable Long priceId) {
        priceService.deleteProductPrice(priceId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product price deleted successfully", null));
    }
    
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAllProductPrices(@PathVariable Long productId) {
        priceService.deleteProductPrices(productId);
        return ResponseEntity.ok(new ApiResponse<>(true, "All product prices deleted successfully", null));
    }
}