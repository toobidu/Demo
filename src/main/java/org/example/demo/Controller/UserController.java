package org.example.demo.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Modal.DTO.Users.UserDTO;
import org.example.demo.Service.Interface.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final IUserService userService;

    @GetMapping
    @PreAuthorize("hasPermission(null, 'view_users')")
//    @PreAuthorize("hasAuthority('view_users')")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers(
            @RequestParam(required = false) String typeAccount,
            @RequestParam(required = false) String rank
    ) {
        log.debug("Entered getAllUsers with userId: {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        return ResponseEntity.ok(ApiResponse.success("Danh sách người dùng", userService.getAllUsers(typeAccount, rank)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'view_users')")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Thông tin người dùng", userService.getUser(id)));
    }

    @PostMapping
    @PreAuthorize("hasPermission(null, 'create_user')")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(ApiResponse.success("Người dùng được tạo thành công!", userService.createUser(userDTO)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'update_user')")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật người dùng thành công!", userService.updateUser(id, userDTO)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'delete_user')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa người dùng thành công!", null));
    }
}