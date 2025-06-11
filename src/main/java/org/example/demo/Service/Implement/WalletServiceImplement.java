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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
        log.info("Processing deposit of {} for userId: {} by adminId: {}", amount, userId, adminId);
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Wallet not found for userId: {}", userId);
                    return new UserFriendlyException("Wallet not found");
                });
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> {
                    log.error("Admin not found: ID {}", adminId);
                    return new UserFriendlyException("Admin not found");
                });
        if (!admin.getTypeAccount().equals("admin")) {
            log.error("User {} is not an admin", adminId);
            throw new UserFriendlyException("Only admins can approve deposits");
        }

        Transaction transaction = new Transaction();
        transaction.setToWallet(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType("deposit");
        transaction.setAdmin(admin);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction); // Trigger will update wallet balance

        wallet = walletRepository.findByUserId(userId).orElseThrow();
        log.info("Deposit successful for userId: {}, new balance: {}", userId, wallet.getBalance());
        return walletMapper.toDTO(wallet);
    }

    @Override
    public WalletDTO getWallet(Long userId) {
        log.info("Retrieving wallet for userId: {}", userId);
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.error("Wallet not found for userId: {}", userId);
                    return new UserFriendlyException("Wallet not found");
                });
        return walletMapper.toDTO(wallet);
    }
}
