package com.goals.course.authenticator.service.implementation;

import com.goals.course.authenticator.dao.repository.RoleRepository;
import com.goals.course.authenticator.dao.repository.UserRepository;
import com.goals.course.authenticator.dto.RoleDTO;
import com.goals.course.authenticator.exception.UserNotFoundException;
import com.goals.course.authenticator.mapper.RoleMapper;
import com.goals.course.authenticator.service.UserRolesService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserRolesServiceImpl implements UserRolesService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public List<RoleDTO> changeUserRoles(final UUID userId, final List<UUID> roleIds) {
        final var existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        final var existingRoles = roleRepository.findAllById(roleIds);

        existingUser.setRoles(existingRoles);
        userRepository.save(existingUser);

        return roleMapper.mapToRoleDTOList(existingRoles);
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::mapToRoleDTO)
                .toList();
    }
}
