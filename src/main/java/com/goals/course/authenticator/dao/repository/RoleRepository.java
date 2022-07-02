package com.goals.course.authenticator.dao.repository;

import com.goals.course.authenticator.dao.entity.Role;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleRepository extends ReactiveSortingRepository<Role, UUID> {
    String SELECT_FIELDS = """
            r.id as r_id,\
            r.title as r_title\
            """;
}
