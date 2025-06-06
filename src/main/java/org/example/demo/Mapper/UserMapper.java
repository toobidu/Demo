package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.UserDTO;
import org.example.demo.Modal.Entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    UserDTO toDTO(User user);
    User toEntity(UserDTO userDTO);
}