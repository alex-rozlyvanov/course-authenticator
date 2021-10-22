package com.goals.course.authenticator.service.implementation;

import com.goals.course.authenticator.dao.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceImplTest {

    @InjectMocks
    private SecurityServiceImpl service;

    @Test
    void getCurrentUser_checkResult() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        final var user = new User().setId(userId);
        setUserInSecurityContext(user);

        // WHEN
        final var result = service.getCurrentUser();

        // THEN
        assertThat(result).isSameAs(user);
    }

    private void setUserInSecurityContext(final User user) {
        final var authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(user);
        final var securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }
}
