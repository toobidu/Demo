package org.example.demo.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Modal.DTO.Products.ProductAttributeDTO;
import org.example.demo.Service.Interface.IProductAttributeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-attributes")
@RequiredArgsConstructor
public class ProductAttributeController {

    private final IProductAttributeService productAttributeService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductAttributeDTO>> createProductAttribute(@Valid @RequestBody ProductAttributeDTO attributeDTO) {
        ProductAttributeDTO created = productAttributeService.createProductAttribute(attributeDTO);
        return ResponseEntity.ok(ApiResponse.success("Tạo product attribute thành công!", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductAttributeDTO>> updateProductAttribute(@PathVariable Long id, @Valid @RequestBody ProductAttributeDTO attributeDTO) {
        ProductAttributeDTO updated = productAttributeService.updateProductAttribute(id, attributeDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật product attribute thông!", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductAttribute(@PathVariable Long id) {
        productAttributeService.deleteProductAttribute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductAttributeDTO>> getProductAttribute(@PathVariable Long id) {
        ProductAttributeDTO attributeDTO = productAttributeService.getProductAttribute(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra product attribute!", attributeDTO));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductAttributeDTO>>> getProductAttributes(
            @RequestParam(name = "productId", required = false) Long productId) {
        List<ProductAttributeDTO> attributes = productAttributeService.getProductAttributes(productId);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra danh sách product attribute!", attributes));
    }
}
