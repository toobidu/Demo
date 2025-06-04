package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.Users.RoleDTO;
import org.example.demo.Modal.Entity.Users.Role;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleDTO toDTO(Role role);

    Role toEntity(RoleDTO roleDTO);

    List<RoleDTO> toDTOList(List<Role> roles);
}
