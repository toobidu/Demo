package org.example.demo.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Config.PageResponseDTO;
import org.example.demo.Config.PageUtil;
import org.example.demo.Modal.DTO.Products.ProductAttributeDTO;
import org.example.demo.Service.Interface.IProductAttributeService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product-attributes")
@RequiredArgsConstructor
public class ProductAttributeController {

    private final IProductAttributeService productAttributeService;

    @PostMapping
    @PreAuthorize("hasPermission(null, 'create_product_attribute')")
    public ResponseEntity<ApiResponse<ProductAttributeDTO>> createProductAttribute(@Valid @RequestBody ProductAttributeDTO attributeDTO) {
        ProductAttributeDTO created = productAttributeService.createProductAttribute(attributeDTO);
        return ResponseEntity.ok(ApiResponse.success("Tạo product attribute thành công!", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'update_product_attribute')")
    public ResponseEntity<ApiResponse<ProductAttributeDTO>> updateProductAttribute(@PathVariable Long id, @Valid @RequestBody ProductAttributeDTO attributeDTO) {
        ProductAttributeDTO updated = productAttributeService.updateProductAttribute(id, attributeDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật product attribute thành công!", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'delete_product_attribute')")
    public ResponseEntity<ApiResponse<Void>> deleteProductAttribute(@PathVariable Long id) {
        productAttributeService.deleteProductAttribute(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa product attribute thành công!", null));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'view_product_attribute')")
    public ResponseEntity<ApiResponse<ProductAttributeDTO>> getProductAttribute(@PathVariable Long id) {
        ProductAttributeDTO attribute = productAttributeService.getProductAttribute(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra product attribute!", attribute));
    }

    @GetMapping
    @PreAuthorize("hasPermission(null, 'view_product_attributes')")
    public ResponseEntity<ApiResponse<PageResponseDTO<ProductAttributeDTO>>> getAllProductAttributes(
            @RequestParam(name = "productId", required = false) Long productId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Page<ProductAttributeDTO> attributes = (productId != null)
                ? productAttributeService.getProductAttributes(productId, page, size)
                : productAttributeService.getAllProductAttributes(page, size);
        PageResponseDTO<ProductAttributeDTO> response = new PageUtil().toPageResponse(attributes);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra danh sách product attribute!", response));
    }
}