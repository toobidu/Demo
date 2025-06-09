package org.example.demo.Security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Modal.DTO.Authentication.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtUtil {
    @Value("${jwt.secret-key}")
    private String secret;

    @Value("${jwt.access-expiration}")
    private Long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    public LoginResponse generateToken(String userName) {
        log.debug("Tạo cặp token cho người dùng: {}", userName);
        String accessToken = generateAccessToken(userName);
        String refreshToken = generateRefreshToken(userName);
        return new LoginResponse(accessToken, refreshToken);
    }

    public String generateAccessToken(String userName) {
        log.debug("Tạo access token cho người dùng: {}", userName);
        return generateAccessToken(userName, accessExpiration, null);
    }

    public String generateRefreshToken(String userName) {
        log.debug("Tạo refresh token cho người dùng: {}", userName);
        Map<String, Object> claims = new HashMap<>();
        claims.put("scopes", "refresh");
        return generateAccessToken(userName, refreshExpiration, claims);
    }

    //Tạo access token hoặc refresh token tùy theo claim và thời gian truyền vào.
    private String generateAccessToken(String userName, long expiration, Map<String, Object> claims) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);

        JwtBuilder builder = Jwts.builder()
                .subject(userName)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey());

        // Nếu có thêm claims, set vào token
        if (claims != null) {
            builder.claims(claims);
        }

        String token = builder.compact();
        log.debug("Tạo token cho người dùng: {}", userName);
        return token;
    }

    //Kiểm tra tính hợp lệ của token
    public boolean validateToken(String token) {
        log.debug("Xác thực token.");
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            log.debug("Xác thực token thành công");
            return true;
        } catch (SignatureException e) {
            log.error("Chữ ký không hợp lệ: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Token không hợp lệ: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token hết hạn: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Chuỗi rỗng: {}", e.getMessage());
        }
        return false;
    }

    //Trích xuất tên người dùng từ token
    public String extractUsername(String token) {
        log.debug("Trích xuất tên người dùng từ token.");
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            log.error("Lỗi khi trích xuất tên người dùng từ token: {}", e.getMessage());
            throw new UserFriendlyException("Token không hợp lệ!");
        }
    }

    // Tạo khóa ký HMAC từ secret string để dùng trong sign/verify token.
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

}
