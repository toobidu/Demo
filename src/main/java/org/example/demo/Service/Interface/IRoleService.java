package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.Users.PermissionDTO;
import org.example.demo.Modal.DTO.Users.RoleDTO;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IRoleService {
    RoleDTO createRole(RoleDTO roleDTO);

    RoleDTO updateRole(Long id, RoleDTO roleDTO);

    void deleteRole(Long id);

    RoleDTO getRole(Long id);

    List<RoleDTO> getAllRoles();

    Map<Long, List<PermissionDTO>> getPermissionsByRoleIds(Set<Long> roleIds);
}
