package org.example.demo.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Modal.DTO.Users.PermissionDTO;
import org.example.demo.Service.Interface.IPermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final IPermissionService permissionService;

    @PostMapping
    public ResponseEntity<ApiResponse<PermissionDTO>> createPermission(@Valid @RequestBody PermissionDTO permissionDTO) {
        PermissionDTO created = permissionService.createPermission(permissionDTO);
        return ResponseEntity.ok(ApiResponse.success("Tạo quyền thành công!", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionDTO>> updatePermission(@PathVariable Long id, @Valid @RequestBody PermissionDTO permissionDTO) {
        PermissionDTO updated = permissionService.updatePermission(id, permissionDTO);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật quyền thành công!", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật quyền thành công!", null));

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PermissionDTO>> getPermission(@PathVariable Long id) {
        PermissionDTO permission = permissionService.getPermission(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra quyền!", permission));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PermissionDTO>>> getAllPermissions() {
        List<PermissionDTO> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(ApiResponse.success("Lấy ra danh sách quyền!", permissions));
    }
}
