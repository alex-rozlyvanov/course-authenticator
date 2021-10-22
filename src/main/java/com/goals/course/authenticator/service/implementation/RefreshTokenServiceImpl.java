package com.goals.course.authenticator.service.implementation;

import com.goals.course.authenticator.dao.entity.RefreshToken;
import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dao.repository.RefreshTokenRepository;
import com.goals.course.authenticator.dto.TokenRefreshResponse;
import com.goals.course.authenticator.exception.TokenRefreshException;
import com.goals.course.authenticator.service.InstantWrapper;
import com.goals.course.authenticator.service.JwtTokenService;
import com.goals.course.authenticator.service.RefreshTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenService jwtTokenService;
    private final InstantWrapper instantWrapper;

    @Override
    @Transactional
    public RefreshToken verifyExpiration(final RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Override
    @Transactional
    public TokenRefreshResponse refreshToken(final String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(this::refreshToken)
                .orElseThrow(() -> new TokenRefreshException("Refresh token is not valid!"));
    }

    private TokenRefreshResponse refreshToken(final User user) {
        final var accessToken = jwtTokenService.generateAccessToken(user);
        final var newRefreshToken = createRefreshToken(user);

        return TokenRefreshResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Override
    @Transactional
    public String createRefreshToken(final User user) {
        log.info("Creating refresh token for user {}", user.getId());
        final var refreshToken = new RefreshToken()
                .setUser(user)
                .setExpiryDate(instantWrapper.now().plusMillis(jwtTokenService.getRefreshTokenExpirationMs()))
                .setToken(jwtTokenService.generateRefreshToken(user));

        refreshTokenRepository.deleteByUserId(user.getId());
        refreshTokenRepository.flush();
        return refreshTokenRepository.save(refreshToken).getToken();
    }

    @Override
    @Transactional
    public void deleteRefreshTokenByUserId(final UUID userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

}
