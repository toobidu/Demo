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
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        log.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());

        // Bỏ qua các endpoint không cần xác thực
        String path = request.getRequestURI();
        if (path.startsWith("/api/auth/login") || 
            path.startsWith("/api/auth/register") || 
            path.startsWith("/api/auth/refresh")) {
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
        } else {
            log.debug("No Bearer token found");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getRequiredPermission(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();

        if (path.startsWith("/api/users") && method.equals("POST")) return "create_user";
        if (path.startsWith("/api/users") && method.equals("PUT")) return "update_user";
        if (path.startsWith("/api/users") && method.equals("DELETE")) return "delete_user";
        if (path.startsWith("/api/users") && method.equals("GET")) return "manage_users";
        if (path.startsWith("/api/roles") || path.startsWith("/api/permissions")) return "manage_users";
        if (path.startsWith("/api/products") && method.equals("POST")) return "create_product";
        if (path.startsWith("/api/products") && method.equals("PUT")) return "update_product";
        if (path.startsWith("/api/products") && method.equals("DELETE")) return "delete_product";
        if (path.startsWith("/api/products") && method.equals("GET")) return "manage_products";
        if (path.startsWith("/api/product-prices") || path.startsWith("/api/product_attributes")) return "manage_products";
        if (path.startsWith("/api/orders/admin") && method.equals("GET")) return "view_all_orders";
        if (path.startsWith("/api/orders/print-house") && method.equals("GET")) return "view_print_house_orders";
        if (path.startsWith("/api/orders/user") && method.equals("GET")) return "view_own_orders";
        if (path.startsWith("/api/orders") && method.equals("POST")) return "create_order";
        if (path.startsWith("/api/orders") && method.equals("PUT")) return "update_order";
        if (path.startsWith("/api/orders") && method.equals("DELETE")) return "cancel_order";
        if (path.startsWith("/api/order-items") && method.equals("POST")) return "create_order_item";
        if (path.startsWith("/api/order-items") && method.equals("PUT")) return "update_order_item";
        if (path.startsWith("/api/order-items") && method.equals("DELETE")) return "delete_order_item";
        if (path.startsWith("/api/order-items") && method.equals("GET")) return "view_order_item";
        if (path.startsWith("/api/wallets/deposit") && method.equals("POST")) return "manage_wallets";
        if (path.startsWith("/api/wallets") && method.equals("GET")) return "manage_wallets";
        if (path.startsWith("/api/dictionaries") || path.startsWith("/api/dictionary_items")) return "manage_users";

        return "";
    }
}
