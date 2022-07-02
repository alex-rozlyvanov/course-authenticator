package com.goals.course.authenticator.dao.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@Accessors(chain = true)
@Table("refresh_token")
public class RefreshToken {
    @Id
    @Column
    private UUID id;

    @Column("user_id")
    private User user;

    @Column
    private String token;

    @Column("expiry_date")
    private Instant expiryDate;
}
