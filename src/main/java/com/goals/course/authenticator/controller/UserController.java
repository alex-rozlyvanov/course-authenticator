package com.goals.course.authenticator.controller;

import com.goals.course.authenticator.dto.RoleDTO;
import com.goals.course.authenticator.dto.UserDTO;

import java.util.List;
import java.util.UUID;

public interface UserController {
    UserDTO getUserById(final UUID userId);

    List<RoleDTO> changeUserRoles(final UUID userId, final List<UUID> roleIds);

    UserDTO getCurrentUser();
}
