package org.example.demo.Config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Security.JwtAuthenticationToken;
import org.example.demo.Service.RedisService;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final RedisService redisService;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !(authentication instanceof JwtAuthenticationToken)) {
            log.warn("Authentication không hợp lệ hoặc không phải JwtAuthenticationToken");
            return false;
        }

        Long userId = (Long) authentication.getPrincipal();
        String requiredPermission = permission.toString();

        log.debug("Kiểm tra quyền: userId={}, permission={}", userId, requiredPermission);
        return redisService.hasAuth2(userId, requiredPermission);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }
}