package com.goals.course.authenticator.dao.repository;

import com.goals.course.authenticator.dao.entity.UserRole;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRoleRepository extends ReactiveSortingRepository<UserRole, UUID> {
    String SELECT_FIELDS = """
            ur.id as ur_id,\
            ur.user_id as ur_user_id,\
            ur.role_id as ur_role_id\
            """;
}
