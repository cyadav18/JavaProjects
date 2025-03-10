package org.guidewire.login.services;

import org.guidewire.login.dto.requests.RoleRequest;
import org.guidewire.login.dto.responses.RoleResponse;
import org.guidewire.login.exceptions.RoleNotFoundException;
import org.guidewire.login.model.Permission;
import org.guidewire.login.model.Role;
import org.guidewire.login.repository.PermissionRepository;
import org.guidewire.login.repository.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class RoleService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }

    /**
     * Creates a new role with specified permissions.
     *
     * @param request DTO containing role name and associated permissions.
     * @return RoleResponse DTO with role details.
     */
    public RoleResponse createRole(RoleRequest request) {
        logger.info("createRole: Received request to create role: {}", request.getName());

        Role role = new Role();
        role.setName(request.getName());

        Set<Permission> permissions = fetchPermissions(request.getPermissions());

        role.setPermissions(permissions);
        role = roleRepository.save(role);

        logger.info("createRole: Role '{}' created successfully", request.getName());
        return new RoleResponse(role.getId(), role.getName(), role.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet()));
    }

    /**
     * Assigns new permissions to an existing role.
     *
     * @param roleId          ID of the role to be updated.
     * @param permissionNames List of permission names to be assigned.
     * @return RoleResponse DTO with updated role details.
     */
    public RoleResponse assignPermissionsToRole(Long roleId, List<String> permissionNames) {
        logger.info("assignPermissionsToRole: Assigning permissions {} to role with ID {}", permissionNames, roleId);

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> {
                    logger.warn("assignPermissionsToRole: Role with ID {} not found", roleId);
                    return new RoleNotFoundException("Role not found with ID: " + roleId);
                });

        Set<Permission> permissions = fetchPermissions(permissionNames);

        role.getPermissions().addAll(permissions);
        role = roleRepository.save(role);

        logger.info("assignPermissionsToRole: Permissions added successfully to role '{}'", role.getName());
        return new RoleResponse(role.getId(), role.getName(), role.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet()));
    }

    /**
     * Updates permissions for a role.
     *
     * @param roleId          Role ID.
     * @param permissionNames List of permissions to assign.
     * @return Updated role details.
     */
    public RoleResponse updateRolePermissions(Long roleId, Set<String> permissionNames) {
        logger.info("updateRolePermissions: Finding role by ID: {}", roleId);
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        logger.info("updateRolePermissions: Assigning new permissions to role {}", role.getName());
        Set<Permission> permissions = permissionRepository.findAll()
                .stream()
                .filter(p -> permissionNames.contains(p.getName()))
                .collect(Collectors.toSet());

        role.setPermissions(permissions);
        roleRepository.save(role);

        return new RoleResponse(null, role.getName(), permissions.stream().map(Permission::getName).collect(Collectors.toSet()));
    }


    /**
     * Fetches permissions by names from the database.
     *
     * @param permissionNames List of permission names.
     * @return Set of Permission entities.
     */
    private Set<Permission> fetchPermissions(List<String> permissionNames) {
        return permissionRepository.findAll().stream()
                .filter(p -> permissionNames.contains(p.getName()))
                .collect(Collectors.toSet());
    }

    /**
     * Fetches All roles in the database.
     *
     * @return All roles along with Permission.
     */
    public List<RoleResponse> fetchRolesAndPermissions() {
        logger.info("fetchRolesAndPermissions: Finding roles and permissions");
        List<Role> roles = roleRepository.findAll();
        List<RoleResponse> roleResponses = new ArrayList<>();
        roles.stream().forEach(role -> {
            roleResponses.add(new RoleResponse(role.getId(), role.getName(),
                    role.getPermissions().stream().map(Permission::getName).collect(Collectors.toSet())));
        });
        logger.info("fetchRolesAndPermissions: Roles found successfully");
        return roleResponses;
    }
}
