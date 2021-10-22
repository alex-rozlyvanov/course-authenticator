package com.goals.course.authenticator.mapper.implementation;

import com.goals.course.authenticator.dao.entity.Role;
import com.goals.course.authenticator.dto.RoleDTO;
import com.goals.course.authenticator.mapper.RoleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleMapperImpl implements RoleMapper {
    @Override
    public List<RoleDTO> mapToRoleDTOList(final List<Role> roles) {
        return roles.stream()
                .map(this::mapToRoleDTO)
                .toList();
    }

    @Override
    public RoleDTO mapToRoleDTO(final Role role) {
        return RoleDTO.builder()
                .id(role.getId())
                .title(role.getTitle())
                .build();
    }
}
