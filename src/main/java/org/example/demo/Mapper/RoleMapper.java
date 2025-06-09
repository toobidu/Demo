package org.example.demo.Mapper;

import org.example.demo.Modal.Entity.Users.Role;
import org.example.demo.Modal.DTO.Users.RoleDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toRoleDTO(Role role);
    Role toEntity(RoleDTO roleDTO);
}
