package org.guidewire.login.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    /**
     * Generates a signing key from the secret key.
     */
    private @NotNull SecretKey getSigningKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            logger.error("getSigningKey:Error decoding JWT secret key: {}", e.getMessage(), e);
            throw new RuntimeException("Invalid JWT secret key configuration.");
        }
    }

    /**
     * Generates a JWT token for the given user.
     */
    public String generateToken(@NotNull UserDetails userDetails) {
        try {

            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;

//            String token = Jwts.builder()
//                    .setSubject(userDetails.getUsername())
//                    .setIssuedAt(new Date())
//                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
//                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
//                    .compact();

            // Create claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("roles", customUserDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(auth -> !auth.contains("_")) // Assuming roles don’t have underscores
                    .toList());

            claims.put("permissions", customUserDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(auth -> auth.contains("_")) // Assuming permissions have underscores
                    .toList());

// Generate JWT token with correct claims
            String token = Jwts.builder()
                    .setSubject(customUserDetails.getUsername()) // User's identifier (keeps "sub")
                    .addClaims(claims) // Add roles & permissions to JWT without overriding subject
                    .setIssuedAt(new Date()) // Token issue date
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs)) // Expiration
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Sign with secret key
                    .compact();

            logger.info("generateToken:Generated JWT token for user: {}", userDetails.getUsername());
            return token;
        } catch (Exception e) {
            logger.error("generateToken:Error generating JWT token: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate JWT token.");
        }
    }

    /**
     * Extracts roles from the token.
     */
    public List<String> extractRoles(String token) {
        Claims claims = extractClaims(token);
        try {
            return claims.get("roles", List.class);
        } catch (Exception e) {
            logger.error("extractRoles: Failed to extract roles from token: {}", e.getMessage());
            return Collections.emptyList(); // Return empty list if missing
        }
    }

    /**
     * Extracts permissions from the token.
     */
    public List<String> extractPermissions(String token) {
        Claims claims = extractClaims(token);
        try {
            return claims.get("permissions", List.class);
        } catch (Exception e) {
            logger.error("extractPermissions: Failed to extract permissions from token: {}", e.getMessage());
            return Collections.emptyList(); // Return empty list if missing
        }
    }

    /**
     * Extracts the username from the JWT token.
     */
    public String extractUsername(String token) {
        try {
            return extractClaims(token).getSubject();
        } catch (ExpiredJwtException e) {
            logger.warn("extractUsername:JWT has expired: {}", e.getMessage());
            return null; // Handle expired token scenario
        } catch (UnsupportedJwtException e) {
            logger.error("extractUsername:Unsupported JWT token: {}", e.getMessage());
            return null; // Handle unsupported token
        } catch (MalformedJwtException e) {
            logger.error("extractUsername:Malformed JWT token: {}", e.getMessage());
            return null; // Handle malformed token
        } catch (SignatureException e) {
            logger.error("extractUsername:Invalid JWT signature: {}", e.getMessage());
            return null; // Handle incorrect signature
        } catch (IllegalArgumentException e) {
            logger.error("extractUsername:Illegal JWT token: {}", e.getMessage());
            return null; // Handle empty or illegal token
        } catch (Exception e) {
            logger.error("extractUsername:Unexpected error while extracting username from token: {}", e.getMessage(), e);
            return null; // Handle unexpected issues
        }
    }

    /**
     * Validates the token against the provided UserDetails.
     */
    public boolean validateToken(String token, @NotNull UserDetails userDetails) {
        try {
            String extractedUsername = extractUsername(token);
            boolean isValid = extractedUsername.equals(userDetails.getUsername()) && !isTokenExpired(token);

            if (!isValid) {
                logger.warn("validateToken:JWT validation failed for user: {}", userDetails.getUsername());
            } else {
                logger.info("validateToken:JWT validated successfully for user: {}", userDetails.getUsername());
            }

            return isValid;
        } catch (Exception e) {
            logger.error("validateToken:JWT validation error: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Checks if the token is expired.
     */
    private boolean isTokenExpired(String token) {
        try {
            boolean expired = extractClaims(token).getExpiration().before(new Date());
            if (expired) logger.warn("isTokenExpired:JWT token has expired.");
            return expired;
        } catch (Exception e) {
            logger.error("isTokenExpired:Error checking JWT expiration: {}", e.getMessage(), e);
            return true;  // Treat errors as expired
        }
    }

    /**
     * Extracts claims from the token.
     */
    private Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.warn("JWT has expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            logger.error("Malformed JWT: {}", e.getMessage());
            throw e;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.error("Illegal JWT token: {}", e.getMessage());
            throw e;
        }
    }
}
