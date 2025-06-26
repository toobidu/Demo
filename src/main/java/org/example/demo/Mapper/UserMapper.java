package org.example.demo.Mapper;

import org.example.demo.Config.PermissionHelper;
import org.example.demo.Modal.DTO.Authentication.RegisterRequest;
import org.example.demo.Modal.DTO.Users.PermissionDTO;
import org.example.demo.Modal.DTO.Users.UserDTO;
import org.example.demo.Modal.Entity.Users.User;
import org.example.demo.Modal.Entity.Users.UserRole;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {PermissionHelper.class})
public interface UserMapper {

    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "permissions", expression = "java(permissionHelper.extractPermissionsFromRoles(user.getUserRoles()))")
    @Mapping(source = "typeAccount", target = "typeAccount")
    @Mapping(source = "rank", target = "rank")
    UserDTO toDTO(User user, @Context PermissionHelper permissionHelper);

    User toEntity(UserDTO userDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "passwordHash", source = "password")
    User toEntity(RegisterRequest registerRequest);
}
