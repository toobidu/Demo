package org.example.demo.Controller;

import lombok.RequiredArgsConstructor;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Config.PageResponseDTO;
import org.example.demo.Config.PageUtil;
import org.example.demo.Modal.DTO.Finance.TransactionDTO;
import org.example.demo.Service.Interface.ITransactionService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final ITransactionService transactionService;

    @PostMapping
    @PreAuthorize("hasPermission(null, 'create_transaction') and #transactionDTO.transactionType == 'deposit'")
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
    public ResponseEntity<ApiResponse<PageResponseDTO<TransactionDTO>>> getAllTransactions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TransactionDTO> transactionsPage = transactionService.getAllTransactions(page, size);
        PageResponseDTO<TransactionDTO> response = new PageUtil().toPageResponse(transactionsPage);
        return ResponseEntity.ok(ApiResponse.success("Danh sách giao dịch", response));
    }

    @GetMapping("/wallet/{walletId}")
    @PreAuthorize("hasPermission(#walletId, 'view_own_transactions')")
    public ResponseEntity<ApiResponse<PageResponseDTO<TransactionDTO>>> getTransactionsByWalletId(
            @PathVariable Long walletId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TransactionDTO> transactionsPage = transactionService.getTransactionsByWalletId(walletId, page, size);
        PageResponseDTO<TransactionDTO> response = new PageUtil().toPageResponse(transactionsPage);
        return ResponseEntity.ok(ApiResponse.success("Lịch sử giao dịch của ví", response));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasPermission(#userId, 'view_own_transactions') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponseDTO<TransactionDTO>>> getTransactionsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<TransactionDTO> transactionsPage = transactionService.getTransactionsByUserId(userId, page, size);
        PageResponseDTO<TransactionDTO> response = new PageUtil().toPageResponse(transactionsPage);
        return ResponseEntity.ok(ApiResponse.success("Lịch sử giao dịch của người dùng", response));
    }
}
