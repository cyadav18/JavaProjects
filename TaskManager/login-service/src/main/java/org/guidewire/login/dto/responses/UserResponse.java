package org.guidewire.login.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL) // Makes fields optional in response
public class UserResponse {

    private UUID id;
    private String username;
    private String email;
    private Set<String> roles;

    public UserResponse() {
    }

    public UserResponse(UUID id, String username, String email, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getRoles() {
        return roles;
    }
}
