package com.goals.course.authenticator.service.implementation;

import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dto.LoginRequest;
import com.goals.course.authenticator.dto.UserDTO;
import com.goals.course.authenticator.mapper.UserMapper;
import com.goals.course.authenticator.service.JwtTokenService;
import com.goals.course.authenticator.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {
    @Mock
    private UserMapper mockUserMapper;
    @Mock
    private AuthenticationManager mockAuthenticationManager;
    @Mock
    private JwtTokenService mockJwtTokenService;
    @Mock
    private RefreshTokenService mockRefreshTokenService;
    @InjectMocks
    private LoginServiceImpl service;

    @Test
    void login_callAuthenticate() {
        // GIVEN
        final var loginRequest = LoginRequest.builder()
                .username("test_userName")
                .password("testPass")
                .build();

        when(mockAuthenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));

        // WHEN
        service.login(loginRequest);

        // THEN
        final var expected = new UsernamePasswordAuthenticationToken("test_userName", "testPass");
        verify(mockAuthenticationManager).authenticate(expected);
    }

    @Test
    void login_callGenerateAccessToken() {
        // GIVEN
        final var loginRequest = LoginRequest.builder()
                .username("test_userName")
                .password("testPass")
                .build();
        final var user = new User().setId(UUID.randomUUID());
        final var mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getPrincipal()).thenReturn(user);
        when(mockAuthenticationManager.authenticate(any())).thenReturn(mockAuthentication);

        // WHEN
        service.login(loginRequest);

        // THEN
        verify(mockJwtTokenService).generateAccessToken(user);
    }

    @Test
    void login_callGenerateRefreshToken() {
        // GIVEN
        final var loginRequest = LoginRequest.builder()
                .username("test_userName")
                .password("testPass")
                .build();
        final var user = new User().setId(UUID.randomUUID());
        final var mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getPrincipal()).thenReturn(user);
        when(mockAuthenticationManager.authenticate(any())).thenReturn(mockAuthentication);

        // WHEN
        service.login(loginRequest);

        // THEN
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
        final var mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getPrincipal()).thenReturn(user);
        when(mockAuthenticationManager.authenticate(any())).thenReturn(mockAuthentication);

        // WHEN
        service.login(loginRequest);

        // THEN
        verify(mockUserMapper).mapToUserDTO(user);
    }

    @Test
    void login_checkResult() {
        // GIVEN
        final var loginRequest = LoginRequest.builder().build();
        when(mockAuthenticationManager.authenticate(any())).thenReturn(mock(Authentication.class));

        final var userDTO = UserDTO.builder().id(UUID.fromString("00000000-0000-0000-0000-000000000001")).build();
        when(mockUserMapper.mapToUserDTO(any())).thenReturn(userDTO);
        when(mockJwtTokenService.generateAccessToken(any())).thenReturn("generatedAccessToken");
        when(mockRefreshTokenService.createRefreshToken(any())).thenReturn("generatedRefreshToken");

        // WHEN
        final var result = service.login(loginRequest);

        // THEN
        assertThat(result.getAccessToken()).isEqualTo("generatedAccessToken");
        assertThat(result.getRefreshToken()).isEqualTo("generatedRefreshToken");
        assertThat(result.getUser()).isSameAs(userDTO);
    }

}
