package org.guidewire.login.dto.responses;

public class UserRoleResponse {
    private String username;
    private boolean isAdmin;

    public UserRoleResponse() {

    }

    public UserRoleResponse(String username, boolean isAdmin) {
        this.username = username;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
