package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.PermissionMapper;
import org.example.demo.Modal.DTO.Users.PermissionDTO;
import org.example.demo.Modal.Entity.Users.Permission;
import org.example.demo.Repository.PermissionRepository;
import org.example.demo.Service.Interface.IPermissionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class PermissionServiceImplement implements IPermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public PermissionDTO createPermission(PermissionDTO permissionDTO) {
        log.info("Creating permission: {}", permissionDTO.getPermissionName());
        if (permissionRepository.findByPermissionName(permissionDTO.getPermissionName()).isPresent()) {
            log.error("Permission already exists: {}", permissionDTO.getPermissionName());
            throw new UserFriendlyException("Permission already exists");
        }
        Permission permission = permissionMapper.toEntity(permissionDTO);
        permission = permissionRepository.save(permission);
        log.info("Permission created with ID: {}", permission.getId());
        return permissionMapper.toDTO(permission);
    }

    @Override
    public PermissionDTO updatePermission(Long id, PermissionDTO permissionDTO) {
        log.info("Updating permission ID: {}", id);
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Permission not found: ID {}", id);
                    return new UserFriendlyException("Permission not found");
                });
        permission.setPermissionName(permissionDTO.getPermissionName());
        permission.setDescription(permissionDTO.getDescription());
        permission = permissionRepository.save(permission);
        log.info("Permission updated: ID {}", id);
        return permissionMapper.toDTO(permission);
    }

    @Override
    public void deletePermission(Long id) {
        log.info("Deleting permission ID: {}", id);
        permissionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Permission not found: ID {}", id);
                    return new UserFriendlyException("Permission not found");
                });
        permissionRepository.deleteById(id);
        log.info("Permission deleted: ID {}", id);
    }

    @Override
    public PermissionDTO getPermission(Long id) {
        log.info("Retrieving permission ID: {}", id);
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Permission not found: ID {}", id);
                    return new UserFriendlyException("Permission not found");
                });
        return permissionMapper.toDTO(permission);
    }

    @Override
    public List<PermissionDTO> getAllPermissions() {
        log.info("Retrieving all permissions");
        return permissionRepository.findAll().stream()
                .map(permissionMapper::toDTO)
                .collect(Collectors.toList());
    }
}
