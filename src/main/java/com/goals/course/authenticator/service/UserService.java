package com.goals.course.authenticator.service;

import com.goals.course.authenticator.dao.repository.UserRepository;
import com.goals.course.authenticator.dto.UserDTO;
import com.goals.course.authenticator.exception.UserNotFoundException;
import com.goals.course.authenticator.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityService securityService;

    public Mono<UserDTO> getCurrentUser() {
        return securityService.getCurrentUser()
                .flatMap(currentUser -> getUserById(currentUser.getId()));
    }

    public Mono<UserDTO> getUserById(final UUID userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(() -> new UserNotFoundException(userId)))
                .map(userMapper::mapToUserDTO);
    }
}
