package org.guidewire.login.dto.requests;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class UpdateUserRolesRequest {
    private List<String> roleNames;

    public List<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
    }

    public Set<String> getRole() {
        return roleNames.stream().filter(roleName -> !roleName.isEmpty()).collect(Collectors.toSet());

    }
}
