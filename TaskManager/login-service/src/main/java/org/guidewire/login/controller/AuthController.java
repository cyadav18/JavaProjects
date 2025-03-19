package org.guidewire.login.controller;

import org.guidewire.login.dto.requests.LoginRequest;
import org.guidewire.login.dto.requests.SignupRequest;
import org.guidewire.login.dto.responses.AuthResponse;
import org.guidewire.login.dto.responses.UserResponse;
import org.guidewire.login.dto.responses.UserRoleResponse;
import org.guidewire.login.exceptions.UserNameAlreadyExistsException;
import org.guidewire.login.exceptions.UserNotFoundException;
import org.guidewire.login.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user.
     *
     * @param request Signup request DTO containing user details.
     * @return ResponseEntity with success message or error.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest request) {
        try {
            logger.info("registerUser: Received request to register user: {}", request.getUsername());

            String response = authService.registerUser(request);

            logger.info("registerUser: User {} registered successfully", request.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("success", response));
        } catch (UserNameAlreadyExistsException e) {
            logger.warn("registerUser: Registration failed - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("registerUser: Unexpected error while registering user", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error occurred"));
        }
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request Login request DTO.
     * @return ResponseEntity containing JWT token or error.
     */
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        try {
            logger.info("loginUser: Login attempt for user: {}", request.getUsername());

            String jwtToken = authService.authenticateUser(request);

            logger.info("loginUser: User {} logged in successfully", request.getUsername());
            return ResponseEntity.ok(new AuthResponse(jwtToken, "Login successful"));
        } catch (UserNotFoundException e) {
            logger.warn("loginUser: Login failed - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid username or password"));
        } catch (Exception e) {
            logger.error("loginUser: Unexpected error during login", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error occurred"));
        }
    }

    /**
     * Retrieves details of the currently logged-in user.
     *
     * @param principal Authenticated user.
     * @return ResponseEntity containing user details.
     */
    @GetMapping("/current/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        try {
            logger.info("getCurrentUser: Fetching details for user: {}", principal.getName());

            UserResponse user = authService.getUserDetails(principal.getName());

            logger.info("getCurrentUser: Returning user details for {}", principal.getName());
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            logger.warn("getCurrentUser: User not found - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("getCurrentUser: Unexpected error while fetching user details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Unexpected error occurred"));
        }
    }

    /**
     * API to check if the logged-in user is an Admin
     */
    @GetMapping("/check-admin")
    public ResponseEntity<UserRoleResponse> checkIfAdmin(@AuthenticationPrincipal UserDetails userDetails) {
        boolean isAdmin = authService.isUserAdmin(userDetails);
        return ResponseEntity.ok(new UserRoleResponse(userDetails.getUsername(), isAdmin));
    }

    @GetMapping("/user/details")
    public ResponseEntity<?> fetchUserDetails(@RequestBody List<UUID> userIds) {
        try {
            logger.info("fetchUserDetails: Fetching details for users with IDs: {}", userIds);

            List<UserResponse> users = authService.getUserDetails(userIds);

            logger.info("fetchUserDetails: Returning user details for users: {}", users);
            return ResponseEntity.ok(users);
        } catch (UserNotFoundException e) {
            logger.warn("fetchUserDetails: User not found - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("fetchUserDetails: Unexpected error while fetching user details", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Unexpected error occurred"));
        }
    }
}
