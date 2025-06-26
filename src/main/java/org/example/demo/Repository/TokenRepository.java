package org.example.demo.Repository;

import jakarta.transaction.Transactional;
import org.example.demo.Modal.Entity.Authentication.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByRefreshToken(String refreshToken);

    Optional<Token> findByUserId(Long userId);

    List<Token> findAllByUserId(Long userId);

    // Method để xóa token theo userId
    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    // Method để xóa token đã hết hạn
    @Modifying
    @Transactional
    @Query("DELETE FROM Token t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    // Method để check token có tồn tại và chưa hết hạn
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM Token t WHERE t.refreshToken = :refreshToken AND t.expiresAt > :now")
    boolean existsByRefreshTokenAndNotExpired(@Param("refreshToken") String refreshToken, @Param("now") LocalDateTime now);

    // Method để lấy tất cả token chưa hết hạn của user
    @Query("SELECT t FROM Token t WHERE t.userId = :userId AND t.expiresAt > :now")
    List<Token> findValidTokensByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}
