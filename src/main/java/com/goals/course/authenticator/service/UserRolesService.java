package com.goals.course.authenticator.service;

import com.goals.course.authenticator.dto.RoleDTO;

import java.util.List;
import java.util.UUID;

public interface UserRolesService {
    List<RoleDTO> changeUserRoles(final UUID userId, final List<UUID> roleIds);

    List<RoleDTO> getAllRoles();
}
