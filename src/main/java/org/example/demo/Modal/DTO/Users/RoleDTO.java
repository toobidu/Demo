package org.example.demo.Modal.DTO.Users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class RoleDTO {
    Long id;
    String name;
    LocalDateTime createdDate;
    LocalDateTime updatedDate;
    List<PermissionDTO> permissions;
}
