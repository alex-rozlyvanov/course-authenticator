package com.goals.course.authenticator.service;

import com.goals.course.authenticator.dao.entity.RefreshToken;
import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dao.repository.RefreshTokenRepository;
import com.goals.course.authenticator.dto.TokenRefreshResponse;
import com.goals.course.authenticator.exception.TokenRefreshException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenService jwtTokenService;
    private final Clock clock;

    @Transactional
    public Mono<Boolean> verifyExpiration(final RefreshToken token) {
        log.info("Verifying token expiration");
        if (token.getExpiryDate().isBefore(Instant.now())) {
            return refreshTokenRepository.delete(token)
                    .flatMap(r -> Mono.error(new TokenRefreshException("Refresh token was expired. Please make a new signin request")));
        }
        return Mono.just(true);
    }

    @Transactional
    public Mono<TokenRefreshResponse> refreshToken(final String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .flatMap(this::refreshToken)
                .switchIfEmpty(Mono.error(() -> new TokenRefreshException("Refresh token is not valid!")));
    }

    private Mono<TokenRefreshResponse> refreshToken(final RefreshToken token) {
        return verifyExpiration(token)
                .flatMap(r -> {
                    final var user = token.getUser();
                    final var accessToken = jwtTokenService.generateAccessToken(user);

                    return createRefreshToken(user)
                            .map(newRefreshToken -> TokenRefreshResponse.builder()
                                    .accessToken(accessToken)
                                    .refreshToken(newRefreshToken)
                                    .build());
                });
    }

    @Transactional
    public Mono<String> createRefreshToken(final User user) {
        log.info("Creating refresh token for user '{}'", user.getId());
        final var refreshToken = new RefreshToken()
                .setUser(user)
                .setExpiryDate(clock.instant().plusMillis(jwtTokenService.getRefreshTokenExpirationMs()))
                .setToken(jwtTokenService.generateRefreshToken(user));

        return refreshTokenRepository.deleteByUserId(user.getId())
                .flatMap(r -> refreshTokenRepository.save(refreshToken))
                .map(RefreshToken::getToken);
    }

    @Transactional
    public Mono<Integer> deleteRefreshTokenByUserId(final UUID userId) {
        log.info("Deleting refresh token for user '{}'", userId);
        return refreshTokenRepository.deleteByUserId(userId);
    }

}
