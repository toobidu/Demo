package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.WalletMapper;
import org.example.demo.Modal.DTO.Finance.WalletDTO;
import org.example.demo.Modal.Entity.Finance.Transaction;
import org.example.demo.Modal.Entity.Finance.Wallet;
import org.example.demo.Modal.Entity.Users.User;
import org.example.demo.Repository.TransactionRepository;
import org.example.demo.Repository.UserRepository;
import org.example.demo.Repository.WalletRepository;
import org.example.demo.Service.Interface.IWalletService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WalletServiceImplement implements IWalletService {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final WalletMapper walletMapper;

    @Override
    public WalletDTO deposit(Long userId, BigDecimal amount, Long adminId) {
        log.info("Processing deposit: {} for userId: {} by adminId: {}", amount, userId, adminId);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserFriendlyException("Deposit amount must be greater than 0");
        }

        Wallet wallet = getWalletByUserId(userId);
        User admin = getAdminById(adminId);

        Transaction transaction = new Transaction();
        transaction.setToWallet(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType("deposit");
        transaction.setAdmin(admin);
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction); // giả sử có trigger cập nhật balance tự động

        wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new UserFriendlyException("Wallet not found after deposit"));

        log.info("Deposit successful for userId: {}, new balance: {}", userId, wallet.getBalance());
        return walletMapper.toDTO(wallet);
    }

    @Override
    public void deductBalance(Long userId, BigDecimal amount) {
        log.info("Deducting {} from user {}:{}", userId, "balance", amount);

        Wallet wallet = getWalletByUserId(userId);
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new UserFriendlyException("Số dư không đủ để thực hiện giao dịch");
        }

        Transaction transaction = new Transaction();
        transaction.setFromWallet(wallet);
        transaction.setToWallet(null);
        transaction.setAmount(amount);
        transaction.setTransactionType("deduct_balance_on_order");
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    @Override
    public void creditAdmin(BigDecimal amount) {
        log.info("Crediting {} to admin wallet", amount);

        // Lấy admin đầu tiên trong hệ thống
        List<User> admins = userRepository.findByTypeAccount("admin");

        if (admins.isEmpty()) {
            throw new UserFriendlyException("Không tìm thấy tài khoản admin nào");
        }

        User admin = admins.get(0); // hoặc kiểm tra thêm quyền cụ thể
        Wallet adminWallet = walletRepository.findByUserId(admin.getId())
                .orElseThrow(() -> new UserFriendlyException("Ví admin không tồn tại"));

        Transaction transaction = new Transaction();
        transaction.setToWallet(adminWallet);
        transaction.setAmount(amount);
        transaction.setTransactionType("credit_admin_on_payment");
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    @Override
    public void creditPrinthouse(Long printerHouseId, BigDecimal amount) {
        log.info("Crediting {} to print house ID: {}", amount, printerHouseId);

        Wallet printerWallet = getWalletByUserId(printerHouseId);

        Transaction transaction = new Transaction();
        transaction.setToWallet(printerWallet);
        transaction.setAmount(amount);
        transaction.setTransactionType("credit_printhouse_on_shipping");
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    @Override
    public void refundOnCancel(Long userId, BigDecimal amount) {
        log.info("Refunding {} to user {}:{}", amount, userId, "balance");

        Wallet wallet = getWalletByUserId(userId);

        Transaction transaction = new Transaction();
        transaction.setToWallet(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType("refund_money_on_cancelled_order");
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    @Override
    public WalletDTO getWallet(Long userId) {
        log.info("Retrieving wallet for userId: {}", userId);
        return walletMapper.toDTO(getWalletByUserId(userId));
    }

    // --- Hàm hỗ trợ ---

    private Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new UserFriendlyException("Wallet not found for userId: " + userId));
    }

    private User getAdminById(Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new UserFriendlyException("Admin not found: ID " + adminId));
        if (!"admin".equals(admin.getTypeAccount())) {
            throw new UserFriendlyException("Only admins can approve deposits");
        }
        return admin;
    }
}

