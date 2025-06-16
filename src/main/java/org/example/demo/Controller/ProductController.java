package org.example.demo.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Modal.DTO.Products.ProductDTO;
import org.example.demo.Service.Interface.IProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final IProductService productService;

    @PostMapping
    @PreAuthorize("hasPermission(null, 'create_product')")
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO created = productService.createProduct(productDTO);
        return ResponseEntity.ok(ApiResponse.success("Tạo sản phẩm thành công!", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'update_product')")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO updated = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật sản phẩm thành công!", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'delete_product')")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa sản phẩm thành công!", null));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'view_product')")
    public ResponseEntity<ApiResponse<ProductDTO>> getProduct(@PathVariable Long id) {
        ProductDTO productDTO = productService.getProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra sản phẩm!", productDTO));
    }

    @GetMapping
    @PreAuthorize("hasPermission(null, 'view_products')")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts(@RequestParam(name = "name", required = false) String name) {
        List<ProductDTO> products = productService.getAllProducts(name);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra danh sách sản phẩm!", products));
    }
}