package org.example.demo.Config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Modal.Entity.Users.Role;
import org.example.demo.Modal.Entity.Users.User;
import org.example.demo.Repository.RoleRepository;
import org.example.demo.Repository.UserRepository;
import org.example.demo.Security.JwtAuthenticationToken;
import org.example.demo.Service.RedisService;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final RedisService redisService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Authentication is null or unauthenticated");
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            log.warn("Principal is not instance of UserDetails: {}", principal.getClass());
            return false;
        }

        String requiredPermission = permission.toString();
        String username = userDetails.getUsername();

        Long userId;
        try {
            userId = Long.valueOf(username);
        } catch (NumberFormatException e) {
            log.error("Cannot parse userId from username: {}", username);
            return false;
        }

        // Kiểm tra xem người dùng có quyền này trong Redis không
        boolean result = redisService.hasPermission(userId, requiredPermission);

        if (!result) {
            log.warn("User {} does không có quyền '{}'", userId, requiredPermission);
        }
        return result;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (!"role".equalsIgnoreCase(targetType)) {
            log.warn("Target type is not 'role': {}", targetType);
            return false;
        }

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Authentication is null or unauthenticated");
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            log.warn("Principal is not instance of UserDetails: {}", principal.getClass());
            return false;
        }

        Long roleId;
        try {
            roleId = Long.valueOf(targetId.toString());
        } catch (NumberFormatException e) {
            log.warn("Cannot parse roleId from targetId: {}", targetId);
            return false;
        }

        // Lấy thông tin role đang xử lý
        Role targetRole = roleRepository.findById(roleId).orElse(null);
        if (targetRole == null) {
            log.warn("Cannot find role with id: {}", roleId);
            return false;
        }

        String targetRoleName = targetRole.getRoleName().toUpperCase();
        String userRoleName = getUserRoleFromAuthentication(authentication);

        log.debug("Checking permission for userRoleName: {}, targetRoleName: {}", userRoleName, targetRoleName);

        // Logic phân cấp
        if ("SUPER_ADMIN".equalsIgnoreCase(userRoleName)) {
            return true; // Super admin luôn được phép
        }

        if ("ADMIN".equalsIgnoreCase(userRoleName)) {
            return isAllowedAdminToModifyRole(targetRoleName);
        }

        return false;
    }

    private String getUserRoleFromAuthentication(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            try {
                Long userId = Long.valueOf(username);
                User user = userRepository.findByUsername(String.valueOf(userId)).orElse(null);
                if (user != null && user.getTypeAccount() != null) {
                    return user.getTypeAccount().toUpperCase();
                }
            } catch (Exception e) {
                log.warn("Cannot retrieve typeAccount for user: {}", username);
            }
        }
        return null;
    }

    private boolean isAllowedAdminToModifyRole(String targetRoleName) {
        Set<String> allowedRolesForAdmin = Set.of("SALE", "PRINTER_HOUSE");

        return allowedRolesForAdmin.contains(targetRoleName);
    }
}