package com.goals.course.authenticator.service;

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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private UserMapper mockUserMapper;
    @Mock
    private SecurityService mockSecurityService;

    @InjectMocks
    private UserService service;

    @Test
    void getCurrentUser_callGetById() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var user = new User().setId(userId);

        when(mockSecurityService.getCurrentUser()).thenReturn(Mono.just(user));
        when(mockUserRepository.findById(any())).thenReturn(Mono.just(new User()));
        when(mockUserMapper.mapToUserDTO(any())).thenReturn(UserDTO.builder().build());

        // WHEN
        final var mono = service.getCurrentUser();

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockUserRepository).findById(userId);
    }

    @Test
    void getCurrentUser_callMapToUserDTO() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var user = new User().setId(userId);
        when(mockSecurityService.getCurrentUser()).thenReturn(Mono.just(user));

        final var userFromRepo = new User().setUsername("test+username@gmail.com");
        when(mockUserRepository.findById(any())).thenReturn(Mono.just(userFromRepo));
        when(mockUserMapper.mapToUserDTO(any())).thenReturn(UserDTO.builder().build());

        // WHEN
        final var mono = service.getCurrentUser();

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockUserMapper).mapToUserDTO(userFromRepo);
    }

    @Test
    void getCurrentUser_checkResult() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var user = new User().setId(userId);

        when(mockSecurityService.getCurrentUser()).thenReturn(Mono.just(user));
        when(mockUserRepository.findById(any())).thenReturn(Mono.just(new User()));

        final var userDTO = UserDTO.builder().id(userId).build();
        when(mockUserMapper.mapToUserDTO(any())).thenReturn(userDTO);

        // WHEN
        final var mono = service.getCurrentUser();

        // THEN
        StepVerifier.create(mono)
                .expectNext(userDTO)
                .verifyComplete();
    }

    @Test
    void getCurrentUser_userNotFound_checkResult() {
        // GIVEN
        final var userId = UUID.fromString("10000000-0000-1000-0000-000000000001");
        final var user = new User().setId(userId);

        when(mockSecurityService.getCurrentUser()).thenReturn(Mono.just(user));
        when(mockUserRepository.findById(any())).thenReturn(Mono.empty());

        // WHEN
        final var mono = service.getCurrentUser();

        // THEN
        StepVerifier.create(mono)
                .expectErrorSatisfies(exception -> {
                    assertThat(exception).isInstanceOf(UserNotFoundException.class);
                    assertThat(exception).hasMessage("User with id '10000000-0000-1000-0000-000000000001' not found!");
                })
                .verify();
    }

}
