package org.guidewire.login.security;

import jakarta.annotation.PostConstruct;
import org.guidewire.login.model.Role;
import org.guidewire.login.model.User;
import org.guidewire.login.repository.RoleRepository;
import org.guidewire.login.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Loads a user by username for authentication.
     *
     * @param username The username to search.
     * @return UserDetails object for authentication.
     * @throws UsernameNotFoundException If the user is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("loadUserByUsername: Searching for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("loadUserByUsername: User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        logger.info("loadUserByUsername: User {} found with roles: {}", username, user.getRoles());
        return new CustomUserDetails(user);
    }

    /**
     * Creates a default admin user if none exists.
     * Runs once when the application starts.
     */
    @PostConstruct
    public void createDefaultAdmin() {
        logger.info("Checking for default admin user...");
        Optional<User> existingAdmin = userRepository.findByUsername("admin");

        if (existingAdmin.isPresent()) {
            logger.info("Admin user already exists, skipping creation.");
            return;
        }

        logger.info("🚀 Creating default admin user...");

        // Ensure the ADMIN role exists
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    logger.info("Admin role not found, creating new one...");
                    return roleRepository.save(new Role("ADMIN"));
                });

        // Create and save the default admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setPhoneNumber("NA");
        admin.setPassword(passwordEncoder.encode("admin")); // Encrypt password
        admin.setRoles(Collections.singleton(adminRole));

        userRepository.save(admin);
        logger.info("Default admin user created: username='admin', password='admin'");
    }
}
