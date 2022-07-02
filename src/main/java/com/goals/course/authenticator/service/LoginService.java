package com.goals.course.authenticator.service;

import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dto.LoginRequest;
import com.goals.course.authenticator.dto.LoginResponse;
import com.goals.course.authenticator.dto.UserDTO;
import com.goals.course.authenticator.exception.UserNotFoundException;
import com.goals.course.authenticator.mapper.UserMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@AllArgsConstructor
public class LoginService {

    private final JwtTokenService jwtTokenService;
    private final UserMapper userMapper;
    private final RefreshTokenService refreshTokenService;
    private final ReactiveUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Mono<LoginResponse> login(final LoginRequest request) throws BadCredentialsException {
        log.info("Login '{}' ", request.getUsername());
        return authenticate(request)
                .flatMap(this::buildLoginResponse);
    }

    private Mono<User> authenticate(final LoginRequest request) {
        return userDetailsService.findByUsername(request.getUsername())
                .map(userDetails -> validatePassword(request, userDetails))
                .switchIfEmpty(Mono.error(() -> new UserNotFoundException("User not found by username '%s'".formatted(request.getUsername()))));
    }

    private User validatePassword(final LoginRequest request, final UserDetails userDetails) {
        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            log.debug("Failed to authenticate since password does not match stored value");
            throw new BadCredentialsException("Bad credentials");
        }

        return (User) userDetails;
    }

    private Mono<LoginResponse> buildLoginResponse(final User user) {
        final var accessToken = jwtTokenService.generateAccessToken(user);
        final var userDTO = userMapper.mapToUserDTO(user);
        final var refreshTokenMono = refreshTokenService.createRefreshToken(user);

        return refreshTokenMono.map(refreshToken -> buildLoginResponse(accessToken, userDTO, refreshToken));
    }

    private LoginResponse buildLoginResponse(final String accessToken,
                                             final UserDTO userDTO,
                                             final String refreshToken) {
        return LoginResponse.builder()
                .user(userDTO)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
