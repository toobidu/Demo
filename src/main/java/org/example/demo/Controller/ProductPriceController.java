package org.example.demo.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Config.PageResponseDTO;
import org.example.demo.Config.PageUtil;
import org.example.demo.Modal.DTO.Products.ProductPriceDTO;
import org.example.demo.Service.Interface.IProductPriceService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product-prices")
@RequiredArgsConstructor
public class ProductPriceController {

    private final IProductPriceService productPriceService;

    @PostMapping
    @PreAuthorize("hasPermission(null, 'create_product_price')")
    public ResponseEntity<ApiResponse<ProductPriceDTO>> createProductPrice(@Valid @RequestBody ProductPriceDTO productPriceDTO) {
        ProductPriceDTO created = productPriceService.createProductPrice(productPriceDTO);
        return ResponseEntity.ok(ApiResponse.success("Tạo product price thành công!", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'update_product_price')")
    public ResponseEntity<ApiResponse<ProductPriceDTO>> updateProductPrice(@PathVariable Long id, @Valid @RequestBody ProductPriceDTO productPriceDTO) {
        ProductPriceDTO updated = productPriceService.updateProductPrice(id, productPriceDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật product price thành công!", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'delete_product_price')")
    public ResponseEntity<ApiResponse<Void>> deleteProductPrice(@PathVariable Long id) {
        productPriceService.deleteProductPrice(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa product price thành công!", null));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'view_product_price')")
    public ResponseEntity<ApiResponse<ProductPriceDTO>> getProductPrice(@PathVariable Long id) {
        ProductPriceDTO productPrice = productPriceService.getProductPrice(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra product price!", productPrice));
    }

    @GetMapping
    @PreAuthorize("hasPermission(null, 'view_product_prices')")
    public ResponseEntity<ApiResponse<PageResponseDTO<ProductPriceDTO>>> getAllProductPrices(
            @RequestParam(name = "productId", required = false) Long productId,
            @RequestParam(name = "rank", required = false) String rank,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Page<ProductPriceDTO> productPrices = productPriceService.getProductPrices(productId, rank, page, size);
        PageResponseDTO<ProductPriceDTO> response = new PageUtil().toPageResponse(productPrices);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra danh sách product price!", response));
    }

    @GetMapping("/base/product/{productId}")
    @PreAuthorize("hasPermission(null, 'view_product_prices')")
    public ResponseEntity<ApiResponse<Page<ProductPriceDTO>>> getBaseProductPrices(
            @PathVariable Long productId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        Page<ProductPriceDTO> basePrices = productPriceService.getProductPrices(productId, null, page, size);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra giá gốc của sản phẩm!", basePrices));
    }
}