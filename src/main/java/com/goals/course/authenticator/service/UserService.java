package com.goals.course.authenticator.service;

import com.goals.course.authenticator.dto.UserDTO;

import java.util.UUID;

public interface UserService {
    UserDTO getCurrentUser();

    UserDTO getUserById(final UUID userId);
}
