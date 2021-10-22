package com.goals.course.authenticator.controller.implementation;

import com.goals.course.authenticator.dto.RoleDTO;
import com.goals.course.authenticator.dto.UserDTO;
import com.goals.course.authenticator.service.UserRolesService;
import com.goals.course.authenticator.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private UserControllerImpl service;

    @Test
    void changeUserRoles_callChangeUserRoles() {
        // GIVEN
        final var userId = UUID.fromString("5d42d1c3-2f4e-4bcc-9154-6a8c11b400cd");
        final var roleIds = List.of(UUID.fromString("00000000-0000-0000-0000-000000000002"));

        // WHEN
        service.changeUserRoles(userId, roleIds);

        // THEN
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
        when(mockUserRolesService.changeUserRoles(any(), any())).thenReturn(expectedRoles);

        // WHEN
        final var result = service.changeUserRoles(null, null);

        // THEN
        assertThat(result).isSameAs(expectedRoles);
    }

    @Test
    void getCurrentUser_checkResult() {
        // GIVEN
        final var userDTO = UserDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000007")).build();
        when(mockUserService.getCurrentUser()).thenReturn(userDTO);

        // WHEN
        final var result = service.getCurrentUser();

        // THEN
        assertThat(result).isSameAs(userDTO);
    }
}
