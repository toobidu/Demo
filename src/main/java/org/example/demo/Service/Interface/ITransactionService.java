package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.Finance.TransactionDTO;
import org.springframework.data.domain.Page;

public interface ITransactionService {
    TransactionDTO createTransaction(TransactionDTO transactionDTO);

    TransactionDTO getTransactionById(Long id);

    Page<TransactionDTO> getAllTransactions(int page, int size);

    Page<TransactionDTO> getTransactionsByWalletId(Long walletId, int page, int size);

    Page<TransactionDTO> getTransactionsByUserId(Long userId, int page, int size);
}
