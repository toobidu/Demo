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
import org.springframework.stereotype.Service;

import java.util.List;

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
        log.info("Bắt đầu tạo giao dịch với dữ liệu: {}", transactionDTO);

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

        // Kiểm tra xem ví nguồn và đích có trùng nhau không
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

        // Lưu giao dịch vào DB
        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Giao dịch đã được tạo thành công với ID: {}", savedTransaction.getId());

        return transactionMapper.toDTO(savedTransaction);
    }

    @Override
    public TransactionDTO getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Không tìm thấy giao dịch với ID: " + id));
        return transactionMapper.toDTO(transaction);
    }

    @Override
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(transactionMapper::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactionsByWalletId(Long walletId) {
        List<Transaction> transactions = transactionRepository.findByFromWalletId(walletId);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<TransactionDTO> getTransactionsByUserId(Long userId) {
        List<Transaction> transactions = transactionRepository.findByFromWalletUserIdOrToWalletUserId(userId, userId);
        return transactions.stream()
                .map(transactionMapper::toDTO)
                .collect(java.util.stream.Collectors.toList());
    }
}
