package com.goals.course.authenticator.dao.mapper;

import com.goals.course.authenticator.dao.entity.User;
import io.r2dbc.spi.Row;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class UserDaoMapper {

    private final RoleDaoMapper roleDaoMapper;

    public User toUser(final Row row) {
        final var user = new User()
                .setId(row.get("u_id", UUID.class))
                .setUsername(row.get("u_username", String.class))
                .setPassword(row.get("u_password", String.class))
                .setFirstName(row.get("u_first_name", String.class))
                .setLastName(row.get("u_last_name", String.class))
                .setEnabled(Boolean.TRUE.equals(row.get("u_enabled", Boolean.class)));

        addRolesIfExists(row, user);

        return user;
    }

    private void addRolesIfExists(Row row, User user) {
        try {
            roleDaoMapper.toRole(row)
                    .ifPresent(role -> user.setRoles(List.of(role)));
        } catch (IllegalArgumentException e) {
            // role not fetched, columns not found
        }
    }

    public User mergeRoles(final User user1, final User user2) {
        final var roles = Stream.concat(
                        user1.getRoles().stream(),
                        user2.getRoles().stream()
                )
                .toList();
        return user1.setRoles(roles);
    }
}
