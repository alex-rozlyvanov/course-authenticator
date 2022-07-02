package com.goals.course.authenticator.service;

import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dao.repository.RoleRepository;
import com.goals.course.authenticator.dao.repository.UserRepository;
import com.goals.course.authenticator.dto.RoleDTO;
import com.goals.course.authenticator.exception.UserNotFoundException;
import com.goals.course.authenticator.mapper.RoleMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class UserRolesService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public Mono<List<RoleDTO>> changeUserRoles(final UUID userId, final List<UUID> roleIds) {
        log.info("Changing user '{}' roles to [{}]", userId, roleIds);
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(() -> new UserNotFoundException(userId)))
                .flatMap(existingUser -> setRoles(roleIds, existingUser))
                .flatMap(userRepository::save)
                .map(this::getRoles);
    }

    private List<RoleDTO> getRoles(User savedUser) {
        return savedUser.getRoles()
                .stream()
                .map(roleMapper::mapToRoleDTO)
                .toList();
    }

    private Mono<User> setRoles(final List<UUID> roleIds, final User existingUser) {
        return roleRepository.findAllById(roleIds)
                .collectList()
                .map(existingUser::setRoles);
    }

    public Flux<RoleDTO> getAllRoles() {
        log.info("Get all roles");
        return roleRepository.findAll()
                .map(roleMapper::mapToRoleDTO);
    }
}
