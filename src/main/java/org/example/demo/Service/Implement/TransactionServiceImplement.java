package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.TransactionMapper;
import org.example.demo.Mapper.UserMapper;
import org.example.demo.Modal.DTO.Finance.TransactionDTO;
import org.example.demo.Modal.DTO.Users.UserDTO;
import org.example.demo.Modal.Entity.Finance.Transaction;
import org.example.demo.Modal.Entity.Finance.Wallet;
import org.example.demo.Modal.Entity.Users.User;
import org.example.demo.Repository.TransactionRepository;
import org.example.demo.Repository.WalletRepository;
import org.example.demo.Service.Interface.ITransactionService;
import org.example.demo.Service.Interface.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImplement implements ITransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final WalletRepository walletRepository;
    private final IUserService userService;
    private final UserMapper userMapper;

    @Override
    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        log.info("Creating transaction: {}", transactionDTO);

        Transaction transaction = transactionMapper.toEntity(transactionDTO);

        // Kiểm tra ví nguồn
        if (transactionDTO.getFromWalletId() != null) {
            Wallet fromWallet = walletRepository.findById(transactionDTO.getFromWalletId())
                    .orElseThrow(() -> new UserFriendlyException("Không tìm thấy ví nguồn"));
            transaction.setFromWallet(fromWallet);
        }

        // Kiểm tra ví đích
        if (transactionDTO.getToWalletId() != null) {
            Wallet toWallet = walletRepository.findById(transactionDTO.getToWalletId())
                    .orElseThrow(() -> new UserFriendlyException("Không tìm thấy ví đích"));
            transaction.setToWallet(toWallet);
        }

        // Kiểm tra trùng ví nguồn và đích
        if (transaction.getFromWallet() != null && transaction.getToWallet() != null &&
                transaction.getFromWallet().getId().equals(transaction.getToWallet().getId())) {
            throw new UserFriendlyException("Ví nguồn và ví đích không được trùng nhau");
        }

        // Kiểm tra admin nếu có
        if (transactionDTO.getAdminId() != null) {
            UserDTO adminDTO = userService.getUser(transactionDTO.getAdminId());
            if (adminDTO == null) {
                throw new UserFriendlyException("Không tìm thấy admin với ID: " + transactionDTO.getAdminId());
            }
            User adminEntity = userMapper.toEntity(adminDTO);
            transaction.setAdmin(adminEntity);
        }

        // Lưu giao dịch
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction created with ID: {}", savedTransaction.getId());

        // Cập nhật số dư ví tự động
        updateWalletBalances(savedTransaction);

        return transactionMapper.toDTO(savedTransaction);
    }

    @Override
    public TransactionDTO getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Không tìm thấy giao dịch với ID: " + id));
        return transactionMapper.toDTO(transaction);
    }

    @Override
    public Page<TransactionDTO> getAllTransactions(int page, int size) {
        log.info("Getting all transactions with paging - page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Transaction> transactions = transactionRepository.findAll(pageable);
        return transactions.map(transactionMapper::toDTO);
    }

    @Override
    public Page<TransactionDTO> getTransactionsByWalletId(Long walletId, int page, int size) {
        log.info("Getting transactions for wallet ID: {} with paging - page: {}, size: {}", walletId, page, size);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Transaction> transactions = transactionRepository.findByFromWalletId(walletId, pageable);
        return transactions.map(transactionMapper::toDTO);
    }

    @Override
    public Page<TransactionDTO> getTransactionsByUserId(Long userId, int page, int size) {
        log.info("Getting transactions for user ID: {} with paging - page: {}, size: {}", userId, page, size);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Transaction> transactions = transactionRepository.findByFromWalletUserIdOrToWalletUserId(userId, userId, pageable);
        return transactions.map(transactionMapper::toDTO);
    }

    private void updateWalletBalances(Transaction transaction) {
        if (transaction.getFromWallet() != null) {
            transaction.getFromWallet().setBalance(
                    transaction.getFromWallet().getBalance().subtract(transaction.getAmount()));
            walletRepository.save(transaction.getFromWallet());
        }

        if (transaction.getToWallet() != null) {
            transaction.getToWallet().setBalance(
                    transaction.getToWallet().getBalance().add(transaction.getAmount()));
            walletRepository.save(transaction.getToWallet());
        }
    }
}
