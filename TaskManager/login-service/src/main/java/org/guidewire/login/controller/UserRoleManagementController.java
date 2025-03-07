package org.guidewire.login.controller;

import lombok.extern.slf4j.Slf4j;
import org.guidewire.login.dto.requests.UpdateRolePermissionsRequest;
import org.guidewire.login.dto.requests.UpdateUserRolesRequest;
import org.guidewire.login.dto.responses.RoleResponse;
import org.guidewire.login.dto.responses.UserResponse;
import org.guidewire.login.exceptions.PermissionNotFoundException;
import org.guidewire.login.exceptions.RoleNotFoundException;
import org.guidewire.login.exceptions.UserNotFoundException;
import org.guidewire.login.services.RoleService;
import org.guidewire.login.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // Ensures only Admins can modify roles & permissions
public class UserRoleManagementController {

    private static final Logger logger = LoggerFactory.getLogger(UserRoleManagementController.class);
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserRoleManagementController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    /**
     * Updates roles for a given user.
     *
     * @param username The user's username.
     * @param request  DTO containing list of role names.
     * @return Updated user details with assigned roles.
     */
    @PutMapping("/users/{username}/roles")
    public ResponseEntity<?> updateUserRoles(@PathVariable String username, @RequestBody UpdateUserRolesRequest request) {
        logger.info("updateUserRoles: Received request to update roles for user: {}", username);

        try {
            UserResponse updatedUser = userService.updateUserRoles(username, request.getRole());
            logger.info("updateUserRoles: Successfully updated roles for user: {}", username);
            return ResponseEntity.ok(Map.of("message", "User roles updated successfully", "user", updatedUser));
        } catch (UserNotFoundException e) {
            logger.warn("updateUserRoles: User not found - {}", username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RoleNotFoundException e) {
            logger.warn("updateUserRoles: Role not found - {}", request.getRole());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("updateUserRoles: Unexpected error while updating roles for user: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred"));
        }
    }

    /**
     * Updates permissions for a role.
     *
     * @param roleId  The role ID.
     * @param request DTO containing list of permission names.
     * @return Updated role with assigned permissions.
     */
    @PutMapping("/roles/{roleId}/permissions")
    public ResponseEntity<?> updateRolePermissions(@PathVariable Long roleId, @RequestBody UpdateRolePermissionsRequest request) {
        logger.info("updateRolePermissions: Received request to update permissions for role ID: {}", roleId);

        try {
            RoleResponse updatedRole = roleService.updateRolePermissions(roleId, new HashSet<>(request.getPermissionNames()));
            logger.info("updateRolePermissions: Successfully updated permissions for role ID: {}", roleId);
            return ResponseEntity.ok(Map.of("message", "Role permissions updated successfully", "role", updatedRole));
        } catch (RoleNotFoundException e) {
            logger.warn("updateRolePermissions: Role not found - ID: {}", roleId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (PermissionNotFoundException e) {
            logger.warn("updateRolePermissions: One or more permissions not found - {}", request.getPermissionNames());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("updateRolePermissions: Unexpected error while updating permissions for role ID: {}", roleId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "An unexpected error occurred"));
        }
    }
}
