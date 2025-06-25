package org.example.demo.Controller;

import lombok.RequiredArgsConstructor;
import org.example.demo.Modal.DTO.Authentication.RolePermissionRequest;
import org.example.demo.Service.Interface.IRolePermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role-permissions")
@RequiredArgsConstructor
public class RolePermissionController {
    private final IRolePermissionService rolePermissionService;

    @PostMapping("/add")
    @PreAuthorize("hasPermission(null, 'add_role_permission')")
    public ResponseEntity<?> addPermissionToRole(@RequestParam Long roleId, @RequestParam Long permissionId) {
        rolePermissionService.addPermissionToRole(roleId, permissionId);
        return ResponseEntity.ok("Quyền đã được gán cho vai trò thành công và lưu vào Redis!");
    }

    @PostMapping("/remove")
    @PreAuthorize("hasPermission(null, 'remove_role_permission')")
    public ResponseEntity<?> removePermissionFromRole(@RequestParam Long roleId, @RequestParam Long permissionId) {
        rolePermissionService.removePermissionFromRole(roleId, permissionId);
        return ResponseEntity.ok("Quyền đã được xóa thành công và lưu vào Redis !");
    }

    @PostMapping("/add-multiple")
    @PreAuthorize("hasPermission(null, 'add_role_permission')")
    public ResponseEntity<?> addPermissionsToRole(@RequestBody RolePermissionRequest request) {
        rolePermissionService.addMorePermissionsToRole(request.getRoleId(), request.getPermissionIds());
        return ResponseEntity.ok("Các quyền được gán cho vài trò thành công và lưu vào Redis!");
    }

    @PostMapping("/remove-multiple")
    @PreAuthorize("hasPermission(null, 'remove_role_permission')")
    public ResponseEntity<?> removePermissionsFromRole(@RequestBody RolePermissionRequest request) {
        rolePermissionService.removeMorePermissionsFromRole(request.getRoleId(), request.getPermissionIds());
        return ResponseEntity.ok("Các quyền được xóa thành công và lưu vào Redis!");
    }
}