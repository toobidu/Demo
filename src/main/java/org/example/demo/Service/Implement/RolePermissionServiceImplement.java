package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import org.example.demo.Modal.Entity.Users.Permission;
import org.example.demo.Modal.Entity.Users.Role;
import org.example.demo.Modal.Entity.Users.RolePermission;
import org.example.demo.Modal.Entity.Users.RolePermissionId;
import org.example.demo.Repository.PermissionRepository;
import org.example.demo.Repository.RolePermissionRepository;
import org.example.demo.Repository.RoleRepository;
import org.example.demo.Repository.UserRoleRepository;
import org.example.demo.Service.Interface.IRolePermissionService;
import org.example.demo.Service.RedisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImplement implements IRolePermissionService {
    private final RolePermissionRepository rolePermissionRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RedisService redisService;

    @Override
    @Transactional
    public void addPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId).orElseThrow();
        Permission permission = permissionRepository.findById(permissionId).orElseThrow();
        RolePermissionId id = new RolePermissionId(roleId, permissionId);
        RolePermission rolePermission = new RolePermission();
        rolePermission.setId(id);
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);
        rolePermissionRepository.save(rolePermission);

        var userIds = userRoleRepository.findUserIdsByRoleId(roleId);
        if (userIds != null) {
            for (Long userId : userIds) {
                redisService.deleteUserPermissionsCache(userId);
            }
        }
    }

    @Override
    @Transactional
    public void removePermissionFromRole(Long roleId, Long permissionId) {
        RolePermissionId id = new RolePermissionId(roleId, permissionId);
        rolePermissionRepository.deleteById(id);

        var userIds = userRoleRepository.findUserIdsByRoleId(roleId);
        if (userIds != null) {
            for (Long userId : userIds) {
                redisService.deleteUserPermissionsCache(userId);
            }
        }
    }

    @Override
    @Transactional
    public void addMorePermissionsToRole(Long roleId, List<Long> permissionIds) {
        Role role = roleRepository.findById(roleId).orElseThrow();
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
        }
        var userIds = userRoleRepository.findUserIdsByRoleId(roleId);
        if (userIds != null) {
            for (Long userId : userIds) {
                redisService.deleteUserPermissionsCache(userId);
            }
        }
    }

    @Override
    @Transactional
    public void removeMorePermissionsFromRole(Long roleId, List<Long> permissionIds) {
        for (Long permissionId : permissionIds) {
            RolePermissionId id = new RolePermissionId(roleId, permissionId);
            rolePermissionRepository.deleteById(id);
        }
        var userIds = userRoleRepository.findUserIdsByRoleId(roleId);
        if (userIds != null) {
            for (Long userId : userIds) {
                redisService.deleteUserPermissionsCache(userId);
            }
        }
    }
}