package org.example.demo.Mapper;

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

@Mapper(componentModel = "spring", uses = {PermissionMapper.class})
public interface UserMapper {

    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "permissions", source = "userRoles", qualifiedByName = "mapPermissions")
    @Mapping(source = "typeAccount", target = "typeAccount")
    @Mapping(source = "rank", target = "rank")
    UserDTO toDTO(User user);

    @Named("mapPermissions")
    default List<PermissionDTO> mapPermissions(Set<UserRole> userRoles) {
        if (userRoles == null || userRoles.isEmpty()) {
            return Collections.emptyList();
        }

        return userRoles.stream()
                .filter(ur -> ur.getRole() != null && ur.getRole().getRolePermissions() != null)
                .flatMap(ur -> ur.getRole().getRolePermissions().stream())
                .filter(rp -> rp.getPermission() != null)
                .map(rp -> new PermissionDTO(
                        rp.getPermission().getId(),
                        rp.getPermission().getPermissionName(),
                        rp.getPermission().getDescription()
                ))
                .collect(Collectors.toList());
    }

    User toEntity(UserDTO userDTO);

    @Mapping(target = "passwordHash", expression = "java(passwordEncoder.encode(request.getPassword()))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterRequest request, @Context PasswordEncoder passwordEncoder);

    User toRegister(RegisterRequest registerRequest);

}
