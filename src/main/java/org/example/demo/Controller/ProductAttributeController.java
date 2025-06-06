package org.example.demo.Controller;

import lombok.RequiredArgsConstructor;
import org.example.demo.Modal.DTO.ApiResponse;
import org.example.demo.Modal.DTO.ProductAttributeDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/products/{productId}/attributes")
@RequiredArgsConstructor
public class ProductAttributeController {
    private final IProductAttributeService attributeService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductAttributeDTO>> addProductAttribute(
            @PathVariable Long productId, @RequestBody ProductAttributeDTO attributeDTO) {
        attributeDTO.setProductId(productId);
        ProductAttributeDTO createdAttribute = attributeService.addProductAttribute(attributeDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Product attribute added successfully", createdAttribute));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductAttributeDTO>>> getProductAttributes(@PathVariable Long productId) {
        List<ProductAttributeDTO> attributes = attributeService.getProductAttributes(productId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product attributes retrieved successfully", attributes));
    }
    
    @GetMapping("/keys")
    public ResponseEntity<ApiResponse<Set<String>>> getProductAttributeKeys(@PathVariable Long productId) {
        Set<String> keys = attributeService.getProductAttributeKeys(productId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product attribute keys retrieved successfully", keys));
    }
    
    @GetMapping("/key/{key}")
    public ResponseEntity<ApiResponse<List<ProductAttributeDTO>>> getProductAttributesByKey(
            @PathVariable Long productId, @PathVariable String key) {
        List<ProductAttributeDTO> attributes = attributeService.getProductAttributesByKey(productId, key);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product attributes retrieved successfully", attributes));
    }
    
    @PutMapping("/{attributeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ProductAttributeDTO>> updateProductAttribute(
            @PathVariable Long productId, 
            @PathVariable Long attributeId, 
            @RequestBody ProductAttributeDTO attributeDTO) {
        attributeDTO.setProductId(productId);
        ProductAttributeDTO updatedAttribute = attributeService.updateProductAttribute(attributeId, attributeDTO);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product attribute updated successfully", updatedAttribute));
    }
    
    @DeleteMapping("/{attributeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteProductAttribute(
            @PathVariable Long productId, @PathVariable Long attributeId) {
        attributeService.deleteProductAttribute(attributeId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Product attribute deleted successfully", null));
    }
    
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteAllProductAttributes(@PathVariable Long productId) {
        attributeService.deleteProductAttributes(productId);
        return ResponseEntity.ok(new ApiResponse<>(true, "All product attributes deleted successfully", null));
    }
}