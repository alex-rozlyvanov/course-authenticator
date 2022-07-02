package com.goals.course.authenticator.service;

import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dto.LoginRequest;
import com.goals.course.authenticator.dto.UserDTO;
import com.goals.course.authenticator.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {
    @Mock
    private JwtTokenService mockJwtTokenService;
    @Mock
    private UserMapper mockUserMapper;
    @Mock
    private RefreshTokenService mockRefreshTokenService;
    @Mock
    private ReactiveUserDetailsService mockUserDetailsService;
    @Mock
    private PasswordEncoder mockPasswordEncoder;
    @InjectMocks
    private LoginService service;

    @Test
    void login_callAuthenticate() {
        // GIVEN
        final var loginRequest = LoginRequest.builder()
                .username("test_userName")
                .password("testPass")
                .build();

        when(mockUserDetailsService.findByUsername(any())).thenReturn(Mono.just(new User()));
        when(mockPasswordEncoder.matches(any(), any())).thenReturn(true);
        when(mockRefreshTokenService.createRefreshToken(any())).thenReturn(Mono.empty());

        // WHEN
        final var result = service.login(loginRequest);

        // THEN
        StepVerifier.create(result).verifyComplete();
        verify(mockUserDetailsService).findByUsername("test_userName");
    }

    @Test
    void login_callMatches() {
        // GIVEN
        final var loginRequest = LoginRequest.builder()
                .username("test_userName")
                .password("raw_pass")
                .build();

        final var user = new User().setPassword("encoded_pass");
        when(mockUserDetailsService.findByUsername(any())).thenReturn(Mono.just(user));
        when(mockPasswordEncoder.matches(any(), any())).thenReturn(true);
        when(mockRefreshTokenService.createRefreshToken(any())).thenReturn(Mono.empty());

        // WHEN
        final var result = service.login(loginRequest);

        // THEN
        StepVerifier.create(result).verifyComplete();
        verify(mockPasswordEncoder).matches("raw_pass", "encoded_pass");
    }

    @Test
    void login_passwordDoesNotMatches_throwException() {
        // GIVEN
        final var loginRequest = LoginRequest.builder()
                .username("test_userName")
                .password("raw_pass")
                .build();

        final var user = new User().setPassword("encoded");
        when(mockUserDetailsService.findByUsername(any())).thenReturn(Mono.just(user));
        when(mockPasswordEncoder.matches(any(), any())).thenReturn(false);

        // WHEN
        final var result = service.login(loginRequest);

        // THEN
        StepVerifier.create(result)
                .verifyErrorSatisfies(expectedException -> {
                    assertThat(expectedException).isInstanceOf(BadCredentialsException.class);
                    assertThat(expectedException.getMessage()).isEqualTo("Bad credentials");
                });
    }

    @Test
    void login_callGenerateAccessToken() {
        // GIVEN
        final var loginRequest = LoginRequest.builder()
                .username("test_userName")
                .password("testPass")
                .build();
        final var user = new User().setId(UUID.randomUUID());
        when(mockUserDetailsService.findByUsername(any())).thenReturn(Mono.just(user));
        when(mockPasswordEncoder.matches(any(), any())).thenReturn(true);
        when(mockRefreshTokenService.createRefreshToken(any())).thenReturn(Mono.empty());

        // WHEN
        final var result = service.login(loginRequest);

        // THEN
        StepVerifier.create(result).verifyComplete();
        verify(mockJwtTokenService).generateAccessToken(user);
    }

    @Test
    void login_callCreateRefreshToken() {
        // GIVEN
        final var loginRequest = LoginRequest.builder()
                .username("test_userName")
                .password("testPass")
                .build();
        final var user = new User().setId(UUID.randomUUID());
        when(mockUserDetailsService.findByUsername(any())).thenReturn(Mono.just(user));
        when(mockPasswordEncoder.matches(any(), any())).thenReturn(true);
        when(mockRefreshTokenService.createRefreshToken(any())).thenReturn(Mono.empty());

        // WHEN
        final var result = service.login(loginRequest);

        // THEN
        StepVerifier.create(result).verifyComplete();
        verify(mockRefreshTokenService).createRefreshToken(user);
    }

    @Test
    void login_callMapToUserDTO() {
        // GIVEN
        final var loginRequest = LoginRequest.builder()
                .username("test_userName")
                .password("testPass")
                .build();
        final var user = new User().setId(UUID.randomUUID());
        when(mockUserDetailsService.findByUsername(any())).thenReturn(Mono.just(user));
        when(mockPasswordEncoder.matches(any(), any())).thenReturn(true);
        when(mockRefreshTokenService.createRefreshToken(any())).thenReturn(Mono.empty());

        // WHEN
        final var result = service.login(loginRequest);

        // THEN
        StepVerifier.create(result).verifyComplete();
        verify(mockUserMapper).mapToUserDTO(user);
    }

    @Test
    void login_checkResult() {
        // GIVEN
        final var loginRequest = LoginRequest.builder().build();
        when(mockUserDetailsService.findByUsername(any())).thenReturn(Mono.just(new User()));
        when(mockPasswordEncoder.matches(any(), any())).thenReturn(true);

        final var userDTO = UserDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000001")).build();
        when(mockUserMapper.mapToUserDTO(any())).thenReturn(userDTO);
        when(mockJwtTokenService.generateAccessToken(any())).thenReturn("generatedAccessToken");
        when(mockRefreshTokenService.createRefreshToken(any())).thenReturn(Mono.just("generatedRefreshToken"));

        // WHEN
        final var result = service.login(loginRequest);

        // THEN
        StepVerifier.create(result)
                .assertNext(loginResponse -> {
                    assertThat(loginResponse.getAccessToken()).isEqualTo("generatedAccessToken");
                    assertThat(loginResponse.getRefreshToken()).isEqualTo("generatedRefreshToken");
                    assertThat(loginResponse.getUser()).isSameAs(userDTO);
                })
                .verifyComplete();
    }

}
