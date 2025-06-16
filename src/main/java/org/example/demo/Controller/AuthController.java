package org.example.demo.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Config.ApiResponse;
import org.example.demo.Exception.UserFriendlyException;
import org.example.demo.Mapper.UserMapper;
import org.example.demo.Modal.DTO.Authentication.LoginRequest;
import org.example.demo.Modal.DTO.Authentication.LoginResponse;
import org.example.demo.Modal.DTO.Authentication.RegisterRequest;
import org.example.demo.Modal.DTO.Users.UserDTO;
import org.example.demo.Modal.Entity.Authentication.Token;
import org.example.demo.Modal.Entity.Finance.Wallet;
import org.example.demo.Modal.Entity.Users.Role;
import org.example.demo.Modal.Entity.Users.User;
import org.example.demo.Modal.Entity.Users.UserRole;
import org.example.demo.Modal.Entity.Users.UserRoleId;
import org.example.demo.Repository.*;
import org.example.demo.Security.JwtUtil;
import org.example.demo.Service.RedisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final UserMapper userMapper;
    private final WalletRepository walletRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            log.info("Login with username: {}", loginRequest.getUsername());
            
            // 1. Tìm người dùng
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> {
                        log.error("User not found: {}", loginRequest.getUsername());
                        return new UserFriendlyException("Không tìm thấy người dùng!");
                    });
                    
            // 2. Kiểm tra mật khẩu
            if (user.getPasswordHash() == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
                log.warn("Password incorrect for user: {}", loginRequest.getUsername());
                return ResponseEntity.badRequest().body(ApiResponse.error("Thông tin đăng nhập không chính xác!"));
            }

            Set<String> permissions = getUserPermissions(user.getId());
            List<String> permissionsList = new ArrayList<>(permissions);
            // 3. Tạo token
            String accessToken = jwtUtil.generateAccessToken(user.getId(), permissionsList);
            String refreshToken = jwtUtil.generateRefreshToken(user.getId());
            log.debug("Token generated for user: {}", loginRequest.getUsername());

            // 4. Lưu refresh token
            try {
                // Xóa token cũ nếu có
                tokenRepository.findByUserId(user.getId()).ifPresent(tokenRepository::delete);
                
                Token token = new Token();
                token.setUser(user);
                token.setRefreshToken(refreshToken);
                token.setCreatedAt(LocalDateTime.now());
                token.setExpiresAt(LocalDateTime.now().plusSeconds(jwtUtil.getRefreshExpiration() / 1000));
                tokenRepository.save(token);
                log.debug("Refresh token saved for user: {}", loginRequest.getUsername());
            } catch (Exception e) {
                log.error("Refresh token save error: ", e);
                // Tiếp tục xử lý, không throw exception
            }

            // 5. Lưu quyền vào Redis - bỏ qua nếu có lỗi
            try {
                if (permissions != null && !permissions.isEmpty()) {
                    redisService.saveUserPermissions(user.getId(), permissions);
                    log.info("Saved {} permissions for userId: {}", permissions.size(), user.getId());
                } else {
                    log.warn("No permissions found for userId: {}", user.getId());
                }
            } catch (Exception e) {
                log.error("Error saving user permissions: ", e);
                // Tiếp tục xử lý, không throw exception
            }

            // 6. Tạo response
            LoginResponse response = new LoginResponse();
            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshToken);
            log.info("Login successful for user: {}", loginRequest.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công!", response));
        } catch (UserFriendlyException e) {
            log.error("Error logging in: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error logging in: ", e);
            return ResponseEntity.status(500).body(ApiResponse.error("Đã xảy ra lỗi khi đăng nhập, vui lòng thử lại sau!"));
        }
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            log.info("Register with username: {}, email: {}, typeAccount: {}",
                    registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getTypeAccount());

            // Validate input
            if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Username không được để trống!"));
            }
            if (registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Password không được để trống!"));
            }
            if (userRepository.findByUsername(registerRequest.getUsername().trim()).isPresent()) {
                log.warn("Username already exists: {}", registerRequest.getUsername());
                return ResponseEntity.badRequest().body(ApiResponse.error("Tên người dùng đã tồn tại!"));
            }
            if (registerRequest.getEmail() != null &&
                    userRepository.findByEmail(registerRequest.getEmail().trim()).isPresent()) {
                log.warn("Email already exists: {}", registerRequest.getEmail());
                return ResponseEntity.badRequest().body(ApiResponse.error("Email đã tồn tại!"));
            }

            // Check if role exists for typeAccount before creating user
            String typeAccount = registerRequest.getTypeAccount();
            if (typeAccount == null || typeAccount.isEmpty() ||
                    roleRepository.findByRoleName(typeAccount.toUpperCase()).isEmpty()) {
                log.warn("Role not found for typeAccount: {}", typeAccount);
                return ResponseEntity.badRequest().body(ApiResponse.error("Không tìm thấy role tương ứng với typeAccount!"));
            }

            // Encode password and create user entity
            String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
            User user = new User();
            user.setUsername(registerRequest.getUsername().trim());
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setEmail(registerRequest.getEmail() != null ? registerRequest.getEmail().trim() : null);
            user.setAddress(registerRequest.getAddress());
            user.setPhone(registerRequest.getPhone());
            user.setTypeAccount(typeAccount);
            user.setRank("BRONZE");
            user.setPasswordHash(encodedPassword);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            // Save user
            user = userRepository.save(user);
            log.info("Saved user with ID: {}", user.getId());

            // Create wallet if not exists
            if (!walletRepository.existsByUserId(user.getId())) {
                Wallet wallet = new Wallet();
                wallet.setUser(user);
                wallet.setBalance(BigDecimal.ZERO);
                wallet.setCreatedAt(LocalDateTime.now());
                wallet.setUpdatedAt(LocalDateTime.now());
                walletRepository.save(wallet);
                log.info("Created wallet for user ID: {}", user.getId());
            }

            // Assign role
            assignRoleBasedOnTypeAccount(user);
            log.info("Assigned role to user ID: {}", user.getId());

            log.info("Registered user: {}", user.getUsername());
            return ResponseEntity.ok(ApiResponse.success("Đăng kí thành công!", userMapper.toDTO(user)));

        } catch (Exception e) {
            log.error("Error registering: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Lỗi đăng ký: " + e.getMessage()));
        }
    }

    private void assignRoleBasedOnTypeAccount(User user) {
        try {
            String typeAccount = user.getTypeAccount();
            if (typeAccount == null || typeAccount.isEmpty()) {
                log.warn("TypeAccount is null or empty for user ID: {}", user.getId());
                return;
            }

            // Chuyển đổi typeAccount thành roleName (thường là viết hoa)
            String roleName = typeAccount.toUpperCase();
            log.info("Finding role with roleName: {}", roleName);

            // Tìm role tương ứng
            Role role = roleRepository.findByRoleName(roleName)
                    .orElseThrow(() -> {
                        log.error("Role not found with roleName: {}", roleName);
                        return new UserFriendlyException("Không tìm thấy role tương ứng với typeAccount: " + typeAccount);
                    });

            // Tạo UserRole và thiết lập quan hệ
            UserRole userRole = new UserRole();
            UserRoleId userRoleId = new UserRoleId();
            userRoleId.setUserId(user.getId());
            userRoleId.setRoleId(role.getId());
            userRole.setId(userRoleId);
            userRole.setUser(user);
            userRole.setRole(role);

            // Lưu vào database
            userRoleRepository.save(userRole);
            log.info("Đã gán role {} cho user ID: {}", roleName, user.getId());
        } catch (Exception e) {
            log.error("Lỗi khi gán role cho user: ", e);
            throw e; // Re-throw để xử lý ở mức cao hơn
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody Map<String, String> request) {
        try {
            String refreshToken = request.get("refreshToken");
            if (refreshToken == null || refreshToken.isEmpty()) {
                log.warn("Refresh token is missing");
                return ResponseEntity.badRequest().body(ApiResponse.error("Refresh token không được để trống"));
            }

            log.info("Đang xử lý refresh token");

            // Kiểm tra token hợp lệ
            if (!jwtUtil.validateToken(refreshToken)) {
                log.warn("Invalid refresh token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Refresh token không hợp lệ hoặc đã hết hạn"));
            }

            // Lấy userId từ token
            Long userId = jwtUtil.getUserIdFromToken(refreshToken);
            log.info("Refresh token cho userId: {}", userId);

            // Kiểm tra token có trong database không
            Token tokenEntity = tokenRepository.findByRefreshToken(refreshToken)
                    .orElseThrow(() -> {
                        log.warn("Refresh token not found in database");
                        return new UserFriendlyException("Refresh token không tồn tại");
                    });

            // Kiểm tra token đã hết hạn chưa
            if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
                log.warn("Refresh token has expired");
                tokenRepository.delete(tokenEntity);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Refresh token đã hết hạn"));
            }

            // Lấy thông tin user
            User user = tokenEntity.getUser();
            if (user == null) {
                log.warn("User not found for token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Không tìm thấy người dùng"));
            }

            // Tạo token mới
            String newAccessToken = jwtUtil.generateAccessToken(userId, (List<String>) getUserPermissions(userId));
            String newRefreshToken = jwtUtil.generateRefreshToken(userId);
            log.info("Generated new access token: {}, new refresh token: {}", userId);

            // Cập nhật token trong database
            tokenEntity.setRefreshToken(newRefreshToken);
            tokenEntity.setExpiresAt(LocalDateTime.now().plusSeconds(jwtUtil.getRefreshExpiration() / 1000));
            tokenRepository.save(tokenEntity);
            log.info("Updated refresh token for userId: {}", userId);

            // Tạo response
            LoginResponse response = new LoginResponse();
            response.setAccessToken(newAccessToken);
            response.setRefreshToken(newRefreshToken);
            
            return ResponseEntity.ok(ApiResponse.success("Refresh token thành công", response));
        } catch (UserFriendlyException e) {
            log.error("Error refreshing token: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            log.error("Error refreshing token: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Đã xảy ra lỗi khi refresh token"));
        }
    }

    private Set<String> getUserPermissions(Long userId) {
        try {
            log.debug("Fetching permissions for userId: {}", userId);
            Set<String> permissions = userRepository.findPermissionsByUserId(userId);
            if (permissions == null) {
                permissions = new HashSet<>();
            }
            if (permissions.isEmpty()) {
                log.info("No permissions found for userId: {} trong database", userId);
            } else {
                log.info("Found {} permissions for userId: {}", permissions.size(), userId);
            }
            return permissions;
        } catch (Exception e) {
            log.error("Error fetching permissions for userId {}: {}", userId, e.getMessage(), e);
            return new HashSet<>();
        }
    }
}