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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.Mockito.*;

@SuppressWarnings("ConstantConditions")
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
        when(mockRoleRepository.findAll()).thenReturn(List.of());

        final var roles = Arrays.stream(Roles.values())
                .map(v -> new Role().setTitle(v.name()))
                .collect(Collectors.toList());

        // WHEN
        service.onApplicationEvent(null);

        // THEN
        roles.forEach(r -> verify(mockRoleRepository).save(r));
    }

    @Test
    void onApplicationEvent_rolesArePresent_neverCallRoleRepo_save() {
        // GIVEN
        final var roles = Arrays.stream(Roles.values())
                .map(v -> new Role().setTitle(v.name()))
                .collect(Collectors.toList());

        when(mockRoleRepository.findAll()).thenReturn(roles);

        // WHEN
        service.onApplicationEvent(null);

        // THEN
        verify(mockRoleRepository, never()).save(any());
    }

    @Test
    void onApplicationEvent_callUserRepo_findByUsername() {
        // GIVEN
        ReflectionTestUtils.setField(service, "defaultAdminUsername", "secureUserName");

        // WHEN
        service.onApplicationEvent(null);

        // THEN
        verify(mockUserRepository).findByUsername("secureUserName");
    }

    @Test
    void onApplicationEvent_adminIsNotPresent_callPasswordEncoder_encode() {
        // GIVEN
        ReflectionTestUtils.setField(service, "defaultAdminPass", "securePass");

        // WHEN
        service.onApplicationEvent(null);

        // THEN
        verify(mockPasswordEncoder).encode("securePass");
    }

    @Test
    void onApplicationEvent_adminIsNotPresent_callUserRepo_findByUsername() {
        // GIVEN
        final var roles = Arrays.stream(Roles.values())
                .map(v -> new Role().setTitle(v.name()))
                .collect(Collectors.toList());

        when(mockRoleRepository.findAll()).thenReturn(roles);
        when(mockUserRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(mockPasswordEncoder.encode(any())).thenReturn("ENCODED_PASS");
        ReflectionTestUtils.setField(service, "defaultAdminFirstname", "Admin");
        ReflectionTestUtils.setField(service, "defaultAdminLastname", "Adminovich");
        ReflectionTestUtils.setField(service, "defaultAdminUsername", "secureUserName");

        // WHEN
        service.onApplicationEvent(null);

        // THEN
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
        when(mockUserRepository.findByUsername(any())).thenReturn(Optional.of(new User()));

        // WHEN
        service.onApplicationEvent(null);

        // THEN
        verify(mockPasswordEncoder, never()).encode(any());
    }

    @Test
    void onApplicationEvent_adminIsPresent_neverCallUserRepo_findByUsername() {
        // GIVEN
        when(mockUserRepository.findByUsername(any())).thenReturn(Optional.of(new User()));

        // WHEN
        service.onApplicationEvent(null);

        // THEN
        verify(mockUserRepository, never()).save(any());
    }

}
