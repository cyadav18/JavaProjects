package org.guidewire.login.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserRoleResponse {
    private String username;
    private boolean isAdmin;
}
