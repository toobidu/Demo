package org.example.demo.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Modal.DTO.Products.ProductPriceDTO;
import org.example.demo.Service.Interface.IProductPriceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-prices")
@RequiredArgsConstructor
public class ProductPriceController {

    private final IProductPriceService productPriceService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductPriceDTO>> createProductPrice(@Valid @RequestBody ProductPriceDTO productPriceDTO) {
        ProductPriceDTO created = productPriceService.createProductPrice(productPriceDTO);
        return ResponseEntity.ok(ApiResponse.success("Tạo product price thông!", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductPriceDTO>> updateProductPrice(@PathVariable Long id, @Valid @RequestBody ProductPriceDTO productPriceDTO) {
        ProductPriceDTO updated = productPriceService.updateProductPrice(id, productPriceDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật product price thông!", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProductPrice(@PathVariable Long id) {
        productPriceService.deleteProductPrice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductPriceDTO>> getProductPrice(@PathVariable Long id) {
        ProductPriceDTO productPrice = productPriceService.getProductPrice(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra product price!", productPrice));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductPriceDTO>>> getProductPrices(
            @RequestParam(name = "productId", required = false) Long productId,
            @RequestParam(name = "rank", required = false) String rank) {
        List<ProductPriceDTO> prices = productPriceService.getProductPrices(productId, rank);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra danh sách product price!", prices));
    }
}
