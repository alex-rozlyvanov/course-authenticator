package com.goals.course.authenticator.configuration;

import com.goals.course.authenticator.dao.entity.Role;
import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dao.enums.Roles;
import com.goals.course.authenticator.dao.repository.RoleRepository;
import com.goals.course.authenticator.dao.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminInitializationConfigurationTest {

    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private RoleRepository mockRoleRepository;
    @Mock
    private PasswordEncoder mockPasswordEncoder;

    @InjectMocks
    private AdminInitializationConfiguration service;

    @Test
    void onApplicationEvent_rolesAreNotPresent_callRoleRepo_save() {
        // GIVEN
        when(mockRoleRepository.findAll()).thenReturn(Flux.empty());
        when(mockRoleRepository.save(any())).thenReturn(Mono.empty());
        when(mockUserRepository.save(any())).thenReturn(Mono.empty());
        when(mockUserRepository.findByUsername(any())).thenReturn(Mono.empty());

        // WHEN
        service.onApplicationEvent(null).subscribe();

        // THEN
        final var roles = buildAllRoles();
        roles.forEach(r -> verify(mockRoleRepository).save(r));
    }

    @Test
    void onApplicationEvent_rolesArePresent_neverCallRoleRepo_save() {
        // GIVEN
        final var roles = buildAllRoles();
        when(mockRoleRepository.findAll()).thenReturn(Flux.fromIterable(roles));
        when(mockUserRepository.findByUsername(any())).thenReturn(Mono.just(new User()));

        // WHEN
        final var mono = service.onApplicationEvent(null);

        // THEN
        StepVerifier.create(mono).verifyComplete();
        verify(mockRoleRepository, never()).save(any());
    }

    @Test
    void onApplicationEvent_callUserRepo_findByUsername() {
        // GIVEN
        final var roles = buildAllRoles();
        when(mockRoleRepository.findAll()).thenReturn(Flux.fromIterable(roles));
        when(mockUserRepository.findByUsername(any())).thenReturn(Mono.just(new User()));

        ReflectionTestUtils.setField(service, "defaultAdminUsername", "secureUserName");

        // WHEN
        final var mono = service.onApplicationEvent(null);

        // THEN
        StepVerifier.create(mono).verifyComplete();
        verify(mockUserRepository).findByUsername("secureUserName");
    }

    private List<Role> buildAllRoles() {
        return Arrays.stream(Roles.values())
                .map(v -> new Role().setTitle(v.name()))
                .toList();
    }

    @Test
    void onApplicationEvent_adminIsNotPresent_callEncode() {
        // GIVEN
        final var roles = buildAllRoles();
        when(mockRoleRepository.findAll()).thenReturn(Flux.fromIterable(roles));
        when(mockUserRepository.findByUsername(any())).thenReturn(Mono.empty());
        when(mockUserRepository.save(any())).thenReturn(Mono.empty());

        ReflectionTestUtils.setField(service, "defaultAdminPass", "securePass");

        // WHEN
        final var mono = service.onApplicationEvent(null);

        // THEN
        StepVerifier.create(mono).verifyComplete();
        verify(mockPasswordEncoder).encode("securePass");
    }

    @Test
    void onApplicationEvent_adminIsNotPresent_callSave() {
        // GIVEN
        final var roles = buildAllRoles();
        when(mockRoleRepository.findAll()).thenReturn(Flux.fromIterable(roles));
        when(mockUserRepository.findByUsername(any())).thenReturn(Mono.empty());
        when(mockUserRepository.save(any())).thenReturn(Mono.empty());
        when(mockPasswordEncoder.encode(any())).thenReturn("ENCODED_PASS");
        ReflectionTestUtils.setField(service, "defaultAdminFirstname", "Admin");
        ReflectionTestUtils.setField(service, "defaultAdminLastname", "Adminovich");
        ReflectionTestUtils.setField(service, "defaultAdminUsername", "secureUserName");

        // WHEN
        final var mono = service.onApplicationEvent(null);

        // THEN
        StepVerifier.create(mono).verifyComplete();
        final var adminUser = new User()
                .setEnabled(true)
                .setFirstName("Admin")
                .setLastName("Adminovich")
                .setUsername("secureUserName")
                .setPassword("ENCODED_PASS")
                .setRoles(roles);
        verify(mockUserRepository).save(adminUser);
    }

    @Test
    void onApplicationEvent_adminIsPresent_neverCallPasswordEncoder_encode() {
        // GIVEN
        final var roles = buildAllRoles();
        when(mockRoleRepository.findAll()).thenReturn(Flux.fromIterable(roles));
        when(mockUserRepository.findByUsername(any())).thenReturn(Mono.just(new User()));

        // WHEN
        service.onApplicationEvent(null).log().subscribe();

        // THEN
        verify(mockPasswordEncoder, never()).encode(any());
    }

    @Test
    void onApplicationEvent_adminIsPresent_neverCallUserRepo_findByUsername() {
        // GIVEN
        final var roles = buildAllRoles();
        when(mockRoleRepository.findAll()).thenReturn(Flux.fromIterable(roles));
        when(mockUserRepository.findByUsername(any())).thenReturn(Mono.just(new User().setUsername("test")));

        // WHEN
        final var mono = service.onApplicationEvent(null);

        // THEN
        StepVerifier.create(mono).verifyComplete();
        verify(mockUserRepository, never()).save(any());
    }

}
