package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Query("SELECT w FROM Wallet w JOIN w.user u JOIN u.roles r WHERE r.roleName = :roleName")
    Optional<Wallet> findByUserRoleName(String roleName);
    
    @Query("SELECT w FROM Wallet w WHERE w.walletName = :walletName")
    Optional<Wallet> findByWalletName(String walletName);
    
    @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId")
    List<Wallet> findByUserId(Long userId);
}