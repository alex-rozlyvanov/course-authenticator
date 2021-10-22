package com.goals.course.authenticator.service;

import com.goals.course.authenticator.dao.entity.RefreshToken;
import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dto.TokenRefreshResponse;

import java.util.UUID;

public interface RefreshTokenService {

    RefreshToken verifyExpiration(final RefreshToken token);

    TokenRefreshResponse refreshToken(final String refreshToken);

    String createRefreshToken(final User user);

    void deleteRefreshTokenByUserId(final UUID userId);
}
