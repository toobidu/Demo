package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.Finance.TransactionDTO;

import java.util.List;

public interface ITransactionService {
    TransactionDTO createTransaction(TransactionDTO transactionDTO);

    TransactionDTO getTransactionById(Long id);

    List<TransactionDTO> getAllTransactions();

    List<TransactionDTO> getTransactionsByWalletId(Long walletId);

    List<TransactionDTO> getTransactionsByUserId(Long userId);
}
