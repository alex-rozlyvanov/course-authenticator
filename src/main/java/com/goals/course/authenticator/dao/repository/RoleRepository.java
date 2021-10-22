package com.goals.course.authenticator.dao.repository;

import com.goals.course.authenticator.dao.entity.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoleRepository extends CrudRepository<Role, UUID> {
    List<Role> findAllById(final Iterable<UUID> ids);

    List<Role> findAll();
}
