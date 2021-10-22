package com.goals.course.authenticator.mapper.implementation;

import com.goals.course.authenticator.dao.entity.Role;
import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dto.RoleDTO;
import com.goals.course.authenticator.dto.SignUpRequest;
import com.goals.course.authenticator.mapper.RoleMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
class UserMapperImplTest {

    @Mock
    private RoleMapper mockRoleMapper;
    @InjectMocks
    private UserMapperImpl mapper;

    @Test
    void mapToUser_username_checkResult() {
        // GIVEN
        final var signUpRequest = SignUpRequest.builder().username("testUser").build();

        // WHEN
        final var result = mapper.mapToUser(signUpRequest);

        // THEN
        assertThat(result.getUsername()).isEqualTo("testUser");
    }

    @Test
    void mapToUser_firstname_checkResult() {
        // GIVEN
        final var signUpRequest = SignUpRequest.builder().firstName("testFirstName").build();

        // WHEN
        final var result = mapper.mapToUser(signUpRequest);

        // THEN
        assertThat(result.getFirstName()).isEqualTo("testFirstName");
    }

    @Test
    void mapToUser_lastname_checkResult() {
        // GIVEN
        final var signUpRequest = SignUpRequest.builder().lastName("testLastName").build();

        // WHEN
        final var result = mapper.mapToUser(signUpRequest);

        // THEN
        assertThat(result.getLastName()).isEqualTo("testLastName");
    }

    @Test
    void mapToUserDTO_id_checkResult() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var user = new User().setId(userId);

        // WHEN
        final var result = mapper.mapToUserDTO(user);

        // THEN
        assertThat(result.getId()).isEqualTo(userId);
    }

    @Test
    void mapToUserDTO_username_checkResult() {
        // GIVEN
        final var user = new User().setUsername("testUsername");

        // WHEN
        final var result = mapper.mapToUserDTO(user);

        // THEN
        assertThat(result.getUsername()).isEqualTo("testUsername");
    }

    @Test
    void mapToUserDTO_firstName_checkResult() {
        // GIVEN
        final var user = new User().setFirstName("testFirstName");

        // WHEN
        final var result = mapper.mapToUserDTO(user);

        // THEN
        assertThat(result.getFirstName()).isEqualTo("testFirstName");
    }

    @Test
    void mapToUserDTO_lastName_checkResult() {
        // GIVEN
        final var user = new User().setLastName("testLastName");

        // WHEN
        final var result = mapper.mapToUserDTO(user);

        // THEN
        assertThat(result.getLastName()).isEqualTo("testLastName");
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void mapToUserDTO_enabled_checkResult(final boolean enabledValue) {
        // GIVEN
        final var user = new User().setEnabled(enabledValue);

        // WHEN
        final var result = mapper.mapToUserDTO(user);

        // THEN
        assertThat(result.isEnabled()).isEqualTo(enabledValue);
    }

    @Test
    void mapToUserDTO_roles_call() {
        // GIVEN
        final var roles = List.of(new Role().setTitle("test1"), new Role().setTitle("test2"));
        final var user = new User().setRoles(roles);

        // WHEN
        mapper.mapToUserDTO(user);

        // THEN
        verify(mockRoleMapper).mapToRoleDTOList(roles);
    }

    @Test
    void mapToUserDTO_roles_checkResult() {
        // GIVEN
        final var roleDTO1 = RoleDTO.builder().title("test1").build();
        final var roleDTO2 = RoleDTO.builder().title("test2").build();
        final var mappedRoles = List.of(roleDTO1, roleDTO2);
        when(mockRoleMapper.mapToRoleDTOList(any())).thenReturn(mappedRoles);

        // WHEN
        final var result = mapper.mapToUserDTO(new User());

        // THEN
        assertThat(result.getRoles()).isSameAs(mappedRoles);
    }

}
