package com.goals.course.authenticator.controller.implementation;

import com.goals.course.authenticator.controller.AuthenticationController;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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
    private AuthenticationController service;

    @Test
    void login_callLogin() {
        // GIVEN
        final var loginRequest = LoginRequest.builder()
                .username("testUsername")
                .password("testPass")
                .build();
        when(mockLoginService.login(any())).thenReturn(Mono.just(LoginResponse.builder().build()));

        // WHEN
        final var mono = service.login(loginRequest);

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockLoginService).login(loginRequest);
    }

    @Test
    void login_checkResult() {
        // GIVEN
        final var user = UserDTO.builder().username("test@gmail.com").build();
        final var loginResponse = LoginResponse.builder().user(user).build();
        when(mockLoginService.login(any())).thenReturn(Mono.just(loginResponse));

        // WHEN
        final var result = service.login(null);

        // THEN
        assertThat(result.block()).isSameAs(loginResponse);
    }

    @Test
    void signUp_callSignUp() {
        // GIVEN
        final var signUpRequest = SignUpRequest.builder().username("test").build();
        final var loginResponse = LoginResponse.builder().build();
        when(mockSignUpService.signUp(any())).thenReturn(Mono.just(loginResponse));
        // WHEN
        final var mono = service.signUp(signUpRequest);

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockSignUpService).signUp(signUpRequest);
    }

    @Test
    void signUp_checkResult() {
        // GIVEN
        final var userDTO = UserDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000001")).build();
        final var loginResponse = LoginResponse.builder().user(userDTO).build();
        when(mockSignUpService.signUp(any())).thenReturn(Mono.just(loginResponse));

        // WHEN
        final var mono = service.signUp(null);

        // THEN
        StepVerifier.create(mono)
                .expectNext(loginResponse)
                .verifyComplete();
    }

    @Test
    void refreshToken_callRefreshToken() {
        // GIVEN
        final var refreshRequest = TokenRefreshRequest.builder()
                .refreshToken("test_refresh_token")
                .build();

        final var tokenRefreshResponse = TokenRefreshResponse.builder().build();
        when(mockRefreshTokenService.refreshToken(any())).thenReturn(Mono.just(tokenRefreshResponse));

        // WHEN
        final var mono = service.refreshToken(refreshRequest);

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
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
        when(mockRefreshTokenService.refreshToken(any())).thenReturn(Mono.just(tokenRefreshResponse));

        // WHEN
        final var mono = service.refreshToken(refreshRequest);

        // THEN
        StepVerifier.create(mono)
                .assertNext(result -> {
                    assertThat(result.getAccessToken()).isEqualTo("some_access_token_123");
                    assertThat(result.getRefreshToken()).isEqualTo("some_access_token_123");
                })
                .verifyComplete();
    }

    @Test
    void logout_callDeleteRefreshTokenByUserId() {
        // GIVEN
        final var userId = UUID.fromString("00000000-0000-0000-0000-000000000000");
        final var user = new User().setId(userId);
        when(mockSecurityService.getCurrentUser()).thenReturn(Mono.just(user));
        when(mockRefreshTokenService.deleteRefreshTokenByUserId(any())).thenReturn(Mono.just(1));

        // WHEN
        final var mono = service.logout();

        // THEN
        StepVerifier.create(mono).expectNextCount(1).verifyComplete();
        verify(mockRefreshTokenService).deleteRefreshTokenByUserId(userId);
    }

    @Test
    void logout_checkResult() {
        // GIVEN
        when(mockSecurityService.getCurrentUser()).thenReturn(Mono.just(new User()));
        when(mockRefreshTokenService.deleteRefreshTokenByUserId(any())).thenReturn(Mono.just(1));

        // WHEN
        final var mono = service.logout();

        // THEN
        StepVerifier.create(mono)
                .expectNext("Session finished")
                .verifyComplete();
    }
}
