package com.goals.course.authenticator.service.implementation;

import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dao.repository.UserRepository;
import com.goals.course.authenticator.dto.LoginResponse;
import com.goals.course.authenticator.dto.SignUpRequest;
import com.goals.course.authenticator.mapper.UserMapper;
import com.goals.course.authenticator.service.JwtTokenService;
import com.goals.course.authenticator.service.RefreshTokenService;
import com.goals.course.authenticator.service.SignUpService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SignUpServiceImpl implements SignUpService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final RefreshTokenService refreshTokenService;

    @Override
    public LoginResponse signUp(final SignUpRequest request) {
        final var savedUser = mapAndSaveNewUser(request);
        final var userDTO = userMapper.mapToUserDTO(savedUser);
        final var accessToken = jwtTokenService.generateAccessToken(savedUser);
        final var refreshToken = refreshTokenService.createRefreshToken(savedUser);

        return LoginResponse.builder()
                .user(userDTO)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private User mapAndSaveNewUser(final SignUpRequest request) {
        final var user = userMapper.mapToUser(request)
                .setEnabled(true)
                .setPassword(passwordEncoder.encode(request.getPassword()));
        return userRepository.save(user);
    }
}
