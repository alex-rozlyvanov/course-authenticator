package com.goals.course.authenticator.dao.repository;

import com.goals.course.authenticator.dao.entity.Role;
import com.goals.course.authenticator.dao.entity.User;
import com.goals.course.authenticator.dao.entity.UserRole;
import com.goals.course.authenticator.dao.mapper.UserDaoMapper;
import lombok.AllArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Function;

import static java.util.Objects.isNull;

@Repository
@AllArgsConstructor
public class UserPostgresRepository implements UserRepository {

    private final DatabaseClient client;
    private final UserDaoMapper userDaoMapper;
    private final UserRoleRepository userRoleRepository;

    @Override
    public Mono<User> findByUsername(final String username) {
        final var query = """
                SELECT %s, %s
                FROM users u
                INNER JOIN users_roles ur on u.id = ur.user_id
                INNER JOIN roles r ON ur.role_id = r.id
                WHERE u.username = :username;
                """.formatted(SELECT_FIELDS, RoleRepository.SELECT_FIELDS);

        return client.sql(query)
                .bind("username", username)
                .map(userDaoMapper::toUser)
                .all()
                .reduce(userDaoMapper::mergeRoles);
    }

    @Override
    public Mono<User> findById(final UUID id) {
        final var query = """
                SELECT %s, %s
                FROM users u
                FULL OUTER JOIN users_roles ur ON u.id = ur.user_id
                FULL OUTER JOIN roles r ON ur.role_id = r.id
                WHERE u.id = :id;
                """.formatted(SELECT_FIELDS, RoleRepository.SELECT_FIELDS);

        return client.sql(query)
                .bind("id", id)
                .map(userDaoMapper::toUser)
                .all()
                .reduce(userDaoMapper::mergeRoles);
    }

    @Override
    @Transactional
    public Mono<User> save(final User user) {
        if (isNull(user.getId())) {
            return create(user);
        }
        return update(user);
    }

    private Mono<User> update(final User user) {
        final var query = """
                UPDATE users as u
                SET
                 password = :password,
                 first_name = :first_name,
                 last_name = :last_name,
                 enabled = :enabled
                WHERE u.id = :id
                RETURNING %s
                """.formatted(SELECT_FIELDS);

        return client.sql(query)
                .bind("id", user.getId())
                .bind("password", user.getPassword())
                .bind("first_name", user.getFirstName())
                .bind("last_name", user.getLastName())
                .bind("enabled", user.isEnabled())
                .map(userDaoMapper::toUser)
                .one()
                .flatMap(updateRoles(user));
    }

    private Mono<User> create(User user) {
        final var query = """
                INSERT INTO users as u (username, password, first_name, last_name, enabled)
                VALUES (:username, :password, :first_name, :last_name, :enabled)
                RETURNING %s
                """.formatted(SELECT_FIELDS);

        return client.sql(query)
                .bind("username", user.getUsername())
                .bind("password", user.getPassword())
                .bind("first_name", user.getFirstName())
                .bind("last_name", user.getLastName())
                .bind("enabled", user.isEnabled())
                .map(userDaoMapper::toUser)
                .one()
                .flatMap(saveRoles(user));
    }

    private Function<User, Mono<User>> saveRoles(final User user) {
        return savedUser -> {
            final var roles = user.getRoles().stream()
                    .map(role -> mapToUserRole(savedUser, role))
                    .toList();

            return userRoleRepository.saveAll(roles)
                    .collectList()
                    .map(savedRoles -> savedUser.setRoles(user.getRoles()));
        };
    }

    private Function<User, Mono<User>> updateRoles(final User user) {
        return savedUser -> {
            final var roles = user.getRoles().stream()
                    .map(role -> mapToUserRole(savedUser, role))
                    .toList();

            return userRoleRepository.saveAll(roles)
                    .collectList()
                    .map(savedRoles -> savedUser.setRoles(user.getRoles()));
        };
    }

    private UserRole mapToUserRole(User savedUser, Role role) {
        return new UserRole().setUserId(savedUser.getId()).setRoleId(role.getId());
    }


}
