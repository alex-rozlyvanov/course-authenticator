package com.goals.course.authenticator.mapper;

import com.goals.course.authenticator.dao.entity.Role;
import com.goals.course.authenticator.dto.RoleDTO;

import java.util.List;

public interface RoleMapper {
    List<RoleDTO> mapToRoleDTOList(final List<Role> roles);

    RoleDTO mapToRoleDTO(final Role roles);
}
