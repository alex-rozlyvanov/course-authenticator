package com.goals.course.authenticator.service.implementation;

import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dto.LoginRequest;
import com.goals.course.authenticator.dto.LoginResponse;
import com.goals.course.authenticator.mapper.UserMapper;
import com.goals.course.authenticator.service.JwtTokenService;
import com.goals.course.authenticator.service.LoginService;
import com.goals.course.authenticator.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final UserMapper userMapper;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public LoginResponse login(final LoginRequest request) throws BadCredentialsException {
        final var user = authenticate(request);
        final var accessToken = jwtTokenService.generateAccessToken(user);
        final var refreshToken = refreshTokenService.createRefreshToken(user);
        final var userDTO = userMapper.mapToUserDTO(user);

        return LoginResponse.builder()
                .user(userDTO)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private User authenticate(final LoginRequest request) {
        final var authentication = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        final var authenticate = authenticationManager.authenticate(authentication);

        return (User) authenticate.getPrincipal();
    }
}
