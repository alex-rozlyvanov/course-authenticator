package com.goals.course.authenticator.dao.mapper;

import com.goals.course.authenticator.dao.entity.Role;
import io.r2dbc.spi.Row;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

@Component
public class RoleDaoMapper {

    public Optional<Role> toRole(final Row row) {
        final var id = row.get("r_id", UUID.class);

        if (isNull(id)) {
            return Optional.empty();
        }

        return Optional.of(
                new Role()
                        .setId(id)
                        .setTitle(row.get("r_title", String.class))
        );
    }

}
