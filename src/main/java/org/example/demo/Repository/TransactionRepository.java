package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Finance.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByFromWalletId(Long walletId);

    List<Transaction> findByFromWalletUserIdOrToWalletUserId(Long userIdFrom, Long userIdTo);
}
