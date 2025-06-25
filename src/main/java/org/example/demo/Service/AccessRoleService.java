package org.example.demo.Service;

import lombok.RequiredArgsConstructor;
import org.example.demo.Modal.Entity.Users.User;
import org.example.demo.Repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AccessRoleService {
    private final UserRepository userRepository;

    public void checkAccess(Long targetUserId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!(auth.getPrincipal() instanceof UserDetails userDetails)) {
            throw new AccessDeniedException("Không thể xác định người dùng hiện tại");
        }

        String currentUsername = userDetails.getUsername();

        // Parse currentUserId an toàn hơn
        Long currentUserId;
        try {
            currentUserId = Long.valueOf(currentUsername);
        } catch (NumberFormatException e) {
            throw new AccessDeniedException("Username không hợp lệ - không phải số");
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new AccessDeniedException("Không tìm thấy người dùng"));

        String currentUserTypeAccount = currentUser.getTypeAccount();
        if (currentUserTypeAccount == null) {
            throw new AccessDeniedException("Không xác định được typeAccount của người dùng hiện tại");
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new AccessDeniedException("Không tìm thấy người dùng mục tiêu"));

        String targetTypeAccount = targetUser.getTypeAccount();
        if (targetTypeAccount == null) {
            throw new AccessDeniedException("Không xác định được typeAccount của người dùng mục tiêu");
        }

        // Super Admin luôn được phép
        if ("SUPER_ADMIN".equalsIgnoreCase(currentUserTypeAccount)) {
            return;
        }

        // Admin được phép sửa chính mình
        if (currentUserId.equals(targetUserId)) {
            return;
        }

        // Admin không được phép sửa các tài khoản có typeAccount = "admin" hoặc "super_admin"
        if ("ADMIN".equalsIgnoreCase(currentUserTypeAccount)) {
            Set<String> allowedRolesForAdmin = Set.of("SALE", "PRINTER_HOUSE");

            if (allowedRolesForAdmin.contains(targetTypeAccount.toUpperCase())) {
                return;
            } else {
                throw new AccessDeniedException("Admin chỉ được phép thao tác với SALE hoặc PRINTER_HOUSE");
            }
        }

        // Chỉ được phép sửa chính mình
        if (!currentUserId.equals(targetUserId)) {
            throw new AccessDeniedException("Bạn chỉ được phép thao tác với chính mình");
        }
    }
}
