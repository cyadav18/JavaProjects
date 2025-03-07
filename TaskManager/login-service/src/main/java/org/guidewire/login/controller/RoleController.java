package org.guidewire.login.controller;

import org.guidewire.login.dto.requests.RoleRequest;
import org.guidewire.login.dto.responses.RoleResponse;
import org.guidewire.login.exceptions.RoleNotFoundException;
import org.guidewire.login.services.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin/roles")
@PreAuthorize("hasRole('ADMIN')") // Ensures only admins can manage roles
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
    private final RoleService roleService;

    @Autowired
    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Creates a new role with specified permissions.
     *
     * @param request DTO containing role name and associated permissions.
     * @return ResponseEntity with the created role details.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createRole(@RequestBody RoleRequest request) {
        try {
            logger.info("createRole: Received request to create role: {}", request.getName());

            RoleResponse response = roleService.createRole(request);

            logger.info("createRole: Role '{}' created successfully", response.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("createRole: Unexpected error while creating role", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    /**
     * Assigns permissions to an existing role.
     *
     * @param roleId      ID of the role to be updated.
     * @param permissions List of permission names to be assigned.
     * @return ResponseEntity with the updated role details.
     */
    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<?> assignPermissions(@PathVariable Long roleId, @RequestBody List<String> permissions) {
        try {
            logger.info("assignPermissions: Assigning permissions {} to role with ID {}", permissions, roleId);

            RoleResponse response = roleService.assignPermissionsToRole(roleId, permissions);

            logger.info("assignPermissions: Permissions assigned successfully to role '{}'", response.getName());
            return ResponseEntity.ok(response);
        } catch (RoleNotFoundException e) {
            logger.warn("assignPermissions: Role not found with ID: {}", roleId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("assignPermissions: Unexpected error while assigning permissions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
}


