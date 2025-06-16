package org.example.demo.Security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Config.JwtConfig;
import org.example.demo.Exception.UserFriendlyException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtConfig jwtConfig;

    private SecretKey getSigningKey() {
        try {
            String secret = jwtConfig.getSecret();
            log.debug("JWT Secret length: {}", secret != null ? secret.length() : "NULL");

            // Sử dụng secret key trực tiếp
            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            log.debug("Using raw string secret, length: {} bytes", keyBytes.length);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            log.error("Error creating signing key: ", e);
            throw new RuntimeException("Cannot create JWT signing key: " + e.getMessage(), e);
        }
    }

    public String generateAccessToken(Long userId, List<String> permissions) {
        try {
            log.info("Generating access token for userId: {} with permissions: {}", userId, permissions);

            // Kiểm tra đầu vào
            if (userId == null) {
                throw new IllegalArgumentException("UserId cannot be null");
            }

            Date now = new Date();
            Date expiration = new Date(now.getTime() + jwtConfig.getAccessExpiration());

            log.debug("Token expiration: {}, Duration: {} ms", expiration, jwtConfig.getAccessExpiration());

            String token = Jwts.builder()
                    .subject(userId.toString())
                    .issuedAt(now)
                    .expiration(expiration)
                    .claim("permissions", permissions)
                    .signWith(getSigningKey())
                    .compact();

            log.info("Access token generated successfully for userId: {}", userId);
            return token;
        } catch (Exception e) {
            log.error("Lỗi khi tạo access token cho userId {}: ", userId, e);
            throw new RuntimeException("Không thể tạo access token: " + e.getMessage(), e);
        }
    }

    public String generateRefreshToken(Long userId) {
        try {
            log.info("Generating refresh token for userId: {}", userId);

            // Validate inputs
            if (userId == null) {
                throw new IllegalArgumentException("UserId cannot be null");
            }

            Date now = new Date();
            Date expiration = new Date(now.getTime() + jwtConfig.getRefreshExpiration());

            log.debug("Refresh token expiration: {}, Duration: {} ms", expiration, jwtConfig.getRefreshExpiration());

            String token = Jwts.builder()
                    .subject(userId.toString())
                    .issuedAt(now)
                    .expiration(expiration)
                    .signWith(getSigningKey())
                    .compact();

            log.info("Refresh token generated successfully for userId: {}", userId);
            return token;
        } catch (Exception e) {
            log.error("Lỗi khi tạo refresh token cho userId {}: ", userId, e);
            throw new RuntimeException("Không thể tạo refresh token: " + e.getMessage(), e);
        }
    }

    public Long getUserIdFromToken(String token) {
        log.debug("Extracting userId from token");
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new IllegalArgumentException("Token cannot be null or empty");
            }

            String subject = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();

            if (subject == null || subject.trim().isEmpty()) {
                throw new IllegalArgumentException("Token subject is null or empty");
            }

            return Long.parseLong(subject);
        } catch (NumberFormatException e) {
            log.error("Cannot parse userId from token subject: {}", e.getMessage());
            throw new UserFriendlyException("Invalid token format");
        } catch (Exception e) {
            log.error("Error parsing token: {}", e.getMessage());
            throw new UserFriendlyException("Invalid token");
        }
    }

    public boolean validateToken(String token) {
        log.debug("Validating token");
        try {
            if (token == null || token.trim().isEmpty()) {
                log.debug("Token is null or empty");
                return false;
            }

            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            log.debug("Token validated successfully");
            return true;
        } catch (Exception e) {
            log.error("Invalid token: {}", e.getMessage());
            return false;
        }
    }

    public long getRefreshExpiration() {
        return jwtConfig.getRefreshExpiration();
    }
}
