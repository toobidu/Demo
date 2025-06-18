package org.example.demo.Controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Security.JwtUtil;
import org.example.demo.Config.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/token")
@RequiredArgsConstructor
@Slf4j
public class VerifyTokenController {

    private final JwtUtil jwtUtil;

    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Thiếu token hoặc sai định dạng"));
            }

            String token = authHeader.substring(7); // Bỏ "Bearer "
            log.debug("Received token: {}", token);

            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body(ApiResponse.error("Token không hợp lệ hoặc đã hết hạn"));
            }

            Map<String, Object> claims = jwtUtil.getAllClaimsFromToken(token);

            return ResponseEntity.ok(ApiResponse.success("Token hợp lệ", claims));

        } catch (Exception e) {
            log.error("Error verifying token: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(ApiResponse.error("Đã xảy ra lỗi khi xác minh token"));
        }
    }

    @GetMapping("/test-auth")
    public ResponseEntity<?> testAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(Map.of(
                "authenticated", auth.isAuthenticated(),
                "principal", auth.getPrincipal(),
                "authorities", auth.getAuthorities()
        ));
    }
}
