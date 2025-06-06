package org.example.demo.Controller;

import lombok.RequiredArgsConstructor;

import org.example.demo.Config.ApiResponse;
import org.example.demo.Modal.DTO.RegisterRequest;
import org.example.demo.Modal.DTO.UserDTO;
import org.example.demo.Service.Interface.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final IUserService IUserService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@RequestBody RegisterRequest request) {
        UserDTO createdUser = IUserService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User tạo thành công!", createdUser));
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        UserDTO user = IUserService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra user theo id thành công!", user));
    }
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = IUserService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Lấy ra tất cả users thành công", users));
    }
    
    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByRole(@PathVariable String roleName) {
        List<UserDTO> users = IUserService.getUsersByRole(roleName);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra user theo vai trò thành công", users));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = IUserService.updateUser(id, userDTO);
        return ResponseEntity.ok(ApiResponse.success( "Cập nhật user thành công!", updatedUser));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        IUserService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa user thành công!", null));
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> searchUsers(@RequestParam String keyword) {
        List<UserDTO> users = IUserService.searchUsers(keyword);
        return ResponseEntity.ok(ApiResponse.success("Truy vấn tìm user thành công!", users));
    }
}