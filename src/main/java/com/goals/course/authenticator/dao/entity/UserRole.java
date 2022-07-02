package com.goals.course.authenticator.dao.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Accessors(chain = true)
@Table("users_roles")
public class UserRole {

    @Id
    private UUID id;

    @Column("user_id")
    private UUID userId;

    @Column
    private UUID roleId;

}
