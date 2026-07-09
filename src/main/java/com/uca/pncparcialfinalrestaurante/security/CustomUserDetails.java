package com.uca.pncparcialfinalrestaurante.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Wraps an {@link AuthenticatedUser} so it can travel through Spring Security as the request
 * principal (resolved via {@code @AuthenticationPrincipal} in controllers), carrying the acting
 * user's sucursalId/role without an extra repository lookup per request.
 */
public class CustomUserDetails implements UserDetails {

    private final AuthenticatedUser authenticatedUser;

    public CustomUserDetails(AuthenticatedUser authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
    }

    public AuthenticatedUser toAuthenticatedUser() {
        return authenticatedUser;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(authenticatedUser.rol().authority()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return authenticatedUser.email();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
