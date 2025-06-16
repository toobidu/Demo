package org.example.demo.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Modal.DTO.Users.PermissionDTO;
import org.example.demo.Service.Interface.IPermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final IPermissionService permissionService;

    @GetMapping
    @PreAuthorize("hasPermission(null, 'view_permissions')")
    public ResponseEntity<ApiResponse<List<PermissionDTO>>> getAllPermissions() {
        return ResponseEntity.ok(ApiResponse.success("Danh sách quyền", permissionService.getAllPermissions()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'view_permissions')")
    public ResponseEntity<ApiResponse<PermissionDTO>> getPermissionById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Thông tin quyền", permissionService.getPermission(id)));
    }

    @PostMapping
    @PreAuthorize("hasPermission(null, 'manage_permissions')")
    public ResponseEntity<ApiResponse<PermissionDTO>> createPermission(@Valid @RequestBody PermissionDTO permissionDTO) {
        PermissionDTO created = permissionService.createPermission(permissionDTO);
        return ResponseEntity.ok(ApiResponse.success("Tạo quyền thành công!", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'manage_permissions')")
    public ResponseEntity<ApiResponse<PermissionDTO>> updatePermission(@PathVariable Long id, @Valid @RequestBody PermissionDTO permissionDTO) {
        PermissionDTO updated = permissionService.updatePermission(id, permissionDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật quyền thành công!", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'manage_permissions')")
    public ResponseEntity<ApiResponse<Void>> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa quyền thành công!", null));
    }
}