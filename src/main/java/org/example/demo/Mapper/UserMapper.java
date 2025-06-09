package org.example.demo.Mapper;

import org.example.demo.Modal.DTO.Users.UserDTO;
import org.example.demo.Modal.Entity.Users.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toUserDTO(User user);
    User toEntity(UserDTO userDTO);
}
