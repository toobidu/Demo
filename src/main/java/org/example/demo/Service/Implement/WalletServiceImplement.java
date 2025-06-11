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
        log.info("Processing deposit: {} for userId: {} by adminId: {}", amount, userId, adminId);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserFriendlyException("Deposit amount must be greater than 0");
        }

        Wallet wallet = getWalletByUserId(userId);
        User admin = getAdminById(adminId);

        Transaction transaction = new Transaction();
        transaction.setToWallet(wallet);
        transaction.setAmount(amount);
        transaction.setTransactionType("deposit"); // consider enum here
        transaction.setAdmin(admin);
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction); // assume DB trigger updates balance

        wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new UserFriendlyException("Wallet not found after deposit"));

        log.info("Deposit successful for userId: {}, new balance: {}", userId, wallet.getBalance());
        return walletMapper.toDTO(wallet);
    }

    @Override
    public WalletDTO getWallet(Long userId) {
        log.info("Retrieving wallet for userId: {}", userId);
        return walletMapper.toDTO(getWalletByUserId(userId));
    }

    // Tách nhỏ logic

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

