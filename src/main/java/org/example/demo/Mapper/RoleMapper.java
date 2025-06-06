package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.RoleDTO;
import org.example.demo.Modal.Entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toDTO(Role role);
    Role toEntity(RoleDTO roleDTO);
}