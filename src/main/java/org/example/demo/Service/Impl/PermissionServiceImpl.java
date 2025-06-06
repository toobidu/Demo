package org.example.demo.Service.Impl;

import lombok.RequiredArgsConstructor;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.PermissionMapper;
import org.example.demo.Modal.DTO.PermissionDTO;
import org.example.demo.Modal.Entity.Permission;
import org.example.demo.Repository.PermissionRepository;
import org.example.demo.Service.Interface.IPermissionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements IPermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public PermissionDTO createPermission(PermissionDTO permissionDTO) {
        // Kiểm tra tên quyền đã tồn tại chưa
        if (permissionRepository.findByName(permissionDTO.getName()).isPresent()) {
            throw new UserFriendlyException("Permission name already exists");
        }
        
        Permission permission = permissionMapper.toEntity(permissionDTO);
        Permission savedPermission = permissionRepository.save(permission);
        return permissionMapper.toDTO(savedPermission);
    }

    @Override
    public PermissionDTO getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Permission not found"));
        return permissionMapper.toDTO(permission);
    }

    @Override
    public PermissionDTO getPermissionByName(String name) {
        Permission permission = permissionRepository.findByName(name)
                .orElseThrow(() -> new UserFriendlyException("Permission not found"));
        return permissionMapper.toDTO(permission);
    }

    @Override
    public List<PermissionDTO> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Set<PermissionDTO> getPermissionsByNames(Set<String> names) {
        return permissionRepository.findByNameIn(names).stream()
                .map(permissionMapper::toDTO)
                .collect(Collectors.toSet());
    }

    @Override
    public PermissionDTO updatePermission(Long id, PermissionDTO permissionDTO) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new UserFriendlyException("Permission not found"));
        
        // Kiểm tra nếu tên quyền thay đổi và đã tồn tại
        if (!permission.getName().equals(permissionDTO.getName()) && 
                permissionRepository.findByName(permissionDTO.getName()).isPresent()) {
            throw new UserFriendlyException("Permission name already exists");
        }
        
        permission.setName(permissionDTO.getName());
        permission.setDescription(permissionDTO.getDescription());
        
        Permission updatedPermission = permissionRepository.save(permission);
        return permissionMapper.toDTO(updatedPermission);
    }

    @Override
    public void deletePermission(Long id) {
        if (!permissionRepository.existsById(id)) {
            throw new UserFriendlyException("Permission not found");
        }
        permissionRepository.deleteById(id);
    }
}