package com.goals.course.authenticator.dao.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void getRoles_checkResult() {
        // GIVEN
        final var roles = List.of(new Role().setTitle("testRole"));
        final var user = new User().setRoles(roles);

        // WHEN
        final var result = user.getRoles();

        // THEN
        assertThat(result).isSameAs(roles);
    }

    @Test
    void getRoles_rolesNotSet_checkResult() {
        // GIVEN
        final var user = new User();

        // WHEN
        final var result = user.getRoles();

        // THEN
        assertThat(result).isEqualTo(new ArrayList<>());
    }

}
