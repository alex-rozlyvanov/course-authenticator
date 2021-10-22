package com.goals.course.authenticator.service.implementation;

import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dao.repository.UserRepository;
import com.goals.course.authenticator.dto.SignUpRequest;
import com.goals.course.authenticator.dto.UserDTO;
import com.goals.course.authenticator.mapper.UserMapper;
import com.goals.course.authenticator.service.JwtTokenService;
import com.goals.course.authenticator.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SignUpServiceImplTest {
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private UserMapper mockUserMapper;
    @Mock
    private PasswordEncoder mockPasswordEncoder;
    @Mock
    private JwtTokenService mockJwtTokenService;
    @Mock
    private RefreshTokenService mockRefreshTokenService;

    @InjectMocks
    private SignUpServiceImpl service;

    @Test
    void singUp_callUserMapper_mapToUser() {
        // GIVEN
        final var signUpRequest = buildValidSignUpRequest();
        when(mockUserMapper.mapToUser(any())).thenReturn(new User());

        // WHEN
        service.signUp(signUpRequest);

        // THEN
        verify(mockUserMapper).mapToUser(signUpRequest);
    }

    @Test
    void singUp_callPasswordEncoder_encode() {
        // GIVEN
        final var signUpRequest = buildValidSignUpRequest();
        when(mockUserMapper.mapToUser(any())).thenReturn(new User());

        // WHEN
        service.signUp(signUpRequest);

        // THEN
        verify(mockPasswordEncoder).encode(signUpRequest.getPassword());
    }

    @Test
    void singUp_callUserRepository_save() {
        // GIVEN
        final var mappedUser = new User().setUsername("test234");
        when(mockUserMapper.mapToUser(any())).thenReturn(mappedUser);
        when(mockPasswordEncoder.encode(any())).thenReturn("$encodedPass$");

        // WHEN
        service.signUp(buildValidSignUpRequest());

        // THEN
        final var captor = ArgumentCaptor.forClass(User.class);
        verify(mockUserRepository).save(captor.capture());

        final var user = captor.getValue();
        assertThat(user).isSameAs(mappedUser);
        assertThat(user.getPassword()).isSameAs("$encodedPass$");
        assertTrue(user.isEnabled());
    }

    @Test
    void singUp_callMapToUserDTO() {
        // GIVEN
        final var savedUser = new User().setUsername("test234");
        when(mockUserMapper.mapToUser(any())).thenReturn(new User());
        when(mockUserRepository.save(any())).thenReturn(savedUser);

        // WHEN
        service.signUp(buildValidSignUpRequest());

        // THEN
        verify(mockUserMapper).mapToUserDTO(savedUser);
    }

    @Test
    void singUp_callGenerateAccessToken() {
        // GIVEN
        final var savedUser = new User().setUsername("test234");
        when(mockUserMapper.mapToUser(any())).thenReturn(new User());
        when(mockUserRepository.save(any())).thenReturn(savedUser);

        // WHEN
        service.signUp(buildValidSignUpRequest());

        // THEN
        verify(mockJwtTokenService).generateAccessToken(savedUser);
    }

    @Test
    void singUp_callCreateRefreshToken() {
        // GIVEN
        final var savedUser = new User().setUsername("test234");
        when(mockUserMapper.mapToUser(any())).thenReturn(new User());
        when(mockUserRepository.save(any())).thenReturn(savedUser);

        // WHEN
        service.signUp(buildValidSignUpRequest());

        // THEN
        verify(mockRefreshTokenService).createRefreshToken(savedUser);
    }

    @Test
    void singUp_checkResult() {
        // GIVEN
        final var signUpRequest = buildValidSignUpRequest();
        when(mockUserMapper.mapToUser(any())).thenReturn(new User());

        final var mappedUserDTO = UserDTO.builder().build();
        when(mockUserMapper.mapToUserDTO(any())).thenReturn(mappedUserDTO);
        when(mockJwtTokenService.generateAccessToken(any())).thenReturn("access_token");
        when(mockRefreshTokenService.createRefreshToken(any())).thenReturn("refresh_token");

        // WHEN
        final var result = service.signUp(signUpRequest);

        // THEN
        assertThat(result.getUser()).isSameAs(mappedUserDTO);
        assertThat(result.getAccessToken()).isEqualTo("access_token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh_token");
    }

    private SignUpRequest buildValidSignUpRequest() {
        return SignUpRequest.builder()
                .password("UniqueP@ssw0rd")
                .username("testUser")
                .firstName("testFirstName")
                .lastName("testLastName")
                .build();
    }

}
