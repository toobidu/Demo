package org.example.demo.Service.Implement;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.RoleMapper;
import org.example.demo.Modal.DTO.Users.RoleDTO;
import org.example.demo.Modal.Entity.Users.Role;
import org.example.demo.Repository.RoleRepository;
import org.example.demo.Service.Interface.IRoleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImplement implements IRoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleDTO createRole(RoleDTO roleDTO) {
        log.info("Creating role: {}", roleDTO.getRoleName());
        if (roleRepository.findByName(roleDTO.getRoleName()).isPresent()) {
            log.error("Role already exists: {}", roleDTO.getRoleName());
            throw new UserFriendlyException("Role already exists");
        }
        Role role = roleMapper.toEntity(roleDTO);
        role = roleRepository.save(role);
        log.info("Role created with ID: {}", role.getId());
        return roleMapper.toDTO(role);
    }

    @Override
    public RoleDTO updateRole(Long id, RoleDTO roleDTO) {
        log.info("Updating role ID: {}", id);
        Role role = roleRepository.findById(id).orElseThrow(() -> {
            log.error("Role not found: ID {}", id);
            return new UserFriendlyException("Role not found");
        });
        role.setRoleName(roleDTO.getRoleName());
        role.setDescription(roleDTO.getDescription());
        role = roleRepository.save(role);
        log.info("Role updated: ID {}", id);
        return roleMapper.toDTO(role);
    }

    @Override
    public void deleteRole(Long id) {
        log.info("Deleting role ID: {}", id);
        roleRepository.findById(id).orElseThrow(() -> {
            log.error("Role not found: ID {}", id);
            return new UserFriendlyException("Role not found");
        });
        roleRepository.deleteById(id);
        log.info("Role deleted: ID {}", id);
    }

    @Override
    public RoleDTO getRole(Long id) {
        log.info("Retrieving role ID: {}", id);
        Role role = roleRepository.findById(id).orElseThrow(() -> {
            log.error("Role not found: ID {}", id);
            return new UserFriendlyException("Role not found");
        });
        return roleMapper.toDTO(role);
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        log.info("Retrieving all roles");
        return roleRepository.findAll().stream().map(roleMapper::toDTO).collect(Collectors.toList());
    }
}
