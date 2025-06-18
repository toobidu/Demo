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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

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
        log.debug("Path: {}", path);

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
                    log.warn("Invalid token");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ");
                    return;
                }
                Long userId = jwtUtil.getUserIdFromToken(token);
                Collection<SimpleGrantedAuthority> authorities = getAuthoritiesFromRedis(userId);

                UserDetails userDetails = User.withUsername(userId.toString())
                        .password("")
                        .authorities(authorities)
                        .build();

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("User {} is authenticated", userId);
            } catch (Exception e) {
                log.error("Error authenticating user", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token không hợp lệ");
                return;
            }
        } else {
            log.warn("Cannot find Bearer token");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Thiếu token xác thực");
            return;
        }
        filterChain.doFilter(request, response);
    }

    private Collection<SimpleGrantedAuthority> getAuthoritiesFromRedis(Long userId) {
        Set<String> permissions = redisService.getUserPermissions(userId);
        if (permissions == null || permissions.isEmpty()) {
            log.warn("🚫 Người dùng {} không có quyền nào", userId);
            return Collections.emptyList();
        }

        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }
}

