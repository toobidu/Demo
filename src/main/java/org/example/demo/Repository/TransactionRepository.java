package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Finance.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByFromWalletId(Long walletId, Pageable pageable);

    Page<Transaction> findByFromWalletUserIdOrToWalletUserId(Long fromUserId, Long toUserId, Pageable pageable);

    Page<Transaction> findAll(Pageable pageable);
}
