package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.TransactionMapper;
import org.example.demo.Modal.DTO.Finance.TransactionDTO;
import org.example.demo.Modal.Entity.Finance.Transaction;
import org.example.demo.Modal.Entity.Finance.Wallet;
import org.example.demo.Repository.TransactionRepository;
import org.example.demo.Repository.WalletRepository;
import org.example.demo.Service.Interface.ITransactionService;
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

    @Override
    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        log.info("Creating transaction: {}", transactionDTO);

        Transaction transaction = transactionMapper.toEntity(transactionDTO);

        if (transactionDTO.getFromWalletId() != null) {
            Wallet fromWallet = walletRepository.findById(transactionDTO.getFromWalletId())
                    .orElseThrow(() -> new UserFriendlyException("Không tìm thấy ví nguồn"));
            transaction.setFromWalletId(fromWallet.getId());
        }

        if (transactionDTO.getToWalletId() != null) {
            Wallet toWallet = walletRepository.findById(transactionDTO.getToWalletId())
                    .orElseThrow(() -> new UserFriendlyException("Không tìm thấy ví đích"));
            transaction.setToWalletId(toWallet.getId());
        }

        if (transaction.getFromWalletId() != null && transaction.getToWalletId() != null &&
                transaction.getFromWalletId().equals(transaction.getToWalletId())) {
            throw new UserFriendlyException("Ví nguồn và ví đích không được trùng nhau");
        }

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction created with ID: {}", savedTransaction.getId());

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
        Page<Transaction> transactions = transactionRepository.findTransactionsByUserId(userId, pageable);
        return transactions.map(transactionMapper::toDTO);
    }

    private void updateWalletBalances(Transaction transaction) {
        if (transaction.getFromWalletId() != null) {
            Wallet fromWallet = walletRepository.findById(transaction.getFromWalletId())
                    .orElseThrow(() -> new UserFriendlyException("Không tìm thấy ví nguồn"));
            fromWallet.setBalance(fromWallet.getBalance().subtract(transaction.getAmount()));
            walletRepository.save(fromWallet);
        }

        if (transaction.getToWalletId() != null) {
            Wallet toWallet = walletRepository.findById(transaction.getToWalletId())
                    .orElseThrow(() -> new UserFriendlyException("Không tìm thấy ví đích"));
            toWallet.setBalance(toWallet.getBalance().add(transaction.getAmount()));
            walletRepository.save(toWallet);
        }
    }
}