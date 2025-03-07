package org.guidewire.login.security;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public AuthenticationManager authManager(CustomUserDetailsService userDetailsService) {
        logger.info("AuthenticationManager:Initializing AuthenticationManager with DaoAuthenticationProvider");
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("PasswordEncoder:Initializing BCryptPasswordEncoder for password hashing");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        logger.info("SecurityFilterChain:Configuring SecurityFilterChain...");

        http
                .csrf(csrf -> {
                    logger.info("SecurityFilterChain:Disabling CSRF protection");
                    csrf.disable();
                })
                .authorizeHttpRequests(auth -> {
                    logger.info("SecurityFilterChain:Setting up authorization rules...");
                    auth.requestMatchers("/api/auth/**").permitAll();
                    auth.requestMatchers("/api/admin/**").hasRole("ADMIN");
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> {
                    logger.info("SecurityFilterChain:Configuring session management as STATELESS");
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            logger.warn("SecurityFilterChain:Unauthorized request intercepted: {}", request.getRequestURI());
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"error\": \"Unauthorized\"}");
                        })
                );

        logger.info("SecurityFilterChain:Security configuration completed successfully.");
        return http.build();
    }
}



/*

https://chatgpt.com/c/67a9cea5-1740-8010-9100-dcf461848407


* What It Does
Disables CSRF (Cross-Site Request Forgery) protection.
🔹 Why We Need This
CSRF protection is needed for session-based authentication (e.g., forms & cookies).
But in a JWT-based authentication system, we don’t store session cookies.
Since JWT tokens are sent in headers, CSRF attacks are not possible.
✅ Disabling CSRF is safe when using JWT authentication.
*
*  Why Use Stateless Authentication?
* What It Does
Ensures Spring Security does not create sessions.
🔹 Why We Need This
In traditional authentication, Spring Security creates a session for each user.
JWT authentication is stateless (each request includes a token, no sessions).
Setting STATELESS forces the system to never store authentication sessions.
✅ This ensures that authentication works purely based on JWT tokens.
*
* What It Does
Adds JwtAuthenticationFilter before the default UsernamePasswordAuthenticationFilter.
🔹 Why We Need This
UsernamePasswordAuthenticationFilter expects users to log in via form authentication.
We don’t use form authentication, we use JWT tokens instead.
So we need to run our JWT filter before it, so that:
JWT is extracted from the request header.
Token is validated.
User is authenticated based on JWT.
✅ This ensures every request is authenticated using JWT before reaching protected APIs.
*
*  What It Does
Handles unauthorized access (401 errors) and returns JSON instead of an HTML error page.
🔹 Why We Need This
By default, Spring Security redirects to a login page when unauthorized (which is meant for web apps).
In REST APIs, we want a JSON response instead of a redirection.
*
* */