package org.example.demo.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Modal.DTO.Users.RoleDTO;
import org.example.demo.Service.Interface.IRoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;

    @GetMapping
    @PreAuthorize("hasPermission(null, 'view_roles')")
    public ResponseEntity<ApiResponse<List<RoleDTO>>> getAllRoles() {
        return ResponseEntity.ok(ApiResponse.success("Danh sách vai trò", roleService.getAllRoles()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'view_roles')")
    public ResponseEntity<ApiResponse<RoleDTO>> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Thông tin vai trò", roleService.getRole(id)));
    }

    @PostMapping
    @PreAuthorize("hasPermission(null, 'create_role')")
    public ResponseEntity<ApiResponse<RoleDTO>> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        RoleDTO created = roleService.createRole(roleDTO);
        return ResponseEntity.ok(ApiResponse.success("Tạo role thành công!", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'update_role')")
    public ResponseEntity<ApiResponse<RoleDTO>> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDTO roleDTO) {
        RoleDTO updated = roleService.updateRole(id, roleDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật role thành công!", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'delete_role')")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa role thành công!", null));
    }
}