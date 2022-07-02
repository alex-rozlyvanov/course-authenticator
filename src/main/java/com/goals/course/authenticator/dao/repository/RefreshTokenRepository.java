package com.goals.course.authenticator.dao.repository;

import com.goals.course.authenticator.dao.entity.RefreshToken;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RefreshTokenRepository {
    String SELECT_FIELDS = """
            rt.id as rt_id,\
            rt.token as rt_token,\
            rt.expiry_date as rt_expiry_date\
            """;

    Mono<RefreshToken> findByToken(final String token);

    Mono<Integer> deleteByUserId(final UUID user);

    Mono<Integer> delete(final RefreshToken token);

    Mono<RefreshToken> save(final RefreshToken refreshToken);
}
