package org.example.demo.Config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Security.JwtAuthenticationToken;
import org.example.demo.Service.RedisService;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final RedisService redisService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Authentication null hoặc chưa xác thực");
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            log.warn("Principal không phải kiểu UserDetails: {}", principal.getClass());
            return false;
        }

        String requiredPermission = permission.toString();
        String username = userDetails.getUsername();

        Long userId;
        try {
            userId = Long.valueOf(username);
        } catch (NumberFormatException e) {
            log.error("Không thể parse userId từ username: {}", username);
            return false;
        }

        // 👇 Luôn kiểm tra từ Redis
        boolean result = redisService.hasPermission(userId, requiredPermission);
        if (!result) {
            log.warn("Người dùng {} không có quyền '{}'", userId, requiredPermission);
        }
        return result;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false; // Không dùng loại này
    }
}