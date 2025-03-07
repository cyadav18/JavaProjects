package org.guidewire.login.services;

import org.guidewire.login.dto.responses.PermissionResponse;
import org.guidewire.login.exceptions.PermissionNameAlreadyExistsException;
import org.guidewire.login.model.Permission;
import org.guidewire.login.repository.PermissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionService {

    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);
    private final PermissionRepository permissionRepository;

    @Autowired
    public PermissionService(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    /**
     * Creates a new permission if it does not already exist.
     *
     * @param name Name of the permission
     * @return PermissionResponse containing the created permission details.
     */
    public PermissionResponse createPermission(String name) {
        logger.info("Attempting to create permission with name: {}", name);

        if (permissionRepository.findByName(name).isPresent()) {
            logger.warn("createPermission:Permission with name {} already exists", name);
            throw new PermissionNameAlreadyExistsException("Permission with name " + name + " already exists");
        }

        Permission permission = new Permission(name);
        Permission savedPermission = permissionRepository.save(permission);

        logger.info("createPermission:Permission created successfully with ID: {}", savedPermission.getId());
        return new PermissionResponse(savedPermission.getId(), savedPermission.getName(), "Permission created successfully.");
    }

    /**
     * Retrieves all permissions.
     *
     * @return List of PermissionResponse DTOs
     */
    public List<PermissionResponse> getAllPermissions() {
        logger.info("getAllPermissions:Fetching all permissions");
        List<Permission> permissions = permissionRepository.findAll();

        logger.info("getAllPermissions:Retrieved {} permissions", permissions.size());
        return permissions.stream()
                .map(permission -> new PermissionResponse(permission.getId(), permission.getName(), ""))
                .collect(Collectors.toList());
    }

    /**
     * Deletes a permission by ID.
     *
     * @param id ID of the permission to delete
     */
    public void deletePermission(Long id) {
        logger.info("deletePermission:Deleting permission with ID: {}", id);

        if (!permissionRepository.existsById(id)) {
            logger.warn("deletePermission:Permission with ID {} not found, skipping delete", id);
            throw new PermissionNameAlreadyExistsException("Permission with ID " + id + " already exists");
        }

        permissionRepository.deleteById(id);
        logger.info("deletePermission:Permission with ID {} deleted successfully", id);
    }
}
