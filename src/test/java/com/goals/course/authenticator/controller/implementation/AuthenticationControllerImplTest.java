package com.goals.course.authenticator.controller.implementation;

import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dto.*;
import com.goals.course.authenticator.service.LoginService;
import com.goals.course.authenticator.service.RefreshTokenService;
import com.goals.course.authenticator.service.SecurityService;
import com.goals.course.authenticator.service.SignUpService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerImplTest {

    @Mock
    private SignUpService mockSignUpService;
    @Mock
    private LoginService mockLoginService;
    @Mock
    private SecurityService mockSecurityService;
    @Mock
    private RefreshTokenService mockRefreshTokenService;

    @InjectMocks
    private AuthenticationControllerImpl service;

    @Test
    void login_callLogin() {
        // GIVEN
        final var loginRequest = LoginRequest.builder()
                .username("testUsername")
                .password("testPass")
                .build();

        // WHEN
        service.login(loginRequest);

        // THEN
        verify(mockLoginService).login(loginRequest);
    }

    @Test
    void login_checkResult() {
        // GIVEN
        final var user = UserDTO.builder().username("test@gmail.com").build();
        final var loginResponse = LoginResponse.builder().user(user).build();
        when(mockLoginService.login(any())).thenReturn(loginResponse);

        // WHEN
        final var result = service.login(null);

        // THEN
        assertThat(result.getBody()).isSameAs(loginResponse);
    }

    @Test
    void login_BadCredentialsExceptionThrown_checkResult() {
        // GIVEN
        when(mockLoginService.login(any())).thenThrow(BadCredentialsException.class);

        // WHEN
        final var result = service.login(null);

        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void signUp_callSignUp() {
        // GIVEN
        final var signUpRequest = SignUpRequest.builder().username("test").build();

        // WHEN
        service.signUp(signUpRequest);

        // THEN
        verify(mockSignUpService).signUp(signUpRequest);
    }

    @Test
    void signUp_checkResult() {
        // GIVEN
        final var userDTO = UserDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000001")).build();
        final var loginResponse = LoginResponse.builder().user(userDTO).build();
        when(mockSignUpService.signUp(any())).thenReturn(loginResponse);

        // WHEN
        final var result = service.signUp(null);

        // THEN
        assertThat(result).isSameAs(loginResponse);
    }

    @Test
    void refreshToken_callRefreshToken() {
        // GIVEN
        final var refreshRequest = TokenRefreshRequest.builder()
                .refreshToken("test_refresh_token")
                .build();

        // WHEN
        service.refreshToken(refreshRequest);

        // THEN
        verify(mockRefreshTokenService).refreshToken("test_refresh_token");
    }

    @Test
    void refreshToken_checkResult() {
        // GIVEN
        final var refreshRequest = TokenRefreshRequest.builder().build();
        final var tokenRefreshResponse = TokenRefreshResponse.builder()
                .accessToken("some_access_token_123")
                .refreshToken("some_access_token_123")
                .build();
        when(mockRefreshTokenService.refreshToken(any())).thenReturn(tokenRefreshResponse);

        // WHEN
        final var result = service.refreshToken(refreshRequest);

        // THEN
        assertThat(result.getAccessToken()).isEqualTo("some_access_token_123");
        assertThat(result.getRefreshToken()).isEqualTo("some_access_token_123");
    }

    @Test
    void logout_callDeleteRefreshTokenByUserId() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        final var user = new User().setId(userId);
        when(mockSecurityService.getCurrentUser()).thenReturn(user);

        // WHEN
        service.logout();

        // THEN
        verify(mockRefreshTokenService).deleteRefreshTokenByUserId(userId);
    }

    @Test
    void logout_checkResult() {
        // GIVEN
        when(mockSecurityService.getCurrentUser()).thenReturn(new User());

        // WHEN
        final var result = service.logout();

        // THEN
        assertThat(result).isEqualTo("Session finished");
    }
}
