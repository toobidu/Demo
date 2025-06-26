package org.example.demo.Config;

import org.example.demo.Modal.DTO.Users.PermissionDTO;
import org.example.demo.Modal.Entity.Users.UserRole;
import org.example.demo.Service.Interface.IRoleService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PermissionHelper {

    private final IRoleService roleService;

    public PermissionHelper(IRoleService roleService) {
        this.roleService = roleService;
    }

    public List<PermissionDTO> extractPermissionsFromRoles(Set<UserRole> userRoles) {
        if (userRoles == null || userRoles.isEmpty()) return Collections.emptyList();

        Set<Long> roleIds = userRoles.stream()
                .map(ur -> ur.getId().getRoleId())
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Map<Long, List<PermissionDTO>> permissionMap = roleService.getPermissionsByRoleIds(roleIds);

        return roleIds.stream()
                .map(roleId -> permissionMap.getOrDefault(roleId, Collections.emptyList()))
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }
}
