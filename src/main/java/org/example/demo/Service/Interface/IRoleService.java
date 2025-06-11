package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.Users.RoleDTO;

import java.util.List;

public interface IRoleService {
    RoleDTO createRole(RoleDTO roleDTO);

    RoleDTO updateRole(Long id, RoleDTO roleDTO);

    void deleteRole(Long id);

    RoleDTO getRole(Long id);

    List<RoleDTO> getAllRoles();
}
