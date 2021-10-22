package com.goals.course.authenticator.service.implementation;

import com.goals.course.authenticator.dao.repository.UserRepository;
import com.goals.course.authenticator.dto.UserDTO;
import com.goals.course.authenticator.exception.UserNotFoundException;
import com.goals.course.authenticator.mapper.UserMapper;
import com.goals.course.authenticator.service.SecurityService;
import com.goals.course.authenticator.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final SecurityService securityService;

    @Override
    public UserDTO getCurrentUser() {
        final var user = securityService.getCurrentUser();

        return userRepository.findById(user.getId())
                .map(userMapper::mapToUserDTO)
                .orElseThrow(() -> new UserNotFoundException(user.getId()));
    }

    @Override
    public UserDTO getUserById(final UUID userId) {
        final var user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return userMapper.mapToUserDTO(user);
    }
}
