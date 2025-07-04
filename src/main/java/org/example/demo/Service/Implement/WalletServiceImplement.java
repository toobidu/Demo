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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<Transaction> getUserTransactions(Long userId, Pageable pageable) {
        return transactionRepository.findTransactionsByUserId(userId, pageable);
    }

    @Override
    public WalletDTO deposit(Long userId, BigDecimal amount, Long adminId) {
        log.info("Processing deposit: {} for userId: {} by adminId: {}", amount, userId, adminId);

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new UserFriendlyException("Deposit amount must be greater than 0");
        }

        Wallet wallet = getWalletByUserId(userId);
        User admin = getAdminById(adminId);

        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        Transaction transaction = new Transaction();
        transaction.setToWalletId(wallet.getId());
        transaction.setAmount(amount);
        transaction.setTransactionType("deposit");
        transaction.setAdminId(admin.getId());
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);

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
        transaction.setFromWalletId(wallet.getId());
        transaction.setAmount(amount);
        transaction.setTransactionType("deduct_balance_on_order");
        transaction.setCreatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    @Override
    public void creditAdmin(BigDecimal amount) {
        log.info("Crediting {} to admin wallet", amount);
        List<User> admins = userRepository.findByTypeAccount("admin");

        if (admins.isEmpty()) {
            throw new UserFriendlyException("Không tìm thấy tài khoản admin nào");
        }

        User admin = admins.get(0);
        Wallet adminWallet = walletRepository.findByUserId(admin.getId())
                .orElseThrow(() -> new UserFriendlyException("Ví admin không tồn tại"));

        Transaction transaction = new Transaction();
        transaction.setToWalletId(adminWallet.getId());
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
        transaction.setToWalletId(printerWallet.getId());
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
        transaction.setToWalletId(wallet.getId());
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

    @Override
    public Page<WalletDTO> getAllWallets(int page, int size) {
        log.info("Retrieving all wallets with paging - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Wallet> wallets = walletRepository.findAll(pageable);
        return wallets.map(walletMapper::toDTO);
    }

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