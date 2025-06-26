package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Finance.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByFromWalletId(Long walletId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.fromWalletId IN " +
            "(SELECT w.id FROM Wallet w WHERE w.userId = :userId) " +
            "OR t.toWalletId IN (SELECT w.id FROM Wallet w WHERE w.userId = :userId)")
    Page<Transaction> findTransactionsByUserId(@Param("userId") Long userId, Pageable pageable);

    Page<Transaction> findAll(Pageable pageable);
}
