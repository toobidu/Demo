package org.example.demo.Modal.DTO.Authentication;

import lombok.Data;

import java.util.List;

@Data
public class RolePermissionRequest {
    private Long roleId;
    private List<Long> permissionIds;
}

