package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Modal.Entity.Users.*;
import org.example.demo.Repository.*;
import org.example.demo.Service.Interface.IRolePermissionService;
import org.example.demo.Service.RedisService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolePermissionServiceImplement implements IRolePermissionService {

    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final RedisService redisService;

    @Override
    @Transactional
    public void addPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId).orElseThrow();
        Permission permission = permissionRepository.findById(permissionId).orElseThrow();

        // ✅ Kiểm tra phân quyền trước khi tiếp tục
        if (!canCurrentUserModifyRole(role.getRoleName())) {
            throw new UserFriendlyException("Không có quyền thao tác với role này");
        }

        RolePermissionId id = new RolePermissionId(roleId, permissionId);
        RolePermission rolePermission = new RolePermission();
        rolePermission.setId(id);
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        rolePermissionRepository.save(rolePermission);

        String permissionName = permission.getPermissionName();
        var userIds = userRoleRepository.findUserIdsByRoleId(roleId);
        if (userIds != null && !userIds.isEmpty()) {
            for (Long userId : userIds) {
                redisService.addPermissions(userId, Set.of(permissionName));
            }
        }
    }

    @Override
    @Transactional
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId).orElseThrow();

        // Kiểm tra phân quyền trước khi xóa
        if (!canCurrentUserModifyRole(role.getRoleName())) {
            throw new UserFriendlyException("Không có quyền thao tác với role này");
        }

        RolePermissionId id = new RolePermissionId(roleId, permissionId);
        rolePermissionRepository.deleteById(id);

        Permission permission = permissionRepository.findById(permissionId).orElseThrow();
        String permissionName = permission.getPermissionName();

        var userIds = userRoleRepository.findUserIdsByRoleId(roleId);
        if (userIds != null && !userIds.isEmpty()) {
            for (Long userId : userIds) {
                redisService.removePermissions(userId, Set.of(permissionName));
            }
        }
    }

    @Override
    @Transactional
    public void addMorePermissionsToRole(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.findById(roleId).orElseThrow();

        // Kiểm tra quyền trước khi thực hiện
        if (!canCurrentUserModifyRole(role.getRoleName())) {
            throw new UserFriendlyException("Không có quyền thao tác với role này");
        }

        Set<String> permissionsToAdd = new HashSet<>();

        for (Long permissionId : permissionIds) {
            Permission permission = permissionRepository.findById(permissionId).orElseThrow();
            RolePermissionId id = new RolePermissionId(roleId, permissionId);
            if (!rolePermissionRepository.existsById(id)) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setId(id);
                rolePermission.setRole(role);
                rolePermission.setPermission(permission);
                rolePermissionRepository.save(rolePermission);
            }
            permissionsToAdd.add(permission.getPermissionName());
        }

        var userIds = userRoleRepository.findUserIdsByRoleId(roleId);
        if (userIds != null && !permissionsToAdd.isEmpty()) {
            for (Long userId : userIds) {
                redisService.addPermissions(userId, permissionsToAdd);
            }
        }
    }

    @Override
    @Transactional
    public void removeMorePermissionsFromRole(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.findById(roleId).orElseThrow();

        // ✅ Kiểm tra phân quyền trước khi xóa nhiều quyền
        if (!canCurrentUserModifyRole(role.getRoleName())) {
            throw new UserFriendlyException("Không có quyền thao tác với role này");
        }

        Set<String> permissionsToRemove = new HashSet<>();
        for (Long permissionId : permissionIds) {
            Permission permission = permissionRepository.findById(permissionId).orElseThrow();
            RolePermissionId id = new RolePermissionId(roleId, permissionId);
            rolePermissionRepository.deleteById(id);
            permissionsToRemove.add(permission.getPermissionName());
        }

        var userIds = userRoleRepository.findUserIdsByRoleId(roleId);
        if (userIds != null && !permissionsToRemove.isEmpty()) {
            for (Long userId : userIds) {
                redisService.removePermissions(userId, permissionsToRemove);
            }
        }
    }

    // Hàm kiểm tra phân quyền người dùng hiện tại
    private boolean canCurrentUserModifyRole(String targetRoleName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetails userDetails)) {
            return false;
        }

        String username = userDetails.getUsername();
        try {
            Long userId = Long.valueOf(username);
            String userRole = userRepository.findById(userId)
                    .map(User::getTypeAccount)
                    .map(String::toUpperCase)
                    .orElse("");

            targetRoleName = targetRoleName.toUpperCase();

            //Super admin có toàn quyền
            if ("SUPER_ADMIN".equalsIgnoreCase(userRole)) {
                return true;
            }

            //Admin chỉ được thao tác với SALE & PRINTER_HOUSE
            if ("ADMIN".equalsIgnoreCase(userRole)) {
                Set<String> allowedRolesForAdmin = Set.of("SALE", "PRINTER_HOUSE");

                return allowedRolesForAdmin.contains(targetRoleName);
            }

            //Người dùng thường không được phép thao tác
            return false;

        } catch (NumberFormatException e) {
            return false;
        }
    }
}