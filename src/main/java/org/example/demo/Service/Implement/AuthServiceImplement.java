package org.example.demo.Service.Implement;

import jakarta.transaction.Transactional;
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
import org.example.demo.Service.Interface.IAuthService;
import org.example.demo.Service.RedisService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImplement implements IAuthService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RedisService redisService;
    private final UserMapper userMapper;
    private final WalletRepository walletRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    @Override
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login with username: {}", loginRequest.getUsername());

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UserFriendlyException("Không tìm thấy người dùng!"));

        if (user.getPasswordHash() == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Thông tin đăng nhập không chính xác!"));
        }

        // ✅ Lấy quyền từ DB và lưu vào Redis
        Set<String> permissions = getUserPermissions(user.getId());
        saveUserPermissionsToRedis(user.getId(), permissions);

        // ❌ Không còn đưa quyền vào token
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getTypeAccount(), user.getRank());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // ✅ Lưu refresh token vào DB
        saveRefreshToken(user, refreshToken);

        // ✅ Tạo response trả về client
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);

        log.info("Login successful for user: {}", loginRequest.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công!", response));
    }

    private void saveRefreshToken(User user, String refreshToken) {
        tokenRepository.findByUserId(user.getId()).ifPresent(tokenRepository::delete);

        Token token = new Token();
        token.setUser(user);
        token.setRefreshToken(refreshToken);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusSeconds(jwtUtil.getRefreshExpiration() / 1000));
        tokenRepository.save(token);
        log.debug("Refresh token saved for user: {}", user.getUsername());
    }

    private void saveUserPermissionsToRedis(Long userId, Set<String> permissions) {
        try {
            if (permissions != null && !permissions.isEmpty()) {
                redisService.saveUserPermissions(userId, permissions);
                log.info("Saved {} permissions for userId: {}", permissions.size(), userId);
            } else {
                log.warn("No permissions found for userId: {}", userId);
            }
        } catch (Exception e) {
            log.error("Error saving user permissions: ", e);
        }
    }

    @Transactional
    @Override
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        validateRegisterRequest(registerRequest);

        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        User user = userMapper.toRegister(registerRequest);
        user.setPasswordHash(encodedPassword);
        user.setTypeAccount(registerRequest.getTypeAccount());
        user.setRank("BRONZE");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        user = userRepository.save(user);
        createWalletIfNotExists(user);
        assignRoleBasedOnTypeAccount(user);

        log.info("Registered user: {}", user.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Đăng kí thành công!", userMapper.toDTO(user)));
    }

    private void validateRegisterRequest(RegisterRequest registerRequest) {
        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
            throw new UserFriendlyException("Username không được để trống!");
        }
        if (registerRequest.getPassword() == null || registerRequest.getPassword().trim().isEmpty()) {
            throw new UserFriendlyException("Password không được để trống!");
        }
        if (userRepository.findByUsername(registerRequest.getUsername().trim()).isPresent()) {
            throw new UserFriendlyException("Tên người dùng đã tồn tại!");
        }
        if (registerRequest.getEmail() != null &&
                userRepository.findByEmail(registerRequest.getEmail().trim()).isPresent()) {
            throw new UserFriendlyException("Email đã tồn tại!");
        }

        String typeAccount = registerRequest.getTypeAccount();
        if (typeAccount == null || typeAccount.isEmpty() ||
                roleRepository.findByRoleName(typeAccount.toUpperCase()).isEmpty()) {
            throw new UserFriendlyException("Không tìm thấy role tương ứng với typeAccount!");
        }
    }

    private void createWalletIfNotExists(User user) {
        if (!walletRepository.existsByUserId(user.getId())) {
            Wallet wallet = new Wallet();
            wallet.setUser(user);
            wallet.setBalance(BigDecimal.ZERO);
            wallet.setCreatedAt(LocalDateTime.now());
            wallet.setUpdatedAt(LocalDateTime.now());
            walletRepository.save(wallet);
            log.info("Created wallet for user ID: {}", user.getId());
        }
    }

    private void assignRoleBasedOnTypeAccount(User user) {
        String typeAccount = user.getTypeAccount();
        if (typeAccount == null || typeAccount.isEmpty()) {
            log.warn("TypeAccount is null or empty for user ID: {}", user.getId());
            return;
        }

        String roleName = typeAccount.toUpperCase();
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new UserFriendlyException("Không tìm thấy role tương ứng với typeAccount"));

        UserRole userRole = new UserRole();
        UserRoleId userRoleId = new UserRoleId();
        userRoleId.setUserId(user.getId());
        userRoleId.setRoleId(role.getId());
        userRole.setId(userRoleId);
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);
        log.info("Assigned role {} to user ID: {}", roleName, user.getId());
    }

    @Override
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Refresh token không được để trống"));
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Refresh token không hợp lệ hoặc đã hết hạn"));
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        Token tokenEntity = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new UserFriendlyException("Refresh token không tồn tại"));

        if (tokenEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(tokenEntity);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Refresh token đã hết hạn"));
        }

        User user = tokenEntity.getUser();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Không tìm thấy người dùng"));
        }

        // ✅ Lấy quyền mới nhất từ DB và tạo token mới
        Set<String> permissions = getUserPermissions(userId);
        saveUserPermissionsToRedis(userId, permissions); // Cập nhật Redis nếu cần

        String newAccessToken = jwtUtil.generateAccessToken(userId, user.getTypeAccount(), user.getRank());
        String newRefreshToken = jwtUtil.generateRefreshToken(userId);

        tokenEntity.setRefreshToken(newRefreshToken);
        tokenEntity.setExpiresAt(LocalDateTime.now().plusSeconds(jwtUtil.getRefreshExpiration() / 1000));
        tokenRepository.save(tokenEntity);
        log.info("Updated refresh token for userId: {}", userId);

        LoginResponse response = new LoginResponse();
        response.setAccessToken(newAccessToken);
        response.setRefreshToken(newRefreshToken);
        return ResponseEntity.ok(ApiResponse.success("Refresh token thành công", response));
    }

    private Set<String> getUserPermissions(Long userId) {
        Set<String> permissions = userRepository.findPermissionsByUserId(userId);
        if (permissions == null) {
            permissions = new HashSet<>();
        }
        log.info("Found {} permissions for userId: {}", permissions.size(), userId);
        return permissions;
    }
}
