package org.guidewire.login.controller;

import org.guidewire.login.dto.requests.UpdateUserRolesRequest;
import org.guidewire.login.dto.responses.UserResponse;
import org.guidewire.login.exceptions.UserNotFoundException;
import org.guidewire.login.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth/users")
public class UserController {


    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Assigns or updates roles for a user.
     *
     * @param username The username of the user whose roles need to be updated.
     * @param request  The DTO containing new role names.
     * @return ResponseEntity with updated user details.
     */
    @PutMapping("/{username}/roles")
    public ResponseEntity<?> updateUserRoles(@PathVariable String username, @RequestBody UpdateUserRolesRequest request) {
        try {
            logger.info("updateUserRoles: Updating roles for user '{}'", username);

            UserResponse response = userService.updateUserRoles(username, request.getRole());

            logger.info("updateUserRoles: Roles updated successfully for user '{}'", username);
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            logger.warn("updateUserRoles: User '{}' not found", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("updateUserRoles: Unexpected error while updating user roles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(UUID.fromString(id)));
    }

}
