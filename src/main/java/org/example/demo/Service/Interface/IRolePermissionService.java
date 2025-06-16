package org.example.demo.Service.Interface;

import java.util.List;

public interface IRolePermissionService {
    void addPermissionToRole(Long roleId, Long permissionId);

    void removePermissionFromRole(Long roleId, Long permissionId);

    void addMorePermissionsToRole(Long roleId, List<Long> permissionIds);

    void removeMorePermissionsFromRole(Long roleId, List<Long> permissionIds);
}
