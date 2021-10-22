package com.goals.course.authenticator.service.implementation;

import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dao.repository.UserRepository;
import com.goals.course.authenticator.dto.UserDTO;
import com.goals.course.authenticator.exception.UserNotFoundException;
import com.goals.course.authenticator.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private UserMapper mockUserMapper;
    @Mock
    private SecurityServiceImpl mockSecurityService;

    @InjectMocks
    private UserServiceImpl service;

    @Test
    void getCurrentUser_callGetById() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var user = new User().setId(userId);

        when(mockSecurityService.getCurrentUser()).thenReturn(user);
        when(mockUserRepository.findById(any())).thenReturn(Optional.of(new User()));
        when(mockUserMapper.mapToUserDTO(any())).thenReturn(UserDTO.builder().build());

        // WHEN
        service.getCurrentUser();

        // THEN
        verify(mockUserRepository).findById(userId);
    }

    @Test
    void getCurrentUser_callMapToUserDTO() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var user = new User().setId(userId);
        when(mockSecurityService.getCurrentUser()).thenReturn(user);

        final var userFromRepo = new User().setUsername("test+username@gmail.com");
        when(mockUserRepository.findById(any())).thenReturn(Optional.of(userFromRepo));
        when(mockUserMapper.mapToUserDTO(any())).thenReturn(UserDTO.builder().build());

        // WHEN
        service.getCurrentUser();

        // THEN
        verify(mockUserMapper).mapToUserDTO(userFromRepo);
    }

    @Test
    void getCurrentUser_checkResult() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var user = new User().setId(userId);

        when(mockSecurityService.getCurrentUser()).thenReturn(user);
        when(mockUserRepository.findById(any())).thenReturn(Optional.of(new User()));

        final var userDTO = UserDTO.builder().id(userId).build();
        when(mockUserMapper.mapToUserDTO(any())).thenReturn(userDTO);

        // WHEN
        final var result = service.getCurrentUser();

        // THEN
        assertThat(result).isSameAs(userDTO);
    }

    @Test
    void getCurrentUser_userNotFound_checkResult() {
        // GIVEN
        final var userId = UUID.fromString("10000000-0000-1000-0000-000000000001");
        final var user = new User().setId(userId);

        when(mockSecurityService.getCurrentUser()).thenReturn(user);
        when(mockUserRepository.findById(any())).thenReturn(Optional.empty());

        // WHEN
        final var expectedException = assertThrows(
                UserNotFoundException.class,
                () -> service.getCurrentUser()
        );

        // THEN
        assertThat(expectedException.getMessage()).isEqualTo("User with id '10000000-0000-1000-0000-000000000001' not found!");
    }

}
