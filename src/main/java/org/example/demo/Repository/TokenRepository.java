package org.example.demo.Repository;

import org.example.demo.Modal.Entity.Authentication.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByRefreshToken(String refreshToken);

    Optional<Token> findByUserId(Long userId);
}
