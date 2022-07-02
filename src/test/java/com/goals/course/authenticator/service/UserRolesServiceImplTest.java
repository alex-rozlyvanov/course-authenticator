package com.goals.course.authenticator.service;

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
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRolesServiceImplTest {
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private RoleRepository mockRoleRepository;
    @Mock
    private RoleMapper mockRoleMapper;
    @InjectMocks
    private UserRolesService service;

    @Test
    void changeUserRoles_callUserRepo_findById() {
        // GIVEN
        final var userId = UUID.fromString("5d42d1c3-2f4e-4bcc-9154-6a8c11b400cd");
        final var roles = List.of(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        when(mockUserRepository.findById(any())).thenReturn(Mono.just(new User()));
        when(mockUserRepository.save(any())).thenReturn(Mono.just(new User()));
        when(mockRoleRepository.findAllById(ArgumentMatchers.<Iterable<UUID>>any())).thenReturn(Flux.just(new Role()));

        // WHEN
        final var mono = service.changeUserRoles(userId, roles);

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockUserRepository).findById(userId);
    }

    @SuppressWarnings("unchecked")
    @Test
    void changeUserRoles_callFindAllById() {
        // GIVEN
        final var roleId1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var roleId2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
        final var roleId3 = UUID.fromString("00000000-0000-0000-0000-000000000003");
        final var roleIds = List.of(roleId1, roleId2, roleId3);
        when(mockUserRepository.findById(any())).thenReturn(Mono.just(new User()));
        when(mockRoleRepository.findAllById(ArgumentMatchers.<Iterable<UUID>>any())).thenReturn(Flux.just(new Role()));
        when(mockUserRepository.save(any())).thenReturn(Mono.just(new User()));
//        when(mockRoleMapper.mapToRoleDTO(any())).thenReturn(RoleDTO.builder().build());

        // WHEN
        final var mono = service.changeUserRoles(UUID.fromString("00000000-0000-0000-0000-000000000001"), roleIds);

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        final var captor = ArgumentCaptor.forClass(Set.class);
        verify(mockRoleRepository).findAllById(captor.capture());
        assertThat(captor.getValue())
                .hasSize(3)
                .containsExactly(roleId1, roleId2, roleId3);
    }

    @Test
    void changeUserRoles_callMapToRoleDTO() {
        // GIVEN
        final var user = new User();
        when(mockUserRepository.findById(any())).thenReturn(Mono.just(user));

        final var role1 = new Role().setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        final var role2 = new Role().setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        final var role3 = new Role().setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        when(mockRoleRepository.findAllById(ArgumentMatchers.<Iterable<UUID>>any())).thenReturn(Flux.just(role1, role2, role3));
        when(mockUserRepository.save(any())).thenReturn(Mono.just(user));

        // WHEN
        final var mono = service.changeUserRoles(null, List.of());

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockRoleMapper, times(3)).mapToRoleDTO(any());
        verify(mockRoleMapper).mapToRoleDTO(role1);
        verify(mockRoleMapper).mapToRoleDTO(role2);
        verify(mockRoleMapper).mapToRoleDTO(role3);
    }

    @Test
    void changeUserRoles_callSave() {
        // GIVEN
        final var role1 = new Role().setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        final var role2 = new Role().setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        final var role3 = new Role().setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        final var roles = List.of(role1, role2, role3);
        when(mockRoleRepository.findAllById(ArgumentMatchers.<Iterable<UUID>>any())).thenReturn(Flux.just(role1, role2, role3));

        final var user = new User().setId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        when(mockUserRepository.findById(any())).thenReturn(Mono.just(user));
        when(mockUserRepository.save(any())).thenReturn(Mono.just(user));

        // WHEN
        final var mono = service.changeUserRoles(null, List.of());

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        final var expectedUser = new User().setId(UUID.fromString("00000000-0000-0000-0000-000000000000"))
                .setRoles(roles);
        verify(mockUserRepository).save(expectedUser);
    }

    @Test
    void changeUserRoles_checkResult() {
        // GIVEN
        when(mockUserRepository.findById(any())).thenReturn(Mono.just(new User()));

        final var roleDTO = RoleDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000000")).build();
        when(mockRoleMapper.mapToRoleDTO(any())).thenReturn(roleDTO);
        when(mockRoleRepository.findAllById(ArgumentMatchers.<Iterable<UUID>>any())).thenReturn(Flux.just(new Role()));
        when(mockUserRepository.save(any())).thenReturn(Mono.just(new User().setRoles(List.of(new Role()))));

        // WHEN
        final var mono = service.changeUserRoles(null, List.of());

        // THEN
        StepVerifier.create(mono)
                .expectNext(List.of(roleDTO))
                .verifyComplete();
    }

    @Test
    void changeUserRoles_userNotFound_checkResult() {
        // GIVEN
        final List<UUID> roles = List.of();
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        when(mockUserRepository.findById(any())).thenReturn(Mono.empty());

        // WHEN
        final var mono = service.changeUserRoles(userId, roles);

        // THEN
        StepVerifier.create(mono)
                .expectErrorSatisfies(expectedException -> {
                    assertThat(expectedException).isInstanceOf(UserNotFoundException.class);
                    assertThat(expectedException.getMessage()).isEqualTo("User with id '00000000-0000-0000-0000-000000000001' not found!");

                })
                .verify();
    }

    @Test
    void getAllRoles_callMapToRoleDTO() {
        // GIVEN
        final var role1 = new Role().setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        final var role2 = new Role().setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
        final var role3 = new Role().setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
        when(mockRoleRepository.findAll()).thenReturn(Flux.just(role1, role2, role3));
        when(mockRoleMapper.mapToRoleDTO(any())).thenReturn(RoleDTO.builder().build());

        // WHEN
        final var mono = service.getAllRoles();

        // THEN
        StepVerifier.create(mono).expectNextCount(3).verifyComplete();
        verify(mockRoleMapper, times(3)).mapToRoleDTO(any());
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
        when(mockRoleRepository.findAll()).thenReturn(Flux.just(role1, role2, role3));

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
        StepVerifier.create(result)
                .expectNext(roleDTO1, roleDTO2, roleDTO3)
                .verifyComplete();

    }
}
