package org.guidewire.login.dto.requests;

import java.util.List;

public class UpdateRolePermissionsRequest {
    private List<String> permissionNames;

    public List<String> getPermissionNames() {
        return permissionNames;
    }

    public void setPermissionNames(List<String> permissionNames) {
        this.permissionNames = permissionNames;
    }
}
