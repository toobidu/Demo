package org.example.demo.Controller;

import lombok.RequiredArgsConstructor;
import org.example.demo.Modal.DTO.Authentication.RolePermissionRequest;
import org.example.demo.Service.Interface.IRolePermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role-permissions")
@RequiredArgsConstructor
public class RolePermissionController {
    private final IRolePermissionService rolePermissionService;

    @PostMapping("/add")
    public ResponseEntity<?> addPermissionToRole(@RequestParam Long roleId, @RequestParam Long permissionId) {
        rolePermissionService.addPermissionToRole(roleId, permissionId);
        return ResponseEntity.ok("Permission added to role and cache updated!");
    }

    @PostMapping("/remove")
    public ResponseEntity<?> removePermissionFromRole(@RequestParam Long roleId, @RequestParam Long permissionId) {
        rolePermissionService.removePermissionFromRole(roleId, permissionId);
        return ResponseEntity.ok("Permission removed from role and cache updated!");
    }

    @PostMapping("/add-multiple")
    public ResponseEntity<?> addPermissionsToRole(@RequestBody RolePermissionRequest request) {
        rolePermissionService.addMorePermissionsToRole(request.getRoleId(), request.getPermissionIds());
        return ResponseEntity.ok("Permissions added to role and cache updated!");
    }

    @PostMapping("/remove-multiple")
    public ResponseEntity<?> removePermissionsFromRole(@RequestBody RolePermissionRequest request) {
        rolePermissionService.removeMorePermissionsFromRole(request.getRoleId(), request.getPermissionIds());
        return ResponseEntity.ok("Permissions removed from role and cache updated!");
    }
}