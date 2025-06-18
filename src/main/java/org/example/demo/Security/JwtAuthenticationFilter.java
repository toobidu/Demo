package org.example.demo.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Service.RedisService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        String path = request.getRequestURI();
        log.debug("🔓 Path: {}", path);

        // Bỏ qua các endpoint public
        if (path.startsWith("/api/auth") || path.startsWith("/swagger") || path.equals("/error")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization");
        if (token == null) {
            token = request.getHeader("accessToken");
        }

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            try {
                if (!jwtUtil.validateToken(token)) {
                    log.warn("Token không hợp lệ");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ");
                    return;
                }
                Long userId = jwtUtil.getUserIdFromToken(token);
                List<String> permissions = jwtUtil.getPermissionsFromToken(token);
                List<SimpleGrantedAuthority> authorities = permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

                UserDetails userDetails = User.withUsername(userId.toString())
                        .password("")
                        .authorities(authorities)
                        .build();

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Đã xác thực người dùng: {}", userId);
            } catch (Exception e) {
                log.error("Lỗi xác thực JWT: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ");
                return;
            }
        } else {
            log.warn("Không tìm thấy Bearer token");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Thiếu token xác thực");
            return;
        }
        filterChain.doFilter(request, response);
    }
}

