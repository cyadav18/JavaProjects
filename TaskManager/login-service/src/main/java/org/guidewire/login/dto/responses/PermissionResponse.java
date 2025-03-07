package org.guidewire.login.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL) // Ignore null fields in JSON
public class PermissionResponse {

    private Long id;
    private String name;
    private String message; // Optional message (e.g., "Created successfully")

    public PermissionResponse(Long id, String name, String message) {
        this.id = id;
        this.name = name;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }
}
