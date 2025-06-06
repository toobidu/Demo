package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Transaction;
import org.example.demo.Modal.Entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromWallet(Wallet wallet);
    List<Transaction> findByToWallet(Wallet wallet);
    List<Transaction> findByTransactionType(String transactionType);
}