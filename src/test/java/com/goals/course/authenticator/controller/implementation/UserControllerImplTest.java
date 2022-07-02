package com.goals.course.authenticator.controller.implementation;

import com.goals.course.authenticator.controller.UserController;
import com.goals.course.authenticator.dto.RoleDTO;
import com.goals.course.authenticator.dto.UserDTO;
import com.goals.course.authenticator.service.UserRolesService;
import com.goals.course.authenticator.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerImplTest {

    @Mock
    private UserRolesService mockUserRolesService;
    @Mock
    private UserService mockUserService;

    @InjectMocks
    private UserController service;

    @Test
    void changeUserRoles_callChangeUserRoles() {
        // GIVEN
        final var userId = UUID.fromString("5d42d1c3-2f4e-4bcc-9154-6a8c11b400cd");
        final var roleIds = List.of(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        when(mockUserRolesService.changeUserRoles(any(), any())).thenReturn(Mono.empty());

        // WHEN
        final var mono = service.changeUserRoles(userId, roleIds);

        // THEN
        StepVerifier.create(mono).verifyComplete();
        verify(mockUserRolesService).changeUserRoles(userId, roleIds);
    }

    @Test
    void changeUserRoles_checkResult() {
        // GIVEN
        final var role = RoleDTO.builder()
                .id(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                .title("test")
                .build();
        final var expectedRoles = List.of(role);
        when(mockUserRolesService.changeUserRoles(any(), any())).thenReturn(Mono.just(expectedRoles));

        // WHEN
        final var mono = service.changeUserRoles(null, null);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result).isSameAs(expectedRoles))
                .verifyComplete();
    }

    @Test
    void getCurrentUser_checkResult() {
        // GIVEN
        final var userDTO = UserDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000007")).build();
        when(mockUserService.getCurrentUser()).thenReturn(Mono.just(userDTO));

        // WHEN
        final var mono = service.getCurrentUser();

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> assertThat(result).isSameAs(userDTO))
                .verifyComplete();
    }
}
