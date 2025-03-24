package org.guidewire.login.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        // Extract Authorization Header
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.debug("doFilterInternal: No valid Authorization header found, skipping JWT processing.");
            filterChain.doFilter(request, response);
            return;
        }

        // Extract Token
        String token = authHeader.substring(7);
        String username = null;
        List<String> roles = new ArrayList<>();
        List<String> permissions = new ArrayList<>();

        try {
            username = jwtUtil.extractUsername(token);
            roles = jwtUtil.extractRoles(token);  // Ensure JWT contains roles
            permissions = jwtUtil.extractPermissions(token);  // Ensure JWT contains permissions
            logger.info("doFilterInternal: Extracted username '{}' with roles {} and permissions {}", username, roles, permissions);
        } catch (Exception e) {
            logger.error("doFilterInternal: Invalid JWT token - {}", e.getMessage());
        }

        // Validate token and set authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(token, userDetails)) {
                // Convert roles to Spring Security format
                List<GrantedAuthority> authorities = new ArrayList<>();

                for (String role : roles) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));  // Ensure ROLE_ prefix
                }
                for (String permission : permissions) {
                    authorities.add(new SimpleGrantedAuthority(permission));  // Permissions stay as is
                }

                // Set Authentication in Security Context
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, authorities);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.info("doFilterInternal: Authentication successful for user '{}'. Authorities: {}", username, authorities);
            } else {
                logger.warn("doFilterInternal: JWT validation failed for user '{}'.", username);
            }
        } else {
            logger.debug("doFilterInternal: No authentication context update required.");
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

}
