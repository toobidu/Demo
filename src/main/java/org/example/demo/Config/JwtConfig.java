package org.example.demo.Config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
@Slf4j
public class JwtConfig {
    @Value("${jwt.secret-key}")
    private String secret;
    @Value("${jwt.access-expiration}")
    private Long accessExpiration;
    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    @PostConstruct
    public void init() {
        log.info("JWT Config initialized with secret length: {}",
                secret != null ? secret.length() : "NULL");
        log.info("Access token expiration: {} ms", accessExpiration);
        log.info("Refresh token expiration: {} ms", refreshExpiration);
    }
}

