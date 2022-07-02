package com.goals.course.authenticator.dao.repository;

import com.goals.course.authenticator.dao.entity.User;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository {

    String SELECT_FIELDS = """
            u.id as u_id,\
            u.username as u_username,\
            u.password as u_password,\
            u.first_name as u_first_name,\
            u.last_name as u_last_name,\
            u.enabled as u_enabled\
            """;

    Mono<User> findByUsername(final String username);

    Mono<User> findById(final UUID id);

    Mono<User> save(final User user);
}
