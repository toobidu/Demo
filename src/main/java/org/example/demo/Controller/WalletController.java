package org.example.demo.Controller;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Config.PageResponseDTO;
import org.example.demo.Config.PageUtil;
import org.example.demo.Modal.DTO.Finance.WalletDTO;
import org.example.demo.Service.Interface.IWalletService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final IWalletService walletService;

    //Sale nạp tiền thì Admin duyệt
    @PostMapping("/deposit")
    @PreAuthorize("hasPermission(null, 'approve_deposit')") // chỉ admin được duyệt
    public ResponseEntity<ApiResponse<WalletDTO>> deposit(
            @RequestParam @NotNull(message = "User ID is required") Long userId,
            @RequestParam @DecimalMin(value = "1.00", message = "Amount must be positive") BigDecimal amount,
            @RequestParam Long adminId) {
        WalletDTO walletDTO = walletService.deposit(userId, amount, adminId);
        return ResponseEntity.ok(ApiResponse.success("Nạp tiền thành công!", walletDTO));
    }

    // Xem ví cá nhân
    @GetMapping("/{userId}")
    @PreAuthorize("hasPermission(null, 'view_wallet')")
    public ResponseEntity<ApiResponse<WalletDTO>> getWallet(@PathVariable Long userId) {
        WalletDTO walletDTO = walletService.getWallet(userId);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra ví thành công!", walletDTO));
    }

    // Lấy danh sách ví có phân trang
    @GetMapping
    @PreAuthorize("hasPermission(null, 'view_wallets')")
    public ResponseEntity<ApiResponse<PageResponseDTO<WalletDTO>>> getAllWallets(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<WalletDTO> walletsPage = walletService.getAllWallets(page, size);
        PageResponseDTO<WalletDTO> response = new PageUtil().toPageResponse(walletsPage);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra danh sách ví thành công!", response));
    }
}
