package org.example.demo.Service.Interface;

import org.example.demo.Modal.DTO.PermissionDTO;

import java.util.List;
import java.util.Set;

public interface IPermissionService {
    PermissionDTO createPermission(PermissionDTO permissionDTO);
    PermissionDTO getPermissionById(Long id);
    PermissionDTO getPermissionByName(String name);
    List<PermissionDTO> getAllPermissions();
    Set<PermissionDTO> getPermissionsByNames(Set<String> names);
    PermissionDTO updatePermission(Long id, PermissionDTO permissionDTO);
    void deletePermission(Long id);
}