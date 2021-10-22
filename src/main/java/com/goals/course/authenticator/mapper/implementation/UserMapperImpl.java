package com.goals.course.authenticator.mapper.implementation;

import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dto.SignUpRequest;
import com.goals.course.authenticator.dto.UserDTO;
import com.goals.course.authenticator.mapper.RoleMapper;
import com.goals.course.authenticator.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserMapperImpl implements UserMapper {
    private final RoleMapper roleMapper;

    @Override
    public User mapToUser(final SignUpRequest request) {
        return new User()
                .setUsername(request.getUsername())
                .setFirstName(request.getFirstName())
                .setLastName(request.getLastName());
    }

    @Override
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
