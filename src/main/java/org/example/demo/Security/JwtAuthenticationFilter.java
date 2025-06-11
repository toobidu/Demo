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
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                String requiredPermission = getRequiredPermission(request);

                if (!requiredPermission.isEmpty() && !redisService.hasAuth2(userId, requiredPermission)) {
                    log.warn("User {} lacks permission: {}", userId, requiredPermission);
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Insufficient permissions");
                    return;
                }
                SecurityContextHolder.getContext().setAuthentication(new JwtAuthenticationToken(userId));
                log.info("User {} authorized for request", userId);
            } else {
                log.warn("Invalid JWT token");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getRequiredPermission(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        if (path.startsWith("/api/users")) return "manage_users";
        if (path.startsWith("/api/products")) return "manage_products";
        if (path.startsWith("/api/orders/admin")) return "view_all_orders";
        if (path.startsWith("/api/orders/print-house")) return "view_print_house_orders";
        if (path.startsWith("/api/orders/user")) return "view_own_orders";
        if (path.startsWith("/api/orders") && method.equals("POST")) return "create_order";
        if (path.startsWith("/api/orders") && method.equals("PUT")) return "update_order";
        if (path.startsWith("/api/orders") && method.equals("DELETE")) return "cancel_order";
        if (path.startsWith("/api/wallets/deposit")) return "manage_users";
        return "";
    }
}
