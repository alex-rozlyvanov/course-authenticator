package com.goals.course.authenticator.dao.mapper;

import com.goals.course.authenticator.dao.entity.RefreshToken;
import io.r2dbc.spi.Row;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
@AllArgsConstructor
public class RefreshTokenDaoMapper {
    private final UserDaoMapper userDaoMapper;

    public RefreshToken toRefreshToken(final Row row) {
        return new RefreshToken()
                .setId(row.get("rt_id", UUID.class))
                .setToken(row.get("rt_token", String.class))
                .setUser(userDaoMapper.toUser(row))
                .setExpiryDate(row.get("rt_expiry_date", Instant.class));
    }
}
