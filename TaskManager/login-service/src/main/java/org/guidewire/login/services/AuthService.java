package org.guidewire.login.services;

import org.guidewire.login.dto.requests.LoginRequest;
import org.guidewire.login.dto.requests.LoginResponse;
import org.guidewire.login.dto.requests.SignupRequest;
import org.guidewire.login.dto.responses.UserResponse;
import org.guidewire.login.exceptions.UserNameAlreadyExistsException;
import org.guidewire.login.exceptions.UserNotFoundException;
import org.guidewire.login.model.Role;
import org.guidewire.login.model.User;
import org.guidewire.login.repository.RoleRepository;
import org.guidewire.login.repository.UserRepository;
import org.guidewire.login.security.CustomUserDetails;
import org.guidewire.login.security.JwtUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public boolean isUserAdmin(UserDetails userDetails) {
        if (userDetails == null) return false; // Handle case where token is missing/invalid

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return roles.contains("ADMIN");
    }

    /**
     * Registers a new user and returns a success message.
     */
    public String registerUser(SignupRequest request) {
        logger.info("registerUser:Attempting to register new user: {}", request.getUsername());

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            logger.warn("registerUser:User already exists: {}", request.getUsername());
            throw new UserNameAlreadyExistsException("Username is already taken!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Assign roles
        List<Role> roles = roleRepository.findAllByName(request.getRoles());
        user.setRoles(new HashSet<>(roles));
        userRepository.save(user);

        logger.info("registerUser:User registered successfully: {}", request.getUsername());
        return "User registered successfully!";
    }

    /**
     * Authenticates a user and returns a JWT token.
     */
    public String authenticateUser(LoginRequest request) {
        logger.info("authenticateUser:Authenticating user: {}", request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    logger.error("authenticateUser:User not found: {}", request.getUsername());
                    return new UserNotFoundException("authenticateUser:User not found!");
                });

        UserDetails userDetails = new CustomUserDetails(user);
        String token = jwtUtil.generateToken(userDetails);

        logger.info("authenticateUser:User authenticated successfully: {}", request.getUsername());
        return token;
    }

    /**
     * Fetches user details by username and returns a UserResponse DTO.
     */
    public UserResponse getUserDetails(String username) {
        logger.debug("getUserDetails:Fetching user details for: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("getUserDetails:User not found: {}", username);
                    return new UserNotFoundException("User not found");
                });

        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), extractRoleNames(user));
    }

    /**
     * Authenticates a user and returns login response with JWT and role details.
     */
    public LoginResponse authenticateUserWithDetails(LoginRequest request) {
        logger.info("authenticateUserWithDetails:Authenticating user: {}", request.getUsername());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> {
                    logger.error("authenticateUserWithDetails:User not found: {}", request.getUsername());
                    return new UserNotFoundException("User not found");
                });

        CustomUserDetails userDetails = new CustomUserDetails(user);
        String token = jwtUtil.generateToken(userDetails);

        logger.info("User authenticated successfully with role details: {}", request.getUsername());

        return new LoginResponse(token, user.getUsername(), extractRoleNames(user));
    }

    /**
     * Extracts role names from User entity.
     */
    private Set<String> extractRoleNames(@NotNull User user) {
        return user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    /**
     * Extracts usernames and email from User entity.
     */
    public List<UserResponse> getUserDetails(@NotNull List<UUID> userId) {
//        List<UUID> userIds = userId.stream().map(UUID::fromString).toList();
        List<User> users = userRepository.findAllById(userId);
        return users.stream().map(user -> new UserResponse(user.getId(), user.getUsername(), user.getEmail(), null)).toList();

    }
}
