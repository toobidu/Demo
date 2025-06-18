package org.example.demo.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Config.JwtConfig;
import org.example.demo.Exception.UserFriendlyException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtConfig jwtConfig;

    private SecretKey getSigningKey() {
        try {
            String secret = jwtConfig.getSecret();
            log.debug("JWT Secret (raw UTF-8): {}", secret);

            byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
            log.debug("UTF-8 key length (bytes): {}", keyBytes.length);

            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            log.error("Error creating signing key: ", e);
            throw new RuntimeException("Cannot create JWT signing key: " + e.getMessage(), e);
        }
    }


    public String generateAccessToken(Long userId, List<String> permissions, String typeAccount, String rank) {
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
                    .claim("typeAccount", typeAccount)
                    .claim("rank", rank)
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
                    .signWith(getSigningKey(), SignatureAlgorithm.HS384)
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

    public List<String> getPermissionsFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Object rawPermissions = claims.get("permissions");

            if (rawPermissions == null) {
                log.warn("Permissions claim is null");
                return Collections.emptyList();
            }
            // Nếu là List<?> (bình thường)
            if (rawPermissions instanceof List<?> list) {
                return list.stream().map(Object::toString).collect(Collectors.toList());
            }
            // Nếu là String (bị serialize sai)
            if (rawPermissions instanceof String str) {
                // Có thể là chuỗi JSON, thử tách bằng dấu phẩy
                if (str.startsWith("[") && str.endsWith("]")) {
                    str = str.substring(1, str.length() - 1); // bỏ []
                }
                if (!str.isBlank()) {
                    return List.of(str.split(",")).stream().map(String::trim).collect(Collectors.toList());
                }
            }
            log.warn("Permissions claim is not a List or String: {}", rawPermissions.getClass());
            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Failed to parse permissions from token", e);
            return Collections.emptyList();
        }
    }

    public Map<String, Object> getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Lỗi khi lấy claims từ AccessToken: ", e);
            throw new UserFriendlyException("Token không hợp lệ");
        }
    }


    public long getRefreshExpiration() {
        return jwtConfig.getRefreshExpiration();
    }
}
