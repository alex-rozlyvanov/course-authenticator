package com.goals.course.authenticator.service.implementation;

import com.goals.course.authenticator.dao.entity.Role;
import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dao.repository.RoleRepository;
import com.goals.course.authenticator.dao.repository.UserRepository;
import com.goals.course.authenticator.dto.RoleDTO;
import com.goals.course.authenticator.exception.UserNotFoundException;
import com.goals.course.authenticator.mapper.RoleMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserRolesServiceImplTest {
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private RoleRepository mockRoleRepository;
    @Mock
    private RoleMapper mockRoleMapper;
    @InjectMocks
    private UserRolesServiceImpl service;

    @SuppressWarnings("unchecked")
    @Test
    void changeUserRoles_callRolRepo_findAllById() {
        // GIVEN
        final var roleId1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var roleId2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
        final var roleId3 = UUID.fromString("00000000-0000-0000-0000-000000000003");
        final var roleIds = List.of(roleId1, roleId2, roleId3);
        when(mockUserRepository.findById(any())).thenReturn(Optional.of(new User()));

        // WHEN
        service.changeUserRoles(null, roleIds);

        // THEN
        final var captor = ArgumentCaptor.forClass(Set.class);
        verify(mockRoleRepository).findAllById(captor.capture());
        assertThat(captor.getValue())
                .hasSize(3)
                .contains(roleId1, roleId2, roleId3);
    }

    @Test
    void changeUserRoles_callUserRepo_findById() {
        // GIVEN
        final var userId = UUID.fromString("5d42d1c3-2f4e-4bcc-9154-6a8c11b400cd");
        final var roles = List.of(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        when(mockUserRepository.findById(any())).thenReturn(Optional.of(new User()));

        // WHEN
        service.changeUserRoles(userId, roles);

        // THEN
        verify(mockUserRepository).findById(userId);
    }

    @Test
    void changeUserRoles_callRoleMapperMapToRoleDTOList() {
        // GIVEN
        final var role1 = new Role().setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        final var role2 = new Role().setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        final var role3 = new Role().setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        final var roles = List.of(role1, role2, role3);
        when(mockRoleRepository.findAllById(any())).thenReturn(roles);

        when(mockUserRepository.findById(any())).thenReturn(Optional.of(new User()));

        // WHEN
        service.changeUserRoles(null, List.of());

        // THEN
        verify(mockRoleMapper).mapToRoleDTOList(roles);
    }

    @Test
    void changeUserRoles_callUserRepoSave() {
        // GIVEN
        final var role1 = new Role().setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        final var role2 = new Role().setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        final var role3 = new Role().setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        final var roles = List.of(role1, role2, role3);
        when(mockRoleRepository.findAllById(any())).thenReturn(roles);

        final var user = new User().setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        when(mockUserRepository.findById(any())).thenReturn(Optional.of(user));

        // WHEN
        service.changeUserRoles(null, List.of());

        // THEN
        final var expectedUser = new User().setId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                .setRoles(roles);
        verify(mockUserRepository).save(expectedUser);
    }

    @Test
    void changeUserRoles_checkResult() {
        // GIVEN
        when(mockUserRepository.findById(any())).thenReturn(Optional.of(new User()));

        final var roleDTO = RoleDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000000")).build();
        final var expectedRolesDTO = List.of(roleDTO);
        when(mockRoleMapper.mapToRoleDTOList(any())).thenReturn(expectedRolesDTO);

        // WHEN
        final var result = service.changeUserRoles(null, List.of());

        // THEN
        assertThat(result).isSameAs(expectedRolesDTO);
    }

    @Test
    void changeUserRoles_userNotFound_checkResult() {
        // GIVEN
        final List<UUID> roles = List.of();
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        when(mockUserRepository.findById(any())).thenReturn(Optional.empty());

        // WHEN
        final var expectedException = assertThrows(
                UserNotFoundException.class, () -> service.changeUserRoles(userId, roles));

        // THEN
        assertThat(expectedException.getMessage()).isEqualTo("User with id '00000000-0000-0000-0000-000000000001' not found!");
    }

    @Test
    void getAllRoles_callMapToRoleDTO() {
        // GIVEN
        final var role1 = new Role().setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        final var role2 = new Role().setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        final var role3 = new Role().setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        final var roles = List.of(role1, role2, role3);
        when(mockRoleRepository.findAll()).thenReturn(roles);

        // WHEN
        service.getAllRoles();

        // THEN
        verify(mockRoleMapper).mapToRoleDTO(role1);
        verify(mockRoleMapper).mapToRoleDTO(role2);
        verify(mockRoleMapper).mapToRoleDTO(role3);
    }

    @Test
    void getAllRoles_checkResult() {
        // GIVEN
        final var role1 = new Role().setTitle("1");
        final var role2 = new Role().setTitle("2");
        final var role3 = new Role().setTitle("3");
        when(mockRoleRepository.findAll()).thenReturn(List.of(role1, role2, role3));

        final var roleDTO1 = RoleDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000001")).build();
        final var roleDTO2 = RoleDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000002")).build();
        final var roleDTO3 = RoleDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000003")).build();
        when(mockRoleMapper.mapToRoleDTO(any())).thenReturn(
                roleDTO1,
                roleDTO2,
                roleDTO3
        );

        // WHEN
        final var result = service.getAllRoles();

        // THEN
        assertThat(result).hasSize(3).contains(roleDTO1, roleDTO2, roleDTO3);
    }
}
