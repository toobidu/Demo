package org.example.demo.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Modal.DTO.Users.UserDTO;
import org.example.demo.Service.Interface.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(ApiResponse.success("Người dùng được tạo thành công!", userService.createUser(userDTO)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật người dùng thành công!", userService.updateUser(id, userDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa người dùng thành công!", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Lấy ra danh sách người dùng thành công!", userService.getUser(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsers(
            @RequestParam(name = "type", required = false) String typeAccount,
            @RequestParam(name = "rank", required = false) String rank) {
        List<UserDTO> users = userService.getAllUsers(typeAccount, rank);
        return ResponseEntity.ok(ApiResponse.success("Lấy ra danh sách người dùng thông!", users));
    }

}
