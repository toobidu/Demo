package org.example.demo.Security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Long userId;

    public JwtAuthenticationToken(Long userId, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userId = userId;
        setAuthenticated(true); // Đánh dấu là đã xác thực
    }

    @Override
    public Object getCredentials() {
        return null; // Không có credentials với JWT
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }
}
