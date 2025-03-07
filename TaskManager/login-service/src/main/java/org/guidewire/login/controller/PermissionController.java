package org.guidewire.login.controller;

import org.guidewire.login.dto.requests.PermissionRequest;
import org.guidewire.login.dto.responses.PermissionResponse;
import org.guidewire.login.exceptions.PermissionNameAlreadyExistsException;
import org.guidewire.login.services.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/permissions")
@PreAuthorize("hasRole('ADMIN')") // Only Admins can manage permissions
public class PermissionController {

    private static final Logger logger = LoggerFactory.getLogger(PermissionController.class);
    private final PermissionService permissionService;

    @Autowired
    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * Creates a new permission.
     *
     * @param request DTO containing permission name.
     * @return ResponseEntity with the created permission details.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPermission(@RequestBody PermissionRequest request) {
        try {
            logger.info("createPermission:Received request to create permission: {}", request.getName());

            PermissionResponse response = permissionService.createPermission(request.getName());

            logger.info("createPermission:Permission created successfully: {}", response.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (PermissionNameAlreadyExistsException e) {
            logger.warn("createPermission:Permission creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            logger.error("createPermission:Unexpected error while creating permission", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    /**
     * Retrieves all permissions.
     *
     * @return List of all permissions.
     */
    @GetMapping("/all")
    public ResponseEntity<List<PermissionResponse>> getAllPermissions() {
        logger.info("getAllPermissions:Fetching all permissions...");
        List<PermissionResponse> permissions = permissionService.getAllPermissions();
        logger.info("getAllPermissions:Retrieved {} permissions", permissions.size());
        return ResponseEntity.ok(permissions);
    }

    /**
     * Deletes a permission by ID.
     *
     * @param id ID of the permission to delete.
     * @return ResponseEntity with deletion status.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePermission(@PathVariable Long id) {
        try {
            logger.info("deletePermission:Received request to delete permission with ID: {}", id);

            permissionService.deletePermission(id);

            logger.info("deletePermission:Permission with ID {} deleted successfully", id);
            return ResponseEntity.ok("Permission deleted successfully.");
        } catch (PermissionNameAlreadyExistsException e) {
            logger.warn("deletePermission:Permission creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            logger.error("deletePermission:Error deleting permission with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while deleting permission.");
        }
    }
}
