package org.example.demo.Controller;

import lombok.RequiredArgsConstructor;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Modal.DTO.Finance.TransactionDTO;
import org.example.demo.Service.Interface.ITransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final ITransactionService transactionService;

    @PostMapping
    @PreAuthorize("hasPermission(null, 'create_transaction') or hasPermission(null, 'approve_deposit')")
    public ResponseEntity<ApiResponse<TransactionDTO>> createTransaction(@RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.ok(ApiResponse.success("Giao dịch đã được tạo thành công!", transactionService.createTransaction(transactionDTO)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'view_transaction') or authentication.principal.id == #userId")
    public ResponseEntity<ApiResponse<TransactionDTO>> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Thông tin giao dịch", transactionService.getTransactionById(id)));
    }

    @GetMapping
    @PreAuthorize("hasPermission(null, 'view_transactions')")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getAllTransactions() {
        return ResponseEntity.ok(ApiResponse.success("Danh sách giao dịch", transactionService.getAllTransactions()));
    }

    // Lấy giao dịch theo ví
    @GetMapping("/wallet/{walletId}")
    @PreAuthorize("hasPermission(#walletId, 'view_own_transactions')")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getTransactionsByWalletId(@PathVariable Long walletId) {
        return ResponseEntity.ok(ApiResponse.success("Lịch sử giao dịch của ví", transactionService.getTransactionsByWalletId(walletId)));
    }

    // Lấy giao dịch theo người dùng (admin, sale, printer_house)
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasPermission(#userId, 'view_own_transactions') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TransactionDTO>>> getTransactionsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Lịch sử giao dịch của người dùng", transactionService.getTransactionsByUserId(userId)));
    }
}
