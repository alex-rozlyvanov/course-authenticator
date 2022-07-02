package com.goals.course.authenticator.dao.repository;

import com.goals.course.authenticator.dao.entity.RefreshToken;
import com.goals.course.authenticator.dao.mapper.RefreshTokenDaoMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Repository
@AllArgsConstructor
public class RefreshTokenPostgresRepository implements RefreshTokenRepository {

    private final DatabaseClient client;
    private final RefreshTokenDaoMapper refreshTokenDaoMapper;

    @Override
    public Mono<RefreshToken> findByToken(final String token) {
        final var query = """
                SELECT %s, %s
                FROM refresh_token rt
                INNER JOIN users u on rt.user_id = u.id
                WHERE token = :token
                """.formatted(SELECT_FIELDS, UserRepository.SELECT_FIELDS);

        return client.sql(query)
                .bind("token", token)
                .map(refreshTokenDaoMapper::toRefreshToken)
                .one();
    }

    @Override
    public Mono<Integer> deleteByUserId(final UUID userId) {
        final var query = """
                DELETE FROM refresh_token WHERE user_id = :user_id
                """;

        return client.sql(query)
                .bind("user_id", userId)
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Mono<Integer> delete(final RefreshToken token) {
        final var query = """
                DELETE FROM refresh_token WHERE id = :id
                """;

        return client.sql(query)
                .bind("id", token.getId())
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Mono<RefreshToken> save(final RefreshToken refreshToken) {
        final var query = """
                INSERT INTO refresh_token (user_id, token, expiry_date)
                VALUES (:user_id, :token, :expiryDate)
                RETURNING *
                """;

        return client.sql(query)
                .bind("user_id", refreshToken.getUser().getId())
                .bind("token", refreshToken.getToken())
                .bind("expiryDate", refreshToken.getExpiryDate())
                .map(row -> refreshToken.setId(row.get("id", UUID.class)))
                .one();
    }
}
