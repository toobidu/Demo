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
        log.info("Adding permission {} to role {}", permissionId, roleId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new UserFriendlyException("Role not found"));
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new UserFriendlyException("Permission not found"));

        if (!canCurrentUserModifyRole(role.getRoleName())) {
            throw new UserFriendlyException("Không có quyền thao tác với role này");
        }

        RolePermissionId id = new RolePermissionId(roleId, permissionId);
        RolePermission rolePermission = new RolePermission();
        rolePermission.setId(id);
        rolePermissionRepository.save(rolePermission);

        updateRedisPermissions(roleId, Set.of(permission.getPermissionName()), true);
    }

    @Override
    @Transactional
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        log.info("Removing permission {} from role {}", permissionId, roleId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new UserFriendlyException("Role not found"));

        if (!canCurrentUserModifyRole(role.getRoleName())) {
            throw new UserFriendlyException("Không có quyền thao tác với role này");
        }

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new UserFriendlyException("Permission not found"));

        RolePermissionId id = new RolePermissionId(roleId, permissionId);
        rolePermissionRepository.deleteById(id);

        updateRedisPermissions(roleId, Set.of(permission.getPermissionName()), false);
    }

    @Override
    @Transactional
    public void addMorePermissionsToRole(Long roleId, List<Long> permissionIds) {
        log.info("Adding multiple permissions to role {}", roleId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new UserFriendlyException("Role not found"));

        if (!canCurrentUserModifyRole(role.getRoleName())) {
            throw new UserFriendlyException("Không có quyền thao tác với role này");
        }

        Set<String> permissionsToAdd = new HashSet<>();

        for (Long permissionId : permissionIds) {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new UserFriendlyException("Permission not found"));

            RolePermissionId id = new RolePermissionId(roleId, permissionId);
            if (!rolePermissionRepository.existsById(id)) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setId(id);
                rolePermissionRepository.save(rolePermission);
                permissionsToAdd.add(permission.getPermissionName());
            }
        }

        if (!permissionsToAdd.isEmpty()) {
            updateRedisPermissions(roleId, permissionsToAdd, true);
        }
    }

    @Override
    @Transactional
    public void removeMorePermissionsFromRole(Long roleId, List<Long> permissionIds) {
        log.info("Removing multiple permissions from role {}", roleId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new UserFriendlyException("Role not found"));

        if (!canCurrentUserModifyRole(role.getRoleName())) {
            throw new UserFriendlyException("Không có quyền thao tác với role này");
        }

        Set<String> permissionsToRemove = new HashSet<>();
        for (Long permissionId : permissionIds) {
            Permission permission = permissionRepository.findById(permissionId)
                    .orElseThrow(() -> new UserFriendlyException("Permission not found"));

            RolePermissionId id = new RolePermissionId(roleId, permissionId);
            rolePermissionRepository.deleteById(id);
            permissionsToRemove.add(permission.getPermissionName());
        }

        if (!permissionsToRemove.isEmpty()) {
            updateRedisPermissions(roleId, permissionsToRemove, false);
        }
    }

    private void updateRedisPermissions(Long roleId, Set<String> permissions, boolean isAdd) {
        var userIds = userRoleRepository.findUserIdsByRoleId(roleId);
        if (userIds != null && !permissions.isEmpty()) {
            for (Long userId : userIds) {
                if (isAdd) {
                    redisService.addPermissions(userId, permissions);
                } else {
                    redisService.removePermissions(userId, permissions);
                }
            }
        }
    }

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

            if ("SUPER_ADMIN".equalsIgnoreCase(userRole)) {
                return true;
            }

            if ("ADMIN".equalsIgnoreCase(userRole)) {
                Set<String> allowedRolesForAdmin = Set.of("SALE", "PRINTER_HOUSE");
                return allowedRolesForAdmin.contains(targetRoleName);
            }

            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}