package org.guidewire.login.security;

import jakarta.annotation.PostConstruct;
import org.guidewire.login.model.Permission;
import org.guidewire.login.model.Role;
import org.guidewire.login.model.User;
import org.guidewire.login.repository.PermissionRepository;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository, PermissionRepository permissionRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.permissionRepository = permissionRepository;
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
    public void initializeDefaults() {
        createDefaultPermissions();
        createDefaultAdmin();
        createDefaultRoles();
    }

    public void createDefaultAdmin() {
        logger.info("Checking for default admin user...");
        Optional<User> existingAdmin = userRepository.findByUsername("admin");

        if (existingAdmin.isPresent()) {
            logger.info("Admin user already exists, skipping creation.");
            return;
        }

        logger.info("Creating default admin user...");

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
    private void createDefaultPermissions() {
        logger.info("Checking and creating default permissions...");

        Map<String, String> permissions = new HashMap<>();

        // Task-related permissions
        permissions.put("CREATE_TASK", "Create a new task");
        permissions.put("EDIT_TASK", "Edit an existing task");
        permissions.put("DELETE_TASK", "Delete a task");
        permissions.put("VIEW_TASK", "View task details");
        permissions.put("ASSIGN_TASK", "Assign a task to a user");
        permissions.put("COMMENT_TASK", "Add a comment to a task");
        permissions.put("CLOSE_TASK", "Close a task");
        permissions.put("REOPEN_TASK", "Reopen a closed task");
        permissions.put("ADD_SUBTASK", "Add a subtask to a task");
        permissions.put("REMOVE_SUBTASK", "Remove a subtask from a task");
        // Watcher permissions
        permissions.put("ADD_WATCHER", "Add a watcher to a task");
        permissions.put("REMOVE_WATCHER", "Remove a watcher from a task");
        // Role and permission management (admin)
        permissions.put("CREATE_ROLE", "Create a new role");
        permissions.put("EDIT_ROLE", "Edit an existing role");
        permissions.put("DELETE_ROLE", "Delete a role");
        permissions.put("VIEW_ROLE", "View role details");
        permissions.put("ASSIGN_ROLE", "Assign roles to a user");
        // User management (admin)
        permissions.put("CREATE_USER", "Create a new user");
        permissions.put("EDIT_USER", "Edit existing user details");
        permissions.put("DELETE_USER", "Delete a user");
        permissions.put("VIEW_USER", "View user details");
        // System-level
        permissions.put("VIEW_DASHBOARD", "Access the dashboard view");

        for (Map.Entry<String, String> entry : permissions.entrySet()) {
            permissionRepository.findByName(entry.getKey()).orElseGet(() -> {
                logger.info("➕ Creating permission: {}", entry.getKey());
                return permissionRepository.save(new Permission(entry.getKey(), entry.getValue()));
            });
        }
    }

    private void createDefaultRoles() {
        logger.info("Checking and creating default roles...");
        List<Permission> allPermissions = permissionRepository.findAll();
        Map<String, Set<String>> rolePermissionMap = new HashMap<>();
        rolePermissionMap.put("ADMIN", allPermissions.stream().map(Permission::getName).collect(Collectors.toSet()));
        rolePermissionMap.put("MANAGER", Set.of(
                "CREATE_TASK", "EDIT_TASK", "DELETE_TASK", "VIEW_TASK", "ASSIGN_TASK",
                "COMMENT_TASK", "CLOSE_TASK", "REOPEN_TASK", "ADD_SUBTASK", "REMOVE_SUBTASK",
                "ADD_WATCHER", "REMOVE_WATCHER",
                "VIEW_USER", "VIEW_ROLE"
        ));
        rolePermissionMap.put("USER", Set.of(
                "CREATE_TASK", "EDIT_TASK", "VIEW_TASK", "COMMENT_TASK",
                "ADD_SUBTASK", "REMOVE_SUBTASK"
        ));
        rolePermissionMap.put("WATCHER", Set.of(
                "VIEW_TASK", "COMMENT_TASK"
        ));
        for (Map.Entry<String, Set<String>> entry : rolePermissionMap.entrySet()) {
            String roleName = entry.getKey();
            Set<String> permissionNames = entry.getValue();
            Role role = roleRepository.findByName(roleName).orElseGet(() -> {
                logger.info("Creating role: {}", roleName);
                return new Role(roleName);
            });
            Set<Permission> permissions = allPermissions.stream()
                    .filter(p -> permissionNames.contains(p.getName()))
                    .collect(Collectors.toSet());
            if (!role.getPermissions().equals(permissions)) {
                role.setPermissions(permissions);
                roleRepository.save(role);
                logger.info("Role '{}' created/updated with {} permissions.", roleName, permissions.size());
            }
        }
    }
}
