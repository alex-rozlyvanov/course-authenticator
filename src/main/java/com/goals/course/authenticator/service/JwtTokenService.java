package com.goals.course.authenticator.service;

import com.goals.course.authenticator.dao.entity.User;

import java.util.Date;

public interface JwtTokenService {

    String generateAccessToken(final User user);

    String getUserId(final String token);

    String getUsername(final String token);

    Date getExpirationDate(final String token);

    boolean validate(final String token);

    String generateRefreshToken(final User user);

    long getRefreshTokenExpirationMs();

}
