package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.PermissionDTO;
import org.example.demo.Modal.Entity.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    PermissionDTO toDTO(Permission permission);
    Permission toEntity(PermissionDTO permissionDTO);
}