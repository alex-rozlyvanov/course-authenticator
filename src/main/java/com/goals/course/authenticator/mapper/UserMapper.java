package com.goals.course.authenticator.mapper;

import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dto.SignUpRequest;
import com.goals.course.authenticator.dto.UserDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserMapper {
    private final RoleMapper roleMapper;

    public User mapToUser(final SignUpRequest request) {
        return new User()
                .setUsername(request.getUsername())
                .setFirstName(request.getFirstName())
                .setLastName(request.getLastName());
    }

    public UserDTO mapToUserDTO(final User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .enabled(user.isEnabled())
                .roles(roleMapper.mapToRoleDTOList(user.getRoles()))
                .build();
    }
}
