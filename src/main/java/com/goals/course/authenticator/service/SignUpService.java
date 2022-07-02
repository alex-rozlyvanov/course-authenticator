package com.goals.course.authenticator.service;

import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dao.repository.UserRepository;
import com.goals.course.authenticator.dto.LoginResponse;
import com.goals.course.authenticator.dto.SignUpRequest;
import com.goals.course.authenticator.dto.UserDTO;
import com.goals.course.authenticator.exception.UserAlreadyExistsException;
import com.goals.course.authenticator.mapper.UserMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Slf4j
@Service
@AllArgsConstructor
public class SignUpService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;

    public Mono<LoginResponse> signUp(final SignUpRequest request) {
        return verifyUserExists(request)
                .flatMap(userRepository::save)
                .flatMap(savedUser -> {
                    final var userDTO = userMapper.mapToUserDTO(savedUser);
                    final var accessToken = jwtTokenService.generateAccessToken(savedUser);
                    final var refreshTokenMono = refreshTokenService.createRefreshToken(savedUser);

                    return refreshTokenMono.map(buildLoginResponse(userDTO, accessToken));
                });
    }

    private Mono<User> verifyUserExists(final SignUpRequest request) {
        final var user = mapToUser(request);
        log.info("verifyUserExists '{}'", user);
        return userRepository.findByUsername(user.getUsername())
                .flatMap(u -> Mono.error(new UserAlreadyExistsException(user.getId())))
                .map(User.class::cast) // crutch
                .switchIfEmpty(Mono.just(user));
    }

    private User mapToUser(final SignUpRequest request) {
        return userMapper.mapToUser(request)
                .setEnabled(true)
                .setPassword(passwordEncoder.encode(request.getPassword()));
    }

    private Function<String, LoginResponse> buildLoginResponse(UserDTO userDTO, String accessToken) {
        return refreshToken -> LoginResponse.builder()
                .user(userDTO)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
