package com.goals.course.authenticator.controller.implementation;

import com.goals.course.authenticator.dto.RoleDTO;
import com.goals.course.authenticator.service.UserRolesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RolesControllerImplTest {

    @Mock
    private UserRolesService mockUserRolesService;
    @InjectMocks
    private RolesControllerImpl service;

    @Test
    void getAllRoles_checkResult() {
        // GIVEN
        final var role1 = RoleDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000001")).build();
        final var role2 = RoleDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000002")).build();
        final var role3 = RoleDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000003")).build();
        final var roles = List.of(role1, role2, role3);
        when(mockUserRolesService.getAllRoles()).thenReturn(roles);

        // WHEN
        final var result = service.getAllRoles();

        // THEN
        assertThat(result).isSameAs(roles);
    }

}
