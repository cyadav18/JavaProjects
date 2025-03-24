package org.guidewire.login.services;

import lombok.extern.slf4j.Slf4j;
import org.guidewire.login.dto.responses.UserResponse;
import org.guidewire.login.exceptions.RoleNotFoundException;
import org.guidewire.login.exceptions.UserNotFoundException;
import org.guidewire.login.model.Role;
import org.guidewire.login.model.User;
import org.guidewire.login.repository.RoleRepository;
import org.guidewire.login.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Retrieves a user by username.
     *
     * @param username The username of the user.
     * @return The user details.
     * @throws UserNotFoundException If the user is not found.
     */
    public UserResponse getUserByUsername(String username) {
        logger.info("getUserByUsername: Fetching user with username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("getUserByUsername: User not found with username: {}", username);
                    return new UserNotFoundException("User not found");
                });

        logger.info("getUserByUsername: Successfully retrieved user: {}", username);
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));
    }

    /**
     * Retrieves all users from the system.
     *
     * @return List of all users.
     */
    public List<UserResponse> getAllUsers() {
        logger.info("getAllUsers: Fetching all users from the database.");

        List<User> users = userRepository.findAll();
        logger.info("getAllUsers: Retrieved {} users.", users.size());

        return users.stream()
                .map(user -> new UserResponse(user.getId(), user.getUsername(), user.getEmail(),
                        user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())))
                .toList();
    }

    /**
     * Updates roles for a given user.
     *
     * @param username  The username of the user.
     * @param roleNames The list of roles to assign.
     * @return Updated user details.
     * @throws UserNotFoundException If the user does not exist.
     * @throws RoleNotFoundException If any role does not exist.
     */
    public UserResponse updateUserRoles(String username, Set<String> roleNames) {
        logger.info("updateUserRoles: Updating roles for user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("updateUserRoles: User not found with username: {}", username);
                    return new UserNotFoundException("User not found");
                });

        Set<Role> roles = roleRepository.findAll()
                .stream()
                .filter(role -> roleNames.contains(role.getName()))
                .collect(Collectors.toSet());

//        if (roles.isEmpty()) {
//            logger.warn("updateUserRoles: No valid roles found for provided role names: {}", roleNames);
//            throw new RoleNotFoundException("One or more roles not found");
//        }

        user.setRoles(roles);
        userRepository.save(user);

        logger.info("updateUserRoles: Successfully updated roles for user: {}", username);
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(),
                roles.stream().map(Role::getName).collect(Collectors.toSet()));
    }
}
