package com.goals.course.authenticator.mapper.implementation;

import com.goals.course.authenticator.dao.entity.Role;
import com.goals.course.authenticator.dto.RoleDTO;
import com.goals.course.authenticator.mapper.RoleMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleMapperImplTest {

    @Test
    void mapToRoleDTOList_callMapToRoleDTO() {
        // GIVEN
        final var role = buildRole("00000000-0000-0000-0000-000000000001");
        final var role1 = buildRole("00000000-0000-0000-0000-000000000002");
        final var roles = List.of(role, role1);
        final var roleMapperSpy = Mockito.spy(new RoleMapper());

        // WHEN
        roleMapperSpy.mapToRoleDTOList(roles);

        // THEN
        verify(roleMapperSpy, times(2)).mapToRoleDTO(any());
        verify(roleMapperSpy).mapToRoleDTO(role);
        verify(roleMapperSpy).mapToRoleDTO(role1);
    }

    @Test
    void mapToRoleDTOList_checkResult() {
        // GIVEN
        final var role = buildRole("00000000-0000-0000-0000-000000000001");
        final var role1 = buildRole("00000000-0000-0000-0000-000000000002");
        final var roles = List.of(role, role1);
        final var roleMapperSpy = Mockito.spy(new RoleMapper());

        final var roleDTO = buildRoleDTO("00000000-0000-0000-0000-000000000001");
        final var roleDTO1 = buildRoleDTO("00000000-0000-0000-0000-000000000002");
        doReturn(roleDTO, roleDTO1).when(roleMapperSpy).mapToRoleDTO(any());

        // WHEN
        final var result = roleMapperSpy.mapToRoleDTOList(roles);

        // THEN
        assertThat(result)
                .hasSize(2)
                .contains(roleDTO, roleDTO1);
    }

    @Test
    void mapToRoleDTO_id_checkResult() {
        // GIVEN
        final var role = buildRole("00000000-0000-0000-0000-000000000099");

        // WHEN
        final var result = new RoleMapper().mapToRoleDTO(role);

        // THEN
        assertThat(result.getId()).hasToString("00000000-0000-0000-0000-000000000099");
    }

    @Test
    void mapToRoleDTO_title_checkResult() {
        // GIVEN
        final var role = new Role().setTitle("test_title");

        // WHEN
        final var result = new RoleMapper().mapToRoleDTO(role);

        // THEN
        assertThat(result.getTitle()).isEqualTo("test_title");
    }

    private Role buildRole(String roleId) {
        return new Role().setId(UUID.fromString(roleId));
    }

    private RoleDTO buildRoleDTO(final String roleId) {
        return RoleDTO.builder()
                .id(UUID.fromString(roleId))
                .build();
    }
}
