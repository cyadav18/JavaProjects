package org.guidewire.login.security;

import org.guidewire.login.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetails.class);
    private final String username;
    private final String password;
    private final Set<GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = new HashSet<>();

        logger.info("Initializing CustomUserDetails for user: {}", username);

        /*
         * Example Data for Authorities:
         * Roles Table:
         *   - ADMIN
         *   - USER
         * Permissions Table:
         *   - CREATE_TASK
         *   - DELETE_TASK
         *   - VIEW_TASK
         *
         * Authority Mapping Example:
         * Role: ADMIN  → Authorities: ["ADMIN", "CREATE_TASK", "DELETE_TASK"]
         * Role: USER   → Authorities: ["USER", "VIEW_TASK"]
         */
        Set<String> roles = user.getRoles().stream()
//                .map(role -> "ROLE_" + role.getName().toUpperCase())
                .map(role -> role.getName().toUpperCase())
                .collect(Collectors.toSet());

        logger.debug("User {} has roles: {}", username, roles);

        this.authorities.addAll(roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet()));

        // Add permissions directly
        Set<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getName().toUpperCase())
                .collect(Collectors.toSet());

        logger.debug("User {} has permissions: {}", username, permissions);

        this.authorities.addAll(permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet()));

        logger.info("CustomUserDetails initialized successfully for user: {}", username);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        logger.debug("Fetching authorities for user: {}", username);
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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

    public Set<String> getRoles() {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> !auth.contains("_")) // Assuming roles don’t have underscores
                .collect(Collectors.toSet());
    }

    public Set<String> getPermissions() {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .filter(auth -> auth.contains("_")) // Assuming permissions have underscores (e.g., CREATE_TASK)
                .collect(Collectors.toSet());
    }

}
