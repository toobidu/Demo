package org.example.demo.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.demo.Service.RedisService;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        log.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());

        // Bỏ qua các endpoint không cần xác thực
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/login") || path.startsWith("/api/auth/register") || path.startsWith("/api/auth/refresh") || path.startsWith("/api/role-permissions/add-multiple") || path.startsWith("/api/role-permissions/add") || path.startsWith("/api/role-permissions/remove-multiple") || path.startsWith("/api/role-permissions/remove")) {
            log.debug("Skipping authentication for public endpoint: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        log.debug("Authorization Header: [{}]", header);

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7).trim();
            log.debug("Extracted Token: [{}]", token);

            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);

                // Đặt authentication vào SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(userId));
                log.info("User {} authenticated successfully", userId);
            } else {
                log.warn("Invalid JWT token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        } else {
            log.debug("No Bearer token found");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        filterChain.doFilter(request, response);
    }
}