package com.goals.course.authenticator.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@Jacksonized
public class UserDTO {
    private final UUID id;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final boolean enabled;
    private final List<RoleDTO> roles;
}
