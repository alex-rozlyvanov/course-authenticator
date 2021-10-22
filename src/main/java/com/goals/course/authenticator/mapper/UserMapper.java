package com.goals.course.authenticator.mapper;

import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dto.SignUpRequest;
import com.goals.course.authenticator.dto.UserDTO;

public interface UserMapper {
    User mapToUser(final SignUpRequest request);

    UserDTO mapToUserDTO(final User user);
}
