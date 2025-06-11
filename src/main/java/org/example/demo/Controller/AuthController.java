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
import org.example.demo.Modal.Entity.Users.User;
import org.example.demo.Repository.TokenRepository;
import org.example.demo.Repository.UserRepository;
import org.example.demo.Security.JwtUtil;
import org.example.demo.Service.RedisService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Set;

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

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        User user = userRepository.findByUserName(loginRequest.getUserName())
                .orElseThrow(() -> {
                    log.error("Không tìm thấy người dùng: {}", loginRequest.getUserName());
                    return new UserFriendlyException("Không tìm thấy người dùng!");
                });
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Thông tin đăng nhập không chính xác!"));
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        Token token = new Token();
        token.setUser(user);
        token.setRefreshToken(refreshToken);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusSeconds(jwtUtil.getRefreshExpiration() / 1000));
        tokenRepository.save(token);

        Set<String> permissions = getUserPermissions(user.getId());
        redisService.saveUserPermissions(user.getId(), permissions);
        log.info("Lưu quyền cho người dùng: {}", user.getId());

        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        log.info("Đăng nhập thành công cho người dùng : {}", loginRequest.getUserName());
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công!", response));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody RegisterRequest registerRequest) {

        if (userRepository.findByUserName(registerRequest.getUserName()).isPresent()) {
            log.warn("Tên người dùng đã tồn tại: {}", registerRequest.getUserName());
            return ResponseEntity.badRequest().body(ApiResponse.error("Tên người dùng đã tồn tại!"));
        }

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            log.warn("Email đã tồn tại: {}", registerRequest.getEmail());
            return ResponseEntity.badRequest().body(ApiResponse.error("Email đã tồn tại!"));
        }

        User user = userMapper.toEntity(registerRequest, passwordEncoder);
        user = userRepository.save(user);
        log.info("Người dùng đăng kí thành công: {}", user.getUserName());

        return ResponseEntity.ok(ApiResponse.success("Đăng kí thành công!", userMapper.toDTO(user)));
    }


    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody String refreshToken) {
        log.info("Refresh token");
        Token token = tokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> {
                    log.error("Refresh token không hợp lệ");
                    return new UserFriendlyException("Refresh token không hợp lệ");
                });
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Refresh token expired for userId: {}", token.getUser().getId());
            return ResponseEntity.badRequest().body(ApiResponse.error("Refresh token expired"));
        }

        String accessToken = jwtUtil.generateAccessToken(token.getUser().getId());
        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        log.info("Refresh token cho người dùng: {}", token.getUser().getId());
        return ResponseEntity.ok(ApiResponse.success("Token đã được refresh", response));
    }

    private Set<String> getUserPermissions(Long userId) {
        log.debug("Fetching quyền cho người dùng: {}", userId);
        Set<String> permissions = userRepository.findPermissionsByUserId(userId);
        if (permissions.isEmpty()) {
            log.warn("Không có quyền nào cho người dùng: {} trong database", userId);
        }
        return permissions;
    }
}
